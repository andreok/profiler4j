/*
 * Copyright 2006 Antonio S. R. Gomes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.profiler4j.console;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import net.sf.profiler4j.agent.AgentConstants;
import net.sf.profiler4j.console.client.Client;
import net.sf.profiler4j.console.client.ClientException;
import net.sf.profiler4j.console.client.ProgressCallback;
import net.sf.profiler4j.console.client.Snapshot;
import net.sf.profiler4j.console.util.export.FilenameGenerator;
import net.sf.profiler4j.console.util.export.ImageFileWriter;
import net.sf.profiler4j.console.util.export.ToPng;
import net.sf.profiler4j.console.util.task.LongTask;
import net.sf.profiler4j.console.util.task.LongTaskExecutorDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Application controller.
 * 
 * @author Antonio S. R. Gomes
 */
public class Console {

    private static final String MSG_FAILED_EXPORTING_CALLGRAPH = "Failed exporting the callgraph: ";

    private static final String MSG_FAILURE_TO_GENERATE_FILENAME = "Could not generate a filename from the pattern, callgraph exporting failed: ";

    private static final Log log = LogFactory.getLog(Console.class);

    private Client client;
    private Project project;
    private MainFrame mainFrame;
    private StatusBarPanel statusBar;
    private File lastDir;
    private Prefs prefs;
    private Document tipDoc;

