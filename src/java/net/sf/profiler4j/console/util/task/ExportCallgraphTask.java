/**
 * Copyright 2009 Murat Knecht
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License. */
package net.sf.profiler4j.console.util.task;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import net.sf.profiler4j.console.CallGraphPanel;
import net.sf.profiler4j.console.Console;
import net.sf.profiler4j.console.MainFrame;
import net.sf.profiler4j.console.Project;
import net.sf.profiler4j.console.client.Snapshot;
import net.sf.profiler4j.console.util.export.FilenameGenerator;
import net.sf.profiler4j.console.util.export.ImageFileWriter;
import net.sf.profiler4j.console.util.export.ToPng;

public final class ExportCallgraphTask extends LongTask {
    private Snapshot snapshot;
    private Project project;
    private MainFrame mainFrame;
    
    static final String MSG_FAILED_EXPORTING_CALLGRAPH = "Failed exporting the callgraph: ";

    static final String MSG_FAILURE_TO_GENERATE_FILENAME = "Could not generate a filename from the pattern, callgraph exporting failed: ";


    public ExportCallgraphTask(Snapshot snapshot, Project project, MainFrame frame) {
        this.snapshot = snapshot;
        this.project = project;
        this.mainFrame = frame;
    }
    
    public void executeInBackground() throws Exception {
        setTaskMessage("Exporting callgraph...");
        try {
            // Generate a new file to export the image.
            FilenameGenerator generator = new FilenameGenerator(project.getExportPattern());
            
            // Zoom the call graph to max resolution.
            CallGraphPanel panel = mainFrame.getCallGraphPanel();
            panel.setSnapshot(snapshot);
            panel.applyNCut(mainFrame.getNcutSlider().getMaximum());
            
            new ImageFileWriter().writeFile(
                    panel, // The panel to draw.
                    new ToPng(),                        // The image creator, defining the image format.
                    generator.getValidFile()            // The file to write the image to.
                    );
            
            // Reduce the resolution to the original value.
            panel.applyNCut(mainFrame.getNcutSlider().getValue());
            
        } catch (FileNotFoundException exc) {
            JOptionPane.showMessageDialog(mainFrame, MSG_FAILURE_TO_GENERATE_FILENAME + exc.getMessage());
            // If the pattern is invalid, then disable further exporting.
            project.setExportAutomaticallyEnabled(false);
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(mainFrame, MSG_FAILED_EXPORTING_CALLGRAPH + exc.getMessage());
            // If the pattern is invalid, then disable further exporting.
            project.setExportAutomaticallyEnabled(false);
        }
    }
}