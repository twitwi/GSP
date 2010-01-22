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
 * @author emonet
 */
public class CVExtractSubimage extends AbstractModule {

    @ModuleParameter
    public Rectangle rectangle = null;

    public synchronized void imageInput(IplImage input) {
        if (!isEnabled()) return;
        IplImage sub = CVTools.extractSubImage(input, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        imageOutput(sub);
        CVTools.releaseSubImage(sub);
    }

    public void imageOutput(IplImage image) {
        emitEvent(image);
    }


}
