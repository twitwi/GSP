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
public class FloatPointerToFloatArray extends AbstractModule {


    public void input(int length, NativePointer floatPointer) {
        output(convert(length, floatPointer));
    }

    public void output(float[] s) {
        emitEvent(s);
    }

    public static float[] convert(int length, NativePointer floatPointer) {
        return ((Pointer) floatPointer.pointer).getFloatArray(0, length);
    }
}
