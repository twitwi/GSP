/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.spi.AbstractModule;
import org.bridj.Pointer;

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
        return ((Pointer) floatPointer.pointer).getFloats(length);
    }
}
