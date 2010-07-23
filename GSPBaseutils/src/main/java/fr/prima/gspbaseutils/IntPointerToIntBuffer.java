/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import com.sun.jna.Pointer;
import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.nio.IntBuffer;

/**
 *
 * @author remonet
 */
public class IntPointerToIntBuffer extends AbstractModule {

    public void input(int length, NativePointer intPointer) {
        output(intPointer.toIntBuffer(length));
    }

    public void output(IntBuffer s) {
        emitEvent(s);
    }
}
