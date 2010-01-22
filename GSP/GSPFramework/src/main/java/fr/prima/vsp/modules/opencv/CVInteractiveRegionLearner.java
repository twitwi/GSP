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
public class CVInteractiveRegionLearner extends AbstractModule {

    IplImage cumulatedImage = null;

    boolean shouldCumulateBackgroundDifference = false;
    IplImage cumulatedMotionImage = null;
    int cumulateMotionImageCounter = -1;
    boolean cumulateAntiMotion = false;

    public CVInteractiveRegionLearner() {
        allowOpenEvents = true;
    }

    public synchronized void reinit() {
        stopModule();
        message("reinited");
    }

    public synchronized void motionImage(IplImage motion) {
        if (cumulateMotionImageCounter == -1 || !isEnabled()) return;
        if (cumulateMotionImageCounter > 0) {
            if (cumulatedMotionImage == null || cumulatedMotionImage.width != motion.width || cumulatedMotionImage.height != motion.height /*|| ...*/) {
                if (cumulatedMotionImage != null) {
                    release(cumulatedMotionImage);
                }
                cumulatedMotionImage = createImageAs(motion);
                CxcoreLibrary.INSTANCE.cvCopy(arr(motion), arr(cumulatedMotionImage), null);
            } else {
                CxcoreLibrary.INSTANCE.cvMax(arr(cumulatedMotionImage), arr(motion), arr(cumulatedMotionImage));
            }
        } else {
            // finishing
            if (cumulateAntiMotion) {
                IplImage oldCumulatedMotionImage = cumulatedMotionImage;
                cumulatedMotionImage = createImageAs(oldCumulatedMotionImage);
                CxcoreLibrary.INSTANCE.cvCopy(arr(oldCumulatedMotionImage), arr(cumulatedMotionImage), null);
                CxcoreLibrary.INSTANCE.cvAbsDiffS(arr(oldCumulatedMotionImage), arr(cumulatedMotionImage), scalar(255));
                release(oldCumulatedMotionImage);
                message("motion-image-reverted");
            }
            integrateImage(cumulatedMotionImage);
            release(cumulatedMotionImage);
            cumulatedMotionImage = null;
            cumulatedImage();
            message("motion-recording-end");
        }
        cumulateMotionImageCounter --;
    }
    public synchronized void backgroundDifference(IplImage bgDiff) {
        if (!shouldCumulateBackgroundDifference || !isEnabled()) return;

        shouldCumulateBackgroundDifference = false;
        integrateImage(bgDiff);
        message("background-snapshot-taken");
    }

    private void integrateImage(IplImage toCumulate) {
        CvLibrary.INSTANCE.cvErode(arr(toCumulate), arr(toCumulate), null, 1);
        CvLibrary.INSTANCE.cvDilate(arr(toCumulate), arr(toCumulate), null, 2);
        if (cumulatedImage == null || cumulatedImage.width != toCumulate.width || cumulatedImage.height != toCumulate.height /*|| ...*/) {
            if (cumulatedImage != null) {
                release(cumulatedImage);
            }
            cumulatedImage = createImageAs(toCumulate);
            CxcoreLibrary.INSTANCE.cvCopy(arr(toCumulate), arr(cumulatedImage), null);
        } else {
            CxcoreLibrary.INSTANCE.cvMin(arr(cumulatedImage), arr(toCumulate), arr(cumulatedImage));
        }
        cumulatedImage();
    }


    public void outputRegion() {
        if (cumulatedImage != null) {
            CvRect rect = CvLibrary.INSTANCE.cvBoundingRect(arr(mat(cumulatedImage)), 0); // 0 is ignored
            message("region:" + rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height);
        }
    }

    @Override
    public synchronized void stopModule() {
        if (cumulatedImage != null) {
            release(cumulatedImage);
            cumulatedImage = null;
        }
        if (cumulatedMotionImage != null) {
            release(cumulatedMotionImage);
            cumulatedMotionImage = null;
        }
        shouldCumulateBackgroundDifference = false;
        cumulateMotionImageCounter = -1;
    }

    public synchronized void clear() {
        if (cumulatedImage != null) {
            release(cumulatedImage);
        }
        cumulatedImage = null;
    }

    public synchronized void cumulateBackground() {
        shouldCumulateBackgroundDifference = true;
    }
    public synchronized void cumulateMotion() {
        cumulateAntiMotion = false;
        cumulateMotionImageCounter = 20;
        message("motion-recording-start");
    }
    public synchronized void cumulateAntiMotion() {
        cumulateMotion();
        cumulateAntiMotion = true;
    }
    
    /*
    public synchronized void commit() {
        rectangle();
    }
    public void rectangle() {
        emitEvent(x, y, w, h);
    }*/

    public void cumulatedImage() {
        emitEvent(cumulatedImage);
    }
    public void message(Object msg) {
        emitEvent(msg);
    }

}
