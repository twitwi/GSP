/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import com.sun.jna.Pointer;
import fr.prima.gsp.framework.spi.AbstractModule;
import fr.prima.jna.opencv.cxcore.CxcoreLibrary;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.vsp.modules.opencv.CVTools;

/**
 *
 * @author twilight
 */
public class GetIPLData extends AbstractModule {

    IplImage c = null;
    public void input(IplImage im) {
        if (c == null) {
            c = CVTools.createImageAs(im);
        }
        CxcoreLibrary.INSTANCE.cvCopy(CVTools.arr(im), CVTools.arr(c), null);
        //c.imageData.clear(1000);
        output(c.imageData);
        outputImage(c);
    }

    public void output(Pointer p) {
        emitEvent(p);
    }

    private void outputImage(IplImage im) {
        emitEvent(im);
    }
}
