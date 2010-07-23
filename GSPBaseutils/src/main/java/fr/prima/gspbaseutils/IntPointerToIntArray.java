/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import com.sun.jna.Pointer;
import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.spi.AbstractModule;

/**
 *
 * @author remonet
 */
public class IntPointerToIntArray extends AbstractModule {

    public void input(int length, NativePointer intPointer) {
        output(convert(length, intPointer));
    }

    public void output(int[] s) {
        emitEvent(s);
    }

    public static int[] convert(int length, NativePointer intPointer) {
        return ((Pointer) intPointer.pointer).getIntArray(0, length);
    }
}
