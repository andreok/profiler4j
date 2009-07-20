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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.FileOutputStream;

import javax.swing.JPanel;

import com.keypoint.PngEncoder;

/**
 * Exports a given JPanel to a PNG image.
 * 
 * @author murat
 */
public class ToPng implements ToImageConverter {

    public byte[] export(JPanel panel) {
        BufferedImage bimg = new BufferedImage(
            panel.getWidth(), //
            panel.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        
//        int w = bimg.getWidth(null);
//        int h = bimg.getHeight(null);
//        int [] pixels = new int[w * h];
//        PixelGrabber pg = new PixelGrabber(bimg,0,0,w,h,pixels,0,w);
//        try { 
//          pg.grabPixels();
//          } 
//        catch(InterruptedException ie) { 
//          ie.printStackTrace();
//          }
//
        // Let the panel draw itself as usual, except its not drawing
        // on the screenbuffer but rather in our pixel map.
        panel.paint((Graphics2D)bimg.getGraphics());
        
        PngEncoder png = new PngEncoder(bimg, true);
        return png.pngEncode();
        // Encode as a JPEG
    }

}
