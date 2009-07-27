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

import java.awt.Image;

import javax.swing.JPanel;

/**
 * Creates an image representation of a given panel's drawings.
 * <p>
 * A derivation determines the particular image format. 
 * 
 * @author murat
 */
public interface ToImageConverter {
    
    /**
     * Creates an image representation from the panel.
     * 
     * @param panel The panel whose image representation is wanted.
     * @return the image representation
     */
    public byte[] export(JPanel panel);
    
    /**
     * Creates an image from the given panel's drawings.
     * 
     * @param panel whose drawings as image are wanted.
     * @return the image
     */
    public Image createImage(JPanel panel);
}