    private Timer memoryPanelTimer = new Timer(1000, new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (client.isConnected()) {
                try {
                    sendEvent(AppEventType.GOT_MEMORY_INFO, client.getMemoryInfo());
                } catch (ClientException re) {
                    error("Could not refresh memory info", re);
                    memoryPanelTimer.stop();
                }
            }
        };
    });

    private List<AppEventListener> listeners = new ArrayList<AppEventListener>();

    private FileFilter projectFilter = new FileFilter() {
        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(".p4j");
        }
        public String getDescription() {
            return "*.p4j (Profiling Project)";
        }
    };

    private Console(Prefs prefs) {
        this.prefs = prefs;
        project = new Project();
        lastDir = new File(System.getProperty("user.home"));
        client = new Client();
        SAXBuilder builder = new SAXBuilder();
        try {
            tipDoc = builder.build(getClass().getResource("tips.xml"));
        } catch (JDOMException e) {
            error("Could not read tips (XML Error)", e);
        } catch (IOException e) {
            error("Could not read tips (I/O Error)", e);
        }

    }

    /**
     * @return Returns the tipDoc.
     */
    public Document getTipDoc() {
        return this.tipDoc;
    }

    /**
     * @return Returns the prefs.
     */
    public Prefs getPrefs() {
        return this.prefs;
    }

    public void error(String message) {
        showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void error(String message, Throwable t) {
        showMessageDialog(mainFrame,
                          message + "\nMessage: " + t.getMessage(),
                          "Error",
                          JOptionPane.ERROR_MESSAGE);
    }

    public void connect() {
        LongTask t = new LongTask() {
            public void executeInBackground() throws Exception {
                setTaskMessage("Establishing connection with remote JVM...");
                client.connect(project.getHostname(), project.getPort());
                setTaskMessage("Activating profiling rules...");
                client.applyRules(project.formtRules(),
                                  project.formatOptions(),
                                  new ProgressCallback() {
                                      private int max;
                                      public void operationStarted(int amount) {
                                          max = amount;
                                      }
                                      public void update(int value) {
                                          setTaskProgress((value * 100) / max);
                                          setTaskMessage("Activating profiling rules... (class "
                                                  + value + " of " + max + ")");
                                      }
                                  });
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        sendEvent(AppEventType.CONNECTED);
                        memoryPanelTimer.start();
                    }
                });

            }
        };
        runInBackground(t);
    }

    public void disconnect() {
        if (!client.isConnected()) {
            return;
        }
        int ret = JOptionPane
            .showConfirmDialog(mainFrame,
                               "Do you want to undo any changes made to classes before\n"
                                       + "you disconnect?\n\n"
                                       + "(This requires some time to complete but leaves\n"
                                       + "the remote JVM running at 100% of the original speed)",
                               "Disconnection",
                               JOptionPane.YES_NO_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION) {
            return;
        }
        final boolean undoChanges = ret == JOptionPane.YES_OPTION;
        LongTask t = new LongTask() {
            public void executeInBackground() throws Exception {
                if (undoChanges) {
                    setTaskMessage("Undoing changes to classes...");
                    client.restoreClasses(new ProgressCallback() {
                        private int max;
                        public void operationStarted(int amount) {
                            max = amount;
                            update(0);
                        }
                        public void update(int value) {
                            setTaskProgress((value * 100) / max);
                            setTaskMessage("Undoing changes to classes... (class "
                                    + value + " of " + max + ")");
                        }
                    });
                }
            };
        };
        runInBackground(t);

        memoryPanelTimer.stop();
        sendEvent(AppEventType.TO_DISCONNECT);

        try {
            client.disconnect();
        } catch (ClientException e) {
            error("Connection was close with error: (" + e.getMessage() + ")", e);
        }

        sendEvent(AppEventType.DISCONNECTED);
    }

    /**
     * @return <code>true</code> if a new project was created (the user could have been
     *         cancelled)
     */
    public boolean newProject() {
        if (client.isConnected()) {
            int ret = JOptionPane.showConfirmDialog(mainFrame,
                                                    "Proceed and disconnect?",
                                                    "New Profiling Project",
                                                    JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        if (checkUnsavedChanges()) {
            return false;
        }
        if (client.isConnected()) {
            disconnect();
            if (client.isConnected()) {
                return false;
            }
        }
        Project p = new Project();
        ProjectDialog d = new ProjectDialog(mainFrame, this);
        if (d.edit(p)) {
            this.project = p;
            return true;
        }
        return false;
    }

    public void openProject() {

        if (client.isConnected()) {
            int ret = JOptionPane.showConfirmDialog(mainFrame,
                                                    "Proceed and disconnect?",
                                                    "Open Profiling Project",
                                                    JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.NO_OPTION) {
                return;
            }
        }

        if (checkUnsavedChanges()) {
            return;
        }

        if (client.isConnected()) {
            disconnect();
            if (client.isConnected()) {
                return;
            }
        }
        JFileChooser fc = new JFileChooser(lastDir);
        fc.addChoosableFileFilter(projectFilter);
        if (fc.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();
            SAXBuilder builder = new SAXBuilder();
            Document doc = null;
            try {
                doc = builder.build(selFile);
            } catch (JDOMException e) {
                error("XML Error", e);
            } catch (IOException e) {
                error("I/O Error", e);
            }
            if (doc != null) {
                Project p = new Project();
                Element el = doc.getRootElement();
                p.setHostname(el.getChildText("Host"));
                p.setPort(Integer.parseInt(el.getChildText("Port")));
                Element rulesEl = el.getChild("Rules");

                p.setAccess(Rule.AccessOption
                    .valueOf(rulesEl.getAttributeValue("access")));
                p.setBeanprops(Boolean.parseBoolean(rulesEl
                    .getAttributeValue("beanProps")));

                p.getRules().clear();
                for (Iterator i = rulesEl.getChildren("Rule").iterator(); i.hasNext();) {
                    Element r = (Element) i.next();
                    Rule rule = new Rule(r.getText(), Rule.Action.valueOf(r
                        .getAttributeValue("action")));
                    p.getRules().add(rule);
                }
                p.setFile(selFile);
                p.clearChanged();
                this.project = p;
                lastDir = selFile.getParentFile();
            }
        }

    }

    public void close() {
        if (checkUnsavedChanges()) {
            return;
        }
        if (client.isConnected()) {
            disconnect();
            if (client.isConnected()) {
                return;
            }
        }
    }

    /**
     * Saves the current project.
     * 
     * @param saveAs force the user to select a file name even
     * @return <code>true</code> if the user has cancelled (only in the case of save as)
     */
    public boolean saveProject(boolean saveAs) {
        if (project.getFile() == null || saveAs) {
            JFileChooser fc = new JFileChooser(lastDir);

            fc.setDialogTitle("Save Project As");

            fc.addChoosableFileFilter(projectFilter);
            if (fc.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                if (!f.getName().endsWith(".p4j")) {
                    f = new File(f.getAbsolutePath() + ".p4j");
                }
                project.setFile(f);
            } else {
                return true;
            }
        }

        Element rootEl = new Element("Profiler4jProject");
        Document doc = new Document(rootEl);

        rootEl.addContent(new Element("Host").setText(project.getHostname()));
        rootEl.addContent(new Element("Port").setText(String.valueOf(project.getPort())));

        Element rulesEl = new Element("Rules");
        rootEl.addContent(rulesEl);

        rulesEl.setAttribute("access", project.getAccess().name());
        rulesEl.setAttribute("beanProps", String.valueOf(project.isBeanprops()));

        for (Rule rule : project.getRules()) {
            rulesEl.addContent(new Element("Rule")
                .setText(rule.getPattern())
                    .setAttribute("action", rule.getAction().name()));
        }

        try {
            FileWriter fw = new FileWriter(project.getFile());
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(doc, fw);
            fw.close();
            project.clearChanged();
        } catch (IOException e) {
            error("I/O Error", e);
        }
        return false;
    }

    public boolean editProject() {
        ProjectDialog d = new ProjectDialog(mainFrame, this);
        return d.edit(project);
    }

    public void applyRules() {
        int ret = JOptionPane
            .showConfirmDialog(mainFrame,
                               "Request activation of profiling rules now? (This may take some time)",
                               "Activate Profiling Rules",
                               JOptionPane.OK_CANCEL_OPTION);
        if (ret != JOptionPane.OK_OPTION) {
            return;
        }
        LongTask t = new LongTask() {
            public void executeInBackground() throws Exception {
                setTaskMessage("Activating profiling rules...");
                client.applyRules(project.formtRules(),
                                  project.formatOptions(),
                                  new ProgressCallback() {
                                      private int max;
                                      public void operationStarted(int amount) {
                                          max = amount;
                                          setTaskProgress(0);
                                      }
                                      public void update(int value) {
                                          setTaskProgress((value * 100) / max);
                                          setTaskMessage("Activating profiling rules... (class "
                                                  + value + " of " + max + ")");
                                      }
                                  });
            };
        };
        runInBackground(t);
        if (t.getError() == null) {
            sendEvent(AppEventType.RULES_APPLIED);
        }
    }

    public void takeSnapshot() {
        LongTask t = new LongTask() {
            public void executeInBackground() throws Exception {
                setTaskMessage("Retrieving snapshot...");
                setValue(client.getSnapshot());
            };
        };
        runInBackground(t);
        if (t.getError() == null) {
            Snapshot s = (Snapshot) t.getValue();
            sendEvent(AppEventType.SNAPSHOT, s);
            
            // In case the project settings specify this, dump a snapshot using the filename pattern
            // specified by the user.
            if (project.isExportAutomaticallyEnabled()) {
                try {
                    // Generate a new file to export the image.
                    FilenameGenerator generator = new FilenameGenerator(project.getExportPattern());
                    
                    // Zoom the call graph to max resolution.
                    CallGraphPanel panel = getMainFrame().getCallGraphPanel();
                    panel.applyNCut(mainFrame.getNcutSlider().getMaximum());
                    
                    new ImageFileWriter().writeFile(
                            panel, // The panel to draw.
                            new ToPng(),                        // The image creator, defining the image format.
                            generator.getValidFile()            // The file to write the image to.
                            );
                    
                    // Reduce the resolution to the original value.
                    panel.applyNCut(mainFrame.getNcutSlider().getValue());
                    
                } catch (FileNotFoundException exc) {
                    JOptionPane.showMessageDialog(getMainFrame(), MSG_FAILURE_TO_GENERATE_FILENAME + exc.getMessage());
                    // If the pattern is invalid, then disable further exporting.
                    project.setExportAutomaticallyEnabled(false);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(getMainFrame(), MSG_FAILED_EXPORTING_CALLGRAPH + exc.getMessage());
                    // If the pattern is invalid, then disable further exporting.
                    project.setExportAutomaticallyEnabled(false);
                }
            }
        }
    }

    /**
     * 
     * @return <code>true</code> if the user has cancelled
     */
    private boolean checkUnsavedChanges() {
        if (project.isChanged()) {
            int ret = JOptionPane
                .showConfirmDialog(mainFrame,
                                   "Project has unsaved changes? Save before exit?",
                                   "Unsaved Changes",
                                   JOptionPane.YES_NO_CANCEL_OPTION);
            if (ret == JOptionPane.CANCEL_OPTION) {
                return true;
            } else if (ret == JOptionPane.YES_OPTION) {
                return saveProject(false);
            }
        }
        return false;
    }

    public void exit() {

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            System.err.println("ERROR: could not flush preferences to disk");
            e.printStackTrace();
        }

        if (checkUnsavedChanges()) {
            return;
        }

        if (sendEvent(AppEventType.APP_CLOSING)) {
            return;
        }
        if (client.isConnected()) {
            disconnect();
            if (!client.isConnected()) {
                sendEvent(AppEventType.APP_CLOSED);
                System.exit(0);
            }
        } else {
            sendEvent(AppEventType.APP_CLOSED);
            System.exit(0);
        }
    }

    public void runInBackground(LongTask task) {
        memoryPanelTimer.stop();
        try {
            LongTaskExecutorDialog d = new LongTaskExecutorDialog(mainFrame);
            d.setLocationRelativeTo(mainFrame);
            d.runTask(task);
        } finally {
            memoryPanelTimer.restart();
        }
    }

    public void addListener(AppEventListener l) {
        listeners.add(l);
    }

    public void removeListener(AppEventListener l) {
        listeners.remove(l);
    }

    private boolean sendEvent(AppEventType evType) {
        return sendEvent(new AppEvent(evType));
    }

    private boolean sendEvent(AppEventType evType, Object arg) {
        return sendEvent(new AppEvent(evType, arg));
    }

    private boolean sendEvent(AppEvent ev) {
        for (AppEventListener l : listeners) {
            boolean vetoed = l.receiveEvent(ev);
            if (vetoed) {
                if (!ev.getType().isVetoable()) {
                    throw new IllegalArgumentException("AppEventType " + ev.getType()
                            + " is not vetoable");
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the project.
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return Returns the mainFrame.
     */
    public MainFrame getMainFrame() {
        return this.mainFrame;
    }

    /**
     * @param mainFrame The mainFrame to set.
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.statusBar = mainFrame.getStatusBarPanel();
    }

    public void setStatusMessage(String msg) {
        statusBar.setMessage(msg);
    }

    /**
     * @return Returns the client.
     */
    public Client getClient() {
        return this.client;
    }

    public static void main(String[] args) {

        System.out.println();
        System.out.println("+--------------------------------------------------------+");
        System.out.println("| Profiler4j-Fork Console "
                + String.format("%-36s", AgentConstants.VERSION) + "|");
        System.out.println("| Copyright 2006 Antonio S. R. Gomes                     |");
        System.out.println("| Copyright 2009 Murat Knecht                            |");
        System.out.println("| See LICENSE-2.0.txt for details                        |");
        System.out.println("+--------------------------------------------------------+");

        Prefs prefs = new Prefs();

        System.setProperty("swing.aatext", String.valueOf(prefs.isAntialiasing()));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore
        }
        final Console app = new Console(prefs);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame f = new MainFrame(app);
                app.setMainFrame(f);
                f.pack();
                f.setVisible(true);
            }
        });

    }

}
