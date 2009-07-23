/*
 * Copyright 2006 Antonio S. R. Gomes
 * Copyright 2009 Murat Knecht
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.rmi.server.ExportException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.profiler4j.agent.AgentConstants;
import net.sf.profiler4j.console.TreeBuilder.NodeInfo;
import net.sf.profiler4j.console.client.ClientException;
import net.sf.profiler4j.console.client.Snapshot;
import net.sf.profiler4j.console.util.export.ImageFileWriter;
import net.sf.profiler4j.console.util.export.ToPng;

public class MainFrame extends JFrame implements AppEventListener {

    private Console app;

    private static final long serialVersionUID = 1L;

    // @jve:decl-index=0:

    private JPanel jContentPane = null;
    private JButton connectButton = null;
    private JButton snapshotButton = null;
    private JButton resetButton = null;
    private JMenuBar jJMenuBar = null;
    private JMenu fileMenu = null;
    private JMenu exportMenu = null;
    private JMenuItem openMenuItem = null;
    private JMenuItem exportCallGraphMenuItem = null;

    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.toString().endsWith("-profiler4j.ser");
        }

        @Override
        public String getDescription() {
            return "Profiler4j Snapshots (*-profiler4j.ser)";
        }
    };

    private FileFilter imageExportFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isDirectory()
                || !f.exists()
                && (   f.getName().endsWith(".png")
                   );
        }

        @Override
        public String getDescription() {
            return "PNG Image (.png)";
        }
    };

    public MainFrame(Console app) {
        super();
        this.app = app;
        initialize();
        app.addListener(this);
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(629, 342);
        this.setPreferredSize(new Dimension(800, 600));
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass()
            .getResource("/net/sf/profiler4j/console/images/pill.png")));
        this.setJMenuBar(getJJMenuBar());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getJContentPane());
        this.setTitle("Profiler4j Console " + AgentConstants.VERSION);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {

                if (app.getPrefs().isShowTip()) {
                    TipDialog td = new TipDialog(MainFrame.this);
                    td.showTip(app);
                }
            }
            public void windowClosing(java.awt.event.WindowEvent e) {
                app.exit();
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.setBorder(javax.swing.BorderFactory
                .createEmptyBorder(4, 4, 4, 4));
            jContentPane.add(getTabbedPane(), BorderLayout.CENTER);
            jContentPane.add(getRemoteCommandPanel(), BorderLayout.NORTH);
            jContentPane.add(getStatusBarPanel(), BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    private String lastDir = System.getProperty("user.dir");
    private JTree tree = null;
    private JScrollPane treeScrollPane = null;
    private JScrollPane detailScrollPane = null;
    private JTextArea detailTextArea = null;
    private JToolBar remoteCommandPanel = null;
    private JMenu helpMenu = null;
    private JMenuItem aboutMenuItem = null;
    private JMenuItem exitMenuItem = null;
    private JTabbedPane tabbedPane = null;
    private JPanel callTreePanel = null;
    private JPanel callGraphTabPanel = null;
    private CallGraphPanel callGraphPanel = null;
    private JScrollPane jScrollPane = null;
    private JPanel jPanel1 = null;
    private JSlider ncutSlider = null;
    private JButton runGcButton = null;

    private MemoryPanel memoryMonitorPanel = null;

    private JButton applyRulesButton = null;

    private JButton editProjectButton = null;

    private ClassListPanel classListPanel = null;

    private JButton helpButton = null;

    private JButton openProjectButton = null;

    private JButton newProjectButton = null;

    private JButton saveProjectButton = null;

    private JButton saveProjectAsButton = null;

    private ThreadPanel threadPanel = null;

    private StatusBarPanel statusBarPanel = null;

    private JMenuItem showTipsMenuItem = null;

    private JMenuItem optionsMenuItem = null;

    /**
     * This method initializes jJMenuBar
     * 
     * @return javax.swing.JMenuBar
     */
    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add(getFileMenu());
            jJMenuBar.add(getExportMenu());
            jJMenuBar.add(getHelpMenu());
        }
        return jJMenuBar;
    }

    /**
     * This method initializes fileMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getOpenMenuItem());
            fileMenu.add(getOptionsMenuItem());
            fileMenu.add(getExitMenuItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes openMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getOpenMenuItem() {
        if (openMenuItem == null) {
            openMenuItem = new JMenuItem();
            openMenuItem.setText("Open dump file...");
            openMenuItem.setEnabled(false);
            openMenuItem.setVisible(false);
            openMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    open();
                }
            });
        }
        return openMenuItem;
    }

    /**
     * This method initializes exportMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getExportMenu() {
        if (exportMenu == null) {
            exportMenu = new JMenu();
            exportMenu.setText("Export");
            exportMenu.add(getExportCallgraphMenuItem());
        }
        return exportMenu;
    }

    /**
     * This method initializes openMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportCallgraphMenuItem() {
        if (exportCallGraphMenuItem == null) {
            exportCallGraphMenuItem = new JMenuItem();
            exportCallGraphMenuItem.setText("Call graph...");
            exportCallGraphMenuItem.setEnabled(true);
            exportCallGraphMenuItem.setVisible(true);
            exportCallGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    File imageFile = getImageFileFromUser();
                    if (null == imageFile) {
                        // User has aborted, do nothing.
                        return;
                    }
                    
                    try {
                        exportCallGraph(imageFile);
                    } catch(IOException exc) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Failed exporting the callgraph: " + exc.getMessage());
                    }
                }
            });
        }
        return exportCallGraphMenuItem;
    }
    
    private File getImageFileFromUser() {
        String dir = lastDir;
        JFileChooser fc = new JFileChooser(dir);
        fc.setFileFilter(imageExportFilter);
        
        if (JFileChooser.APPROVE_OPTION != fc.showSaveDialog(MainFrame.this)) {
            // user pressed cancel
            return null;
        }
        
        File imageFile = fc.getSelectedFile();
        lastDir = imageFile.getParent();
        
        return imageFile;
    }

    /**
     * This method initializes jTree
     * 
     * @return javax.swing.JTree
     */
    private JTree getTree() {
        if (tree == null) {
            tree = new JTree();
            tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
            tree.setPreferredSize(null);
            tree.setRowHeight(18);
            tree.setDoubleBuffered(true);
            tree.setRootVisible(true);
            tree.setToggleClickCount(2);
            tree.setEnabled(true);
            tree.setEditable(false);
            tree.setShowsRootHandles(true);
            tree.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    tree.getSelectionModel().addSelectionPath(path);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }
                    if (node.getUserObject() instanceof NodeInfo) {
                        NodeInfo info = (NodeInfo) node.getUserObject();
                        viewDetail(info);
                    }
                }
            });
            tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath()
                            .getLastPathComponent();
                    if (node != null) {
                        NodeInfo info = (NodeInfo) node.getUserObject();
                        viewDetail(info);
                    }
                }
            });
        }
        return tree;
    }

    /**
     * This method initializes jScrollPane1
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTreeScrollPane() {
        if (treeScrollPane == null) {
            treeScrollPane = new JScrollPane();
            treeScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(8, 8, 4, 8), BorderFactory
                .createLineBorder(Color.gray, 1)));
            treeScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));
            treeScrollPane.setVisible(true);
            treeScrollPane.setViewportView(getTree());
        }
        return treeScrollPane;
    }

    /**
     * This method initializes detailScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getDetailScrollPane() {
        if (detailScrollPane == null) {
            detailScrollPane = new JScrollPane();
            detailScrollPane.setPreferredSize(null);
            detailScrollPane.setViewportView(getDetailTextArea());
            detailScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(4, 8, 8, 8), BorderFactory
                .createLineBorder(Color.gray, 1)));
            detailScrollPane.setVisible(true);
        }
        return detailScrollPane;
    }

    /**
     * This method initializes jTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getDetailTextArea() {
        if (detailTextArea == null) {
            detailTextArea = new JTextArea();
            detailTextArea.setEditable(false);
            detailTextArea.setMargin(new java.awt.Insets(4, 4, 4, 4));
            detailTextArea.setRows(5);
            detailTextArea.setPreferredSize(null);
            detailTextArea.setEnabled(true);
            detailTextArea.setToolTipText("Details about the method selected above");
            detailTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN,
                    12));
        }
        return detailTextArea;
    }

    /**
     * This method initializes connectButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getConnectButton() {
        if (connectButton == null) {
            connectButton = new JButton();
            connectButton.setPreferredSize(new java.awt.Dimension(32, 32));
            connectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/connect.png")));

            connectButton.setFocusPainted(false);
            connectButton.setToolTipText("Connect to remote JVM");
            connectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (app.getClient().isConnected()) {
                        app.disconnect();
                    } else {
                        app.connect();
                    }
                }
            });
        }
        return connectButton;
    }

    /**
     * This method initializes snapshotButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSnapshotButton() {
        if (snapshotButton == null) {
            snapshotButton = new JButton();
            snapshotButton.setText("");
            snapshotButton.setPreferredSize(new java.awt.Dimension(32, 32));
            snapshotButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/camera.gif")));
            snapshotButton.setToolTipText("Take snaphot");
            snapshotButton.setFocusPainted(false);
            snapshotButton.setEnabled(false);
            snapshotButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.takeSnapshot();
                }
            });
        }
        return snapshotButton;
    }

    /**
     * This method initializes resetButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setText("");
            resetButton.setPreferredSize(new java.awt.Dimension(32, 32));
            resetButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/timeline_marker.png")));
            resetButton.setToolTipText("Reset counters");
            resetButton.setFocusPainted(false);
            resetButton.setEnabled(false);
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        app.getClient().reset();
                    } catch (ClientException e1) {
                        app.error("Could not reset stats", e1);
                    }
                }
            });
        }
        return resetButton;
    }

    /**
     * This method initializes commandPanel
     * 
     * @return javax.swing.JPanel
     */
    private JToolBar getRemoteCommandPanel() {
        if (remoteCommandPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            remoteCommandPanel = new JToolBar();
            remoteCommandPanel.setFloatable(false);
            remoteCommandPanel.setLayout(flowLayout);
            remoteCommandPanel.add(getNewProjectButton());
            remoteCommandPanel.add(getOpenProjectButton());
            remoteCommandPanel.add(getSaveProjectButton());
            remoteCommandPanel.add(getSaveProjectAsButton());
            remoteCommandPanel.add(getEditProjectButton(), null);
            remoteCommandPanel.addSeparator(new Dimension(32, 32));
            remoteCommandPanel.add(getConnectButton(), null);
            remoteCommandPanel.add(getApplyRulesButton());
            remoteCommandPanel.add(getSnapshotButton(), null);
            remoteCommandPanel.add(getResetButton(), null);
            remoteCommandPanel.add(getRunGcButton(), null);
            remoteCommandPanel.addSeparator(new Dimension(32, 32));
            remoteCommandPanel.add(getJButton2(), null);
        }
        return remoteCommandPanel;
    }

    /**
     * This method initializes helpMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText("Help");
            helpMenu.add(getShowTipsMenuItem());
            helpMenu.add(getAboutMenuItem());
        }
        return helpMenu;
    }

    /**
     * This method initializes aboutMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText("About...");
            aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    AboutDialog d = new AboutDialog(MainFrame.this);
                    d.setVisible(true);
                }
            });
        }
        return aboutMenuItem;
    }

    /**
     * This method initializes jMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText("Exit");
            exitMenuItem.setAccelerator(javax.swing.KeyStroke
                .getKeyStroke(java.awt.event.KeyEvent.VK_X,
                              java.awt.Event.ALT_MASK,
                              false));
            exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.exit();
                }
            });
        }
        return exitMenuItem;
    }

    /**
     * This method initializes tabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.setPreferredSize(new java.awt.Dimension(400, 400));
            tabbedPane.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
            tabbedPane.addTab(" Call Graph ", null, getCallGraphTabPanel(), null);
            tabbedPane.addTab(" Call Tree ", null, getCallTreePanel(), null);
            tabbedPane.addTab(" Memory ", null, getMemoryMonitorPanel(), null);
            tabbedPane.addTab(" Class List ", null, getClassListPanel(), null);
            tabbedPane.addTab(" Threads ", null, getThreadPanel(), null);
            tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    if (app.getClient().isConnected()
                            && tabbedPane.getSelectedIndex() == 3) {
                        classListPanel.refreshClassList();
                    }
                }
            });
        }
        return tabbedPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCallTreePanel() {
        if (callTreePanel == null) {
            callTreePanel = new JPanel();
            callTreePanel.setLayout(new BorderLayout());
            callTreePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            callTreePanel.setEnabled(true);
            callTreePanel.setVisible(true);
            callTreePanel.add(getTreeScrollPane(), BorderLayout.CENTER);
            callTreePanel.add(getDetailScrollPane(), BorderLayout.SOUTH);
        }
        return callTreePanel;
    }

    /**
     * This method initializes callGraphPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCallGraphTabPanel() {
        if (callGraphTabPanel == null) {
            callGraphTabPanel = new JPanel();
            callGraphTabPanel.setLayout(new BorderLayout());
            callGraphTabPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8,
                                                                                    8,
                                                                                    8,
                                                                                    8));
            callGraphTabPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
            callGraphTabPanel.add(getJPanel1(), java.awt.BorderLayout.NORTH);
        }
        return callGraphTabPanel;
    }

    /**
     * This method initializes callGraph
     * 
     * @return net.sf.profiler4j.ui.CallGraphPanel
     */
    private CallGraphPanel getCallGraphPanel() {
        if (callGraphPanel == null) {
            callGraphPanel = new CallGraphPanel();
            callGraphPanel.setBorder(null);
        }
        return callGraphPanel;
    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getCallGraphPanel());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
            jPanel1 = new JPanel();
            jPanel1.setBorder(null);
            jPanel1.setLayout(flowLayout1);
            jPanel1.add(getNcutSlider(), null);
        }
        return jPanel1;
    }

    /**
     * This method initializes ncutSlider
     * 
     * @return javax.swing.JSlider
     */
    private JSlider getNcutSlider() {
        if (ncutSlider == null) {
            ncutSlider = new JSlider();
            ncutSlider.setMaximum(100);
            ncutSlider.setMinorTickSpacing(5);
            ncutSlider.setPaintTicks(true);
            ncutSlider.setPaintLabels(true);
            ncutSlider.setValue(20);
            ncutSlider.setToolTipText("Number of methods to show");
            ncutSlider.setSnapToTicks(true);
            ncutSlider.setMajorTickSpacing(20);
            ncutSlider.setPreferredSize(new java.awt.Dimension(200, 42));
            ncutSlider.setMinimum(10);
            // getNcutTextField().setText(String.valueOf(ncutSlider.getValue()));
            ncutSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    callGraphPanel.applyNCut(ncutSlider.getValue());
                    // getNcutTextField().setText(String.valueOf(ncutSlider
                    // .getValue()));
                    callGraphPanel.repaint();
                }
            });
        }
        return ncutSlider;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRunGcButton() {
        if (runGcButton == null) {
            runGcButton = new JButton();
            runGcButton.setEnabled(false);
            runGcButton.setToolTipText("Run GC");
            runGcButton.setFocusPainted(false);
            runGcButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/bin_closed.png")));
            runGcButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            runGcButton.setPreferredSize(new java.awt.Dimension(32, 32));
            runGcButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        app.getClient().runGc();
                    } catch (ClientException e1) {
                        app.error("Could not run remote CG", e1);
                    }
                }
            });
        }
        return runGcButton;
    }

    /**
     * This method initializes memoryMonitorPanel
     * 
     * @return net.sf.profiler4j.ui.MemoryMonitorPanel
     */
    private MemoryPanel getMemoryMonitorPanel() {
        if (memoryMonitorPanel == null) {
            memoryMonitorPanel = new MemoryPanel(app);
        }
        return memoryMonitorPanel;
    }

    private void open() {
        String dir = lastDir;
        JFileChooser fc = new JFileChooser(dir);
        fc.setFileFilter(fileFilter);
        int returnVal = fc.showOpenDialog(MainFrame.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            lastDir = file.getParent();
            try {
                viewSnapshot(Snapshot.load(file));
            } catch (Exception e) {
                app.error("Could not open snapshot", e);
            }
        }
    }
    
    private void exportCallGraph(File imageFile) throws IOException {
        
        ImageFileWriter writer = new ImageFileWriter();
        writer.writeFile(callGraphPanel, new ToPng(), imageFile);
    }

    private void viewDetail(NodeInfo info) {
        StringBuilder out = new StringBuilder();
        if (info != null) {
            Snapshot.Method g = info.getMethodInfo();
            out.append(String.format("Method: %s\n", g.getName()));
            out.append(String.format("Hits  : %d\n", g.getHits()));
            out.append(String.format("Total : %.1fms\n", g.getNetTime()));
            out.append(String.format("Local : %.1fms\n", g.getSelfTime()));
        }
        detailTextArea.setText(out.toString());
        detailTextArea.setCaretPosition(0);
    }

    public boolean receiveEvent(AppEvent ev) {
        if (ev.getType() == AppEventType.CONNECTED) {

            runGcButton.setEnabled(true);
            snapshotButton.setEnabled(true);
            resetButton.setEnabled(true);
            applyRulesButton.setEnabled(true);
            connectButton.setToolTipText("Disconnect from remote JVM");
            connectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/disconnect.png")));
            //classListPanel.refreshClassList();

        } else if (ev.getType() == AppEventType.DISCONNECTED) {

            callGraphPanel.setSnapshot(null);
            connectButton.setToolTipText("Connect to remote JVM");
            connectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/connect.png")));
            snapshotButton.setEnabled(false);
            resetButton.setEnabled(false);
            runGcButton.setEnabled(false);
            applyRulesButton.setEnabled(false);
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            root.removeAllChildren();
            model.reload();

        } else if (ev.getType() == AppEventType.SNAPSHOT) {
            viewSnapshot((Snapshot) ev.getArg());
        }

        return false;
    }

    private void viewSnapshot(Snapshot sn) {
        TreeBuilder builder = new TreeBuilder(sn);
        DefaultMutableTreeNode root = builder.buildTree();
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
        tree.setShowsRootHandles(false);
        tree.setCellRenderer(new MethodRenderer());
        callGraphPanel.setSnapshot(sn);
        callGraphPanel.applyNCut(ncutSlider.getValue());
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getApplyRulesButton() {
        if (applyRulesButton == null) {
            applyRulesButton = new JButton();
            applyRulesButton.setEnabled(false);
            applyRulesButton.setToolTipText("Apply current profiling rules");
            applyRulesButton.setPreferredSize(new java.awt.Dimension(32, 32));
            applyRulesButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/tick.png")));
            applyRulesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.applyRules();
                }
            });
        }
        return applyRulesButton;
    }

    /**
     * This method initializes editProjectButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditProjectButton() {
        if (editProjectButton == null) {
            editProjectButton = new JButton();
            editProjectButton.setEnabled(true);
            editProjectButton.setToolTipText("Edit profiling project details");
            editProjectButton.setFocusPainted(false);
            editProjectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/pencil.png")));
            editProjectButton.setText("");
            editProjectButton.setPreferredSize(new java.awt.Dimension(32, 32));
            editProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.editProject();
                }
            });
        }
        return editProjectButton;
    }

    /**
     * This method initializes classListPanel
     * 
     * @return net.sf.profiler4j.console.ClassListPanel
     */
    private ClassListPanel getClassListPanel() {
        if (classListPanel == null) {
            classListPanel = new ClassListPanel(app);
            classListPanel.setToolTipText("Type the begining of a class name here");
        }
        return classListPanel;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton2() {
        if (helpButton == null) {
            helpButton = new JButton();
            helpButton.setEnabled(true);
            helpButton.setToolTipText("Show Help");
            helpButton.setFocusPainted(false);
            helpButton.setHorizontalTextPosition(SwingConstants.CENTER);
            helpButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/help.png")));
            helpButton.setPreferredSize(new java.awt.Dimension(32, 32));
            helpButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Help not yet written");
                }
            });
        }
        return helpButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOpenProjectButton() {
        if (openProjectButton == null) {
            openProjectButton = new JButton();
            openProjectButton.setEnabled(true);
            openProjectButton.setToolTipText("Open profiling project");
            openProjectButton.setFocusPainted(false);
            openProjectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/fldr_obj.gif")));
            openProjectButton.setText("");
            openProjectButton.setPreferredSize(new Dimension(32, 32));
            openProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.openProject();
                }
            });
        }
        return openProjectButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNewProjectButton() {
        if (newProjectButton == null) {
            newProjectButton = new JButton();
            newProjectButton.setEnabled(true);
            newProjectButton.setToolTipText("Create new profiling project");
            newProjectButton.setFocusPainted(false);
            newProjectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/newfile_wiz.gif")));
            newProjectButton.setText("");
            newProjectButton.setPreferredSize(new Dimension(32, 32));
            newProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.newProject();
                }
            });
        }
        return newProjectButton;
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveProjectButton() {
        if (saveProjectButton == null) {
            saveProjectButton = new JButton();
            saveProjectButton.setEnabled(true);
            saveProjectButton.setToolTipText("Save profiling project");
            saveProjectButton.setFocusPainted(false);
            saveProjectButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/save_edit.gif")));
            saveProjectButton.setText("");
            saveProjectButton.setPreferredSize(new Dimension(32, 32));
            saveProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.saveProject(false);
                }
            });
        }
        return saveProjectButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveProjectAsButton() {
        if (saveProjectAsButton == null) {
            saveProjectAsButton = new JButton();
            saveProjectAsButton.setEnabled(true);
            saveProjectAsButton.setToolTipText("Save profiling project as");
            saveProjectAsButton.setFocusPainted(false);
            saveProjectAsButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/saveas_edit.gif")));
            saveProjectAsButton.setText("");
            saveProjectAsButton.setPreferredSize(new Dimension(32, 32));
            saveProjectAsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    app.saveProject(true);
                }
            });
        }
        return saveProjectAsButton;
    }

    /**
     * This method initializes threadPanel
     * 
     * @return net.sf.profiler4j.console.ThreadPanel
     */
    private ThreadPanel getThreadPanel() {
        if (threadPanel == null) {
            threadPanel = new ThreadPanel(app);
        }
        return threadPanel;
    }

    /**
     * This method initializes statusBarPanel
     * 
     * @return net.sf.profiler4j.console.StatusBarPanel
     */
    public StatusBarPanel getStatusBarPanel() {
        if (statusBarPanel == null) {
            statusBarPanel = new StatusBarPanel();
            statusBarPanel.setMessage("");
        }
        return statusBarPanel;
    }

    /**
     * This method initializes showTipsMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getShowTipsMenuItem() {
        if (showTipsMenuItem == null) {
            showTipsMenuItem = new JMenuItem();
            showTipsMenuItem.setText("Show tips...");
            showTipsMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    TipDialog d = new TipDialog(MainFrame.this);
                    d.showTip(app);
                }
            });
        }
        return showTipsMenuItem;
    }

    /**
     * This method initializes optionsMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getOptionsMenuItem() {
        if (optionsMenuItem == null) {
            optionsMenuItem = new JMenuItem();
            optionsMenuItem.setEnabled(true);
            optionsMenuItem.setText("Options...");
            optionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    OptionsDialog d = new OptionsDialog(MainFrame.this);
                    d.pack();
                    d.setLocationRelativeTo(null);
                    d.setVisible(true);
                }
            });
        }
        return optionsMenuItem;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
