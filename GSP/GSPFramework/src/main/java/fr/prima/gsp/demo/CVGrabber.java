/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import com.sun.jna.Pointer;
import com.sun.jna.Native;
import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import fr.prima.jna.opencv.cxcore.CxcoreLibrary;
import fr.prima.jna.opencv.cxtypes.CvSize;
import fr.prima.jna.opencv.cxtypes.CxtypesLibrary;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.videoserviceclient.BufferedImageSourceListener;
import fr.prima.vsp.modules.opencv.CVTools;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

/**
 *
 * @author emonet
 */
public class CVGrabber extends AbstractModuleEnablable implements BufferedImageSourceListener {
    
    @ModuleParameter(initOnly=true)
    public Assembly assembly;

    public void stopped() {
        // BufferedImageSourceListener method
    }

    public synchronized void bufferedImageReceived(BufferedImage image, ByteBuffer imageDataOrNull) {
        if (!isEnabled()) return;
        CvSize.ByValue size = new CvSize.ByValue();
        size.width = image.getWidth();
        size.height = image.getHeight();
        size.write();

        IplImage im = CxcoreLibrary.INSTANCE.cvCreateImage(size, CxtypesLibrary.IPL_DEPTH_8U, 3);
        if (imageDataOrNull != null && imageDataOrNull.isDirect()) {
            Pointer ptr = im.imageData;
            im.imageData = Native.getDirectBufferPointer(imageDataOrNull);
            im.write();
            imageOutput(im);
            im.imageData = ptr;
            CVTools.release(im);
        } else {
            WritableRaster raster = image.getRaster();
            int bufferSize = raster.getWidth() * raster.getHeight() * raster.getNumDataElements();
            ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
            buffer.put((byte[]) raster.getDataElements(0, 0, raster.getWidth(), raster.getHeight(), null));
            im.imageData = Native.getDirectBufferPointer(buffer);
            im.write();
            imageOutput(im);
            // buffer should get unreferenced and garbage collected
        }
        im.clear();
        size.clear();
    }

    private void imageOutput(IplImage im) {
        emitEvent(im);
    }

    // helpers for pipeline destruction
    public void closePipeline() {assembly.stop();}
    public void closePipeline1(Object o1) {assembly.stop();}
    public void closePipeline2(Object o1, Object o2) {assembly.stop();}
    public void closePipeline3(Object o1, Object o2, Object o3) {assembly.stop();}
    public void closePipeline4(Object o1, Object o2, Object o3, Object o4) {assembly.stop();}

}
