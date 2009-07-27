/*
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
 * limitations under the License.
 */
package net.sf.profiler4j.console.util.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.Validate;

/**
 * Creates an image file from a given image.
 * 
 * @author murat
 */
public class ImageFileWriter {
    
    /**
     * Export the given panel to an image file.
     * <p>
     * First, with the given converter, an image representation of the given
     * panel is created. Then, it is dumped to the given file.
     * 
     * @param panel The panel which to export. Must not be {@code null}.
     * @param converter The convert used to create the image representation of the panel.
     * Must not be {@code null}.
     * @param exportFile The file into which to write the image representation. Must be an opened, existing file
     * for which write-access is granted. Must not be {@code null}.
     *
     * @throws IOException in case the file operations fail.
     */
    public void writeFile(JPanel panel, ToImageConverter converter, File exportFile) throws IOException{
        Validate.notNull(panel);
        Validate.notNull(converter);
        Validate.notNull(exportFile);
        
        byte[] imageBytes = converter.export(panel);
                FileOutputStream fos = new FileOutputStream(exportFile);
                fos.write(imageBytes);
                fos.close();
    }
    
}
