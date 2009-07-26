package net.sf.profiler4j.console.util.export;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Represents image data going over the clip board.
 * <p>
 * Necessary for exporting e.g. the call graph as an image.
 * 
 * @author murat
 */
public class ImageTransferable implements Transferable, ClipboardOwner {

    Image image;

    public ImageTransferable(Image img) {
        this.image = img;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
        
        // Check flavor
        if (null != flavor && flavor != DataFlavor.imageFlavor)
            throw new UnsupportedFlavorException(flavor);
        
        // return image data
        return image;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // don't care, do nothing.
    }

}
