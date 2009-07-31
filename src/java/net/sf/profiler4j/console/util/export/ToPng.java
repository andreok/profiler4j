/*
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
package net.sf.profiler4j.console.util.export;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.FileOutputStream;

import javax.swing.JPanel;

import com.keypoint.PngEncoder;

/**
 * Creates a PNG image from a given JPanel.
 * 
 * @author murat
 */
public class ToPng implements ToImageConverter {

    public byte[] export(JPanel panel) {
        Image bimg = createImage(panel);
        PngEncoder png = new PngEncoder(bimg, true);
        // Currently, we need to compress real well, because image format is too high.
        png.setCompressionLevel(9);
        return png.pngEncode();
        // Encode as a JPEG
    }

    public Image createImage(JPanel panel) {
        // The image needs to be correctly sized even in case the panel has never
        // been drawn yet. So, we trust that during setting the panel's content,
        // someone already did calculate and specify a preferred size. (Is the
        // case for the callgraph panel.)
        panel.setSize(panel.getPreferredSize());
        BufferedImage bimg = new BufferedImage(
                panel.getWidth(), panel.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // Let the panel draw itself as usual, except its not drawing
        // on the screenbuffer but rather in our pixel map.
        panel.paint((Graphics2D) bimg.getGraphics());
        
        return bimg;
    }

}
