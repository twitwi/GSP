/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import com.sun.jna.Pointer;
import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.nio.FloatBuffer;

/**
 *
 * @author remonet
 */
public class FloatPointerToFloatBuffer extends AbstractModule {


    public void input(int length, NativePointer floatPointer) {
        output(floatPointer.toFloatBuffer(length));
    }

    public void output(FloatBuffer s) {
        emitEvent(s);
    }

}
