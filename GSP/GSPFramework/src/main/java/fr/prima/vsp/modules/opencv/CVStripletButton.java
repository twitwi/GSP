/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.opencv;

import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.vsp.modules.AbstractModule;
import fr.prima.vsp.modules.ModuleParameter;
import java.awt.Rectangle;

/**
 *
 * @author twilight
 */
public class CVStripletButton extends AbstractModule {

    @ModuleParameter
    public int delay = 200;

    @ModuleParameter
    public int positivePercent = 50;

    @ModuleParameter
    public int acceptedNegativePercent = 80;

    @ModuleParameter
    public int mode = 0;

    @ModuleParameter
    public Rectangle rectangle;

    long lastFalse = System.currentTimeMillis();
    boolean preventClick = false;
    Runnable executeOnClickOrNull = null;
    
    public synchronized void integralInput(IplImage integral) {
        if (!isEnabled()) return;
        int x = 0;
        int y = 0;
        int w = integral.width - 1;
        int h = integral.height - 1;
        if (rectangle != null) {
            x = rectangle.x;
            y = rectangle.y;
            w = rectangle.width;
            h = rectangle.height;
        }
        rectangleOutput();

        boolean respond = false;
        Boolean s1, s2, s3, s4, s5, s6;
        s1 = s2 = s3 = s4 = s5 = s6 = null;
        do {
            if (mode == 1) {
                // new new version
                int nPositive = getIntegralValue(integral, x + w * 3 / 10, y + h * 3 / 10, w * 4 / 10, h * 4 / 10);
                if (nPositive < positivePercent * 255 * (w * h * 16 / 100) / 100) {
                    // positivePercent = 50% of the integral surface (by default)
                    break;
                }
                int nNegative = 0;
                nNegative += getIntegralValue(integral, x, y, w, h);
                nNegative -= getIntegralValue(integral, x + w / 5, y + h / 5, 4 * w / 5, 4 * h / 5);
                // negative surface is 4 times bigger than positive one
                // we accept as a maximum of negative: acceptedNegativePercent = 25% of the positive area (by default)
                respond = 100 * nNegative < acceptedNegativePercent * nPositive;
                break;
            }
            /*if (mode == 1) {
                // new version
                int nPositive = getIntegralValue(integral, x + w / 3, y + h / 3, w / 3, h / 3);
                if (nPositive < positivePercent * 255 * (w * h / 9) / 100) {
                    // positivePercent = 50% of the integral surface (by default)
                    break;
                }
                int nNegative = 0;
                nNegative += getIntegralValue(integral, x, y, w, h);
                nNegative -= getIntegralValue(integral, x + w / 6, y + h / 6, 4 * w / 6, 4 * h / 6);
                // negative surface is 5 times bigger than positive one
                // we accept as a maximum of negative: acceptedNegativePercent = 25% of the positive area (by default)
                respond = 100 * nNegative / 5 < acceptedNegativePercent * nPositive;
                break;
            }*/
            boolean center =
                    (s1 = horizontalStriplet(integral, x, y + h / 3, w, h / 3))
                    || (s2 = verticalStriplet(integral, x + w / 3, y, w / 3, h));
            if (!center) {
                break;
            }
            // stop as soon as 2 striplets are responding in periphery
            int inPeriphery = 0;
            inPeriphery += (s3 = horizontalStriplet(integral, x, y, w, h / 3)) ? 1 : 0;
            inPeriphery += (s4 = horizontalStriplet(integral, x, y + 2 * h / 3, w, h / 3)) ? 1 : 0;
            if (inPeriphery > 1) {
                break;
            }
            inPeriphery += (s5 = verticalStriplet(integral, x, y, w / 3, h)) ? 1 : 0;
            if (inPeriphery > 1) {
                break;
            }
            inPeriphery += (s6 = verticalStriplet(integral, x + 2 * w / 3, y, w / 3, h)) ? 1 : 0;
            if (inPeriphery > 1) {
                break;
            }
            respond = true;
        } while (false);
        //message("Striplets: "+s1+" "+s2+" "+s3+" "+s4+" "+s5+" "+s6+" ");
        long now = System.currentTimeMillis();
        if (respond) {
            if (!preventClick && now - lastFalse > delay) {
                if (executeOnClickOrNull != null) {
                    executeOnClickOrNull.run();
                }
                //service.sendToAllClients("events", Utility.message("<click/>"));
                message("Click " + now);
                click();
                preventClick = true;
            }
        } else {
            if (preventClick) {
                message("======== " + now);
            }
            preventClick = false;
            lastFalse = now;
        }
    }

    public static int getIntegralValue(IplImage im, int x, int y, int w, int h) {
        int a = getImageValue(im, x, y);
        int b = getImageValue(im, x + w, y);
        int c = getImageValue(im, x, y + h);
        int d = getImageValue(im, x + w, y + h);
        return d - b - c + a;
    }
    public static int getImageValue(IplImage im, int x, int y) {
        return im.imageData.getInt(4 * x + y * im.widthStep);
    }

    public static boolean horizontalStriplet(IplImage im, int x, int y, int w, int h) {
        int n = w * h;
        int cumulated = 0;
        cumulated -= getIntegralValue(im, x, y, w, h);
        cumulated += 2 * getIntegralValue(im, x + 1 * w / 4, y, 2 * w / 4, h);
        return cumulated > 10 * n;
    }

    public static boolean verticalStriplet(IplImage im, int x, int y, int w, int h) {
        int n = w * h;
        int cumulated = 0;
        cumulated -= getIntegralValue(im, x, y, w, h);
        cumulated += 2 * getIntegralValue(im, x, y + 1 * h / 4, w, 2 * h / 4);
        return cumulated > 10 * n;
    }

    public synchronized void rectangleInput(int x, int y, int w, int h) {
        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = w;
        rectangle.height = h;
        message(rectangle);
    }

    public synchronized void smallRectangleInput(int x, int y, int w, int h) {
        rectangle.x = x - w;
        rectangle.y = y - h;
        rectangle.width = 3 * w;
        rectangle.height = 3 * h;
        message(rectangle);
    }


    private void message(Object msg) {
        emitEvent(msg);
    }

    private void click() {
        emitEvent();
    }

    private void rectangleOutput() {
        if (rectangle != null) {
            emitEvent(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }
}
