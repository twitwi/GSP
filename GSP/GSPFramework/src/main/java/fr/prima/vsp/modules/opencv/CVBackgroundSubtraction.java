/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.opencv;

import fr.prima.jna.opencv.cv.CvLibrary;
import fr.prima.jna.opencv.cxcore.CxcoreLibrary;
import fr.prima.jna.opencv.cxtypes.CxtypesLibrary;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.vsp.modules.*;
import static fr.prima.vsp.modules.opencv.CVTools.*;

/**
 *
 * @author emonet
 */
public class CVBackgroundSubtraction extends AbstractModule {

    @ModuleParameter
    public int threshold = 25;

    IplImage bg = null;
    IplImage diff = null;
    IplImage output = null;
    
    public synchronized void grabBackground() {
        if (bg != null) {
            release(bg);
            bg = null;
        }
    }

    public synchronized void imageInput(IplImage input) {
        if (!isEnabled()) return;
        if (bg == null /*|| bg.width != input.width || bg.height != input.height || ...*/) {
            bg = createImageAs(input);
            CxcoreLibrary.INSTANCE.cvCopy(arr(input), arr(bg), null);
            message("background-grabbed");
        } else {
            if (diff == null || diff.width != input.width || diff.height != input.height /*|| ...*/)  {
                if (diff != null) {
                    release(diff);
                    release(output);
                }
                diff = createImageAs(input);
                output = CxcoreLibrary.INSTANCE.cvCreateImage(size(input.width, input.height), CxtypesLibrary.IPL_DEPTH_8U, 1);
            }
            CxcoreLibrary.INSTANCE.cvAbsDiff(arr(input), arr(bg), arr(diff));
            backgroundOutput(bg);
            if (threshold >= 0) {
                CvLibrary.INSTANCE.cvCvtColor(arr(diff), arr(output), CvLibrary.CV_BGR2GRAY);
                if (threshold != 0) {
                    CvLibrary.INSTANCE.cvThreshold(arr(output), arr(output), threshold, 255, CvLibrary.CV_THRESH_BINARY);
                    differenceOutput(output);
                } else {
                    differenceOutput(output);
                }
            } else {
                differenceOutput(diff);
            }

/*
            CxcoreLibrary.INSTANCE.cvConvertScale(arr(input), arr(input), 2, 0);

            IplImage intim = CxcoreLibrary.INSTANCE.cvCreateImage(size(input.width+1, input.height+1), CxtypesLibrary.IPL_DEPTH_SIGN|32, 3);

            CvLibrary.INSTANCE.cvIntegral(new HighguiLibrary.CvArr(input.getPointer()), new HighguiLibrary.CvArr(intim.getPointer()), null, null);

            IplImage viewim = CxcoreLibrary.INSTANCE.cvCreateImage(size(input.width+1, input.height+1), CxtypesLibrary.IPL_DEPTH_8U, 3);
            CxcoreLibrary.INSTANCE.cvConvertScale(new HighguiLibrary.CvArr(intim.getPointer()), new HighguiLibrary.CvArr(viewim.getPointer()), 1 / viewim.width / viewim.height, 0);
*/
        }
    }

    public void differenceOutput(IplImage im) {
        emitEvent(im);
    }
    public void backgroundOutput(IplImage im) {
        emitEvent(im);
    }

    public void message(Object o) {
        emitEvent(o);
    }
}
