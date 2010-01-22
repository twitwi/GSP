/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.opencv;

import fr.prima.jna.opencv.cv.CvLibrary;
import fr.prima.jna.opencv.cxcore.CxcoreLibrary;
import fr.prima.jna.opencv.cxtypes.CvRect;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.vsp.modules.AbstractModule;
import static fr.prima.vsp.modules.opencv.CVTools.*;

/**
 *
 * @author emonet
 */
public class CVRegionLearner extends AbstractModule {


    IplImage intersection;
    boolean shouldTakeSnapshot = false;
    int x;
    int y;
    int w;
    int h;

    public synchronized void imageInput(IplImage input) {
        if (!shouldTakeSnapshot || !isEnabled()) return;
        shouldTakeSnapshot = false;
        if (intersection == null || intersection.width != input.width || intersection.height != input.height /*|| ...*/) {
            if (intersection != null) {
                release(intersection);
            }
            intersection = createImageAs(input);
            CxcoreLibrary.INSTANCE.cvCopy(arr(input), arr(intersection), null);
            intersection();
        } else {
            // or cvMin
            CxcoreLibrary.INSTANCE.cvMul(arr(intersection), arr(input), arr(intersection), 1. / 255.);
            CvRect rect = CvLibrary.INSTANCE.cvBoundingRect(arr(mat(intersection)), 0); // 0 is ignored
            /*x = rect.x;
            y = rect.y;
            w = rect.width;
            h = rect.height;
             */
            //rectangle();
            intersection();
        }
    }

    @Override
    public synchronized void stopModule() {
        if (intersection != null) {
            release(intersection);
            intersection = null;
        }
    }

    public synchronized void clear() {
        if (intersection != null) {
            release(intersection);
        }
        intersection = null;
    }

    public synchronized void snapshot() {
        shouldTakeSnapshot = true;
    }

    public synchronized void commit() {
        rectangle();
    }

    public void rectangle() {
        emitEvent(x, y, w, h);
    }

    public void intersection() {
        emitEvent(intersection);
    }
    public void message(Object msg) {
        emitEvent(msg);
    }

}
