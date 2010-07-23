/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import com.sun.jna.Pointer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author remonet
 */
public class NativePointerUtils {

    private static final int INT_SIZE = 4; // TODO
    private static final int FLOAT_SIZE = 4; // TODO

    public static IntBuffer intBuffer(int length, NativePointer intPointer) {
        return ((Pointer) intPointer.pointer).getByteBuffer(0, length * INT_SIZE).asIntBuffer();
    }
    public static FloatBuffer floatBuffer(int length, NativePointer floatPointer) {
        return ((Pointer) floatPointer.pointer).getByteBuffer(0, length * FLOAT_SIZE).asFloatBuffer();
    }

}
