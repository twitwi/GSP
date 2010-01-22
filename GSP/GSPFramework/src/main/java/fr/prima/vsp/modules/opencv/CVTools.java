/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.opencv;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import fr.prima.jna.opencv.cxcore.CxcoreLibrary;
import fr.prima.jna.opencv.cxtypes.CvMat;
import fr.prima.jna.opencv.cxtypes.CvRect;
import fr.prima.jna.opencv.cxtypes.CvScalar;
import fr.prima.jna.opencv.cxtypes.CvSize;
import fr.prima.jna.opencv.cxtypes.CxtypesLibrary;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.jna.opencv.highgui.HighguiLibrary.CvArr;
import java.nio.IntBuffer;

/**
 *
 * @author twilight
 */
public class CVTools {

    static int CV_CN_SHIFT = 3;
    public static int CV_MAKETYPE(int depth, int cn) {
        return ((depth) | (((cn)-1) << CV_CN_SHIFT));
    }

    public static CvArr arr(IplImage im) {
        return new CvArr(im.getPointer());
    }

    public static CvArr arr(CvMat m) {
        return new CvArr(m.getPointer());
    }

    public static CvMat mat(IplImage im) {
        CvMat matHeader = CxcoreLibrary.INSTANCE.cvCreateMatHeader(im.width, im.height, CV_MAKETYPE(CxtypesLibrary.CV_8U, im.nChannels));
        //PointerByReference ref = new PointerByReference(matHeader.getPointer());
        CxcoreLibrary.INSTANCE.cvGetMat(arr(im), matHeader, (IntBuffer) null, 0);
        return matHeader;
    }

    public static CvSize.ByValue size(int w, int h) {
        CvSize.ByValue res = new CvSize.ByValue();
        res.width = w;
        res.height = h;
        res.write();
        return res;
    }

    public static CvScalar.ByValue scalar(double... v) {
        CvScalar.ByValue res = new CvScalar.ByValue();
        res.val = v;
        res.write();
        return res;
    }

    public static CvRect.ByValue corners(int x, int y, int x2, int y2) {
        return rect(x, y, x2 - x, y2 - y);
    }

    static CvRect.ByValue rect(int x, int y, int w, int h) {
        CvRect.ByValue res = new CvRect.ByValue();
        res.x = x;
        res.y = y;
        res.width = w;
        res.height = h;
        res.write();
        return res;
    }

    public static IplImage createImageAs(IplImage input) {
        CvSize.ByValue size = size(input.width, input.height);
        return CxcoreLibrary.INSTANCE.cvCreateImage(size, input.depth, input.nChannels);
    }

    public static void release(IplImage im) {
        CxcoreLibrary.INSTANCE.cvReleaseImage(im.castToReferenceArray());
    }

    public static void releaseSubImage(IplImage im) {
        CxcoreLibrary.INSTANCE.cvReleaseImageHeader(im.castToReferenceArray());
    }

    public static IplImage extractSubImage(IplImage input, int x, int y, int w, int h) {
        CvSize.ByValue size = size(w, h);
        IplImage header = CxcoreLibrary.INSTANCE.cvCreateImageHeader(size, input.depth, input.nChannels);
        // hard coded 1 byte per channel image
        CvMat matHeader = CxcoreLibrary.INSTANCE.cvCreateMatHeader(w, h, CV_MAKETYPE(CxtypesLibrary.CV_8U, input.nChannels));
        CxcoreLibrary.INSTANCE.cvGetSubRect(arr(input), matHeader, rect(x, y, w, h));
        IplImage res = CxcoreLibrary.INSTANCE.cvGetImage(arr(matHeader), header);
        //OpencvLibrary.cvReleaseImageHeader(new PointerByReference(header.getPointer()));
        //OpencvLibrary.cvReleaseMat(new PointerByReference(matHeader.getPointer()));
        return res;
    }


}
