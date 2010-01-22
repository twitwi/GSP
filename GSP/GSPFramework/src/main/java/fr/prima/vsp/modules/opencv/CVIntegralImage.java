/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.opencv;

import fr.prima.jna.opencv.cv.CvLibrary;
import fr.prima.jna.opencv.cxcore.CxcoreLibrary;
import fr.prima.jna.opencv.cxtypes.CvSize;
import fr.prima.jna.opencv.cxtypes.CxtypesLibrary;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.vsp.modules.AbstractModule;
import static fr.prima.vsp.modules.opencv.CVTools.*;

/**
 *
 * @author twilight
 */
public class CVIntegralImage extends AbstractModule {

    IplImage intim = null;
    IplImage sqintim = null;
    IplImage rintim = null;

    public synchronized void imageInput(IplImage input) {
        if (!isEnabled()) return;
        if (intim == null || intim.width != input.width + 1 || intim.height != input.height + 1 /*|| ...*/) {
            if (intim != null) {
                stopModule();
            }
            CvSize.ByValue intsize = size(input.width + 1, input.height + 1);
            System.err.println("realloc");
            intim = CxcoreLibrary.INSTANCE.cvCreateImage(intsize, CxtypesLibrary.IPL_DEPTH_SIGN | 32, 1);
            sqintim = CxcoreLibrary.INSTANCE.cvCreateImage(intsize, CxtypesLibrary.IPL_DEPTH_64F, 1);
            rintim = CxcoreLibrary.INSTANCE.cvCreateImage(intsize, CxtypesLibrary.IPL_DEPTH_SIGN | 32, 1);
            System.err.println("realloced");
            intsize.clear();
        }
        //CvLibrary.INSTANCE.cvIntegral(arr(input), arr(intim), null, null);
        CvLibrary.INSTANCE.cvIntegral(arr(input), arr(intim), arr(sqintim), arr(rintim));
        integralOutput();
        allOutput();
    }

    @Override
    public synchronized void stopModule() {
        // warning: used in custom code for cleaning
        if (intim != null) {
            release(intim);
            release(sqintim);
            release(rintim);
            intim = null;
            sqintim = null;
            rintim = null;
        }
    }


    private void integralOutput() {
        emitEvent(intim);
    }
    private void allOutput() {
        emitEvent(intim, sqintim, rintim);
    }

}
