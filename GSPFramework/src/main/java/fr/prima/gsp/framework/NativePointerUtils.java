/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.bridj.Pointer;

/**
 *
 * @author remonet
 */
public class NativePointerUtils {

    //private static final int INT_SIZE = 4; // TODO?
    //private static final int FLOAT_SIZE = 4; // TODO?
    public static ByteBuffer byteBuffer(int length, NativePointer bytePointer) {
        return ((Pointer) bytePointer.pointer).getByteBuffer(length);
    }

    public static IntBuffer intBuffer(int length, NativePointer intPointer) {
        //return ((Pointer) intPointer.pointer).getByteBuffer(length * INT_SIZE).asIntBuffer();
        return ((Pointer) intPointer.pointer).getIntBuffer(length);
    }

    public static FloatBuffer floatBuffer(int length, NativePointer floatPointer) {
        //return ((Pointer) floatPointer.pointer).getByteBuffer(length * FLOAT_SIZE).asFloatBuffer();
        return ((Pointer) floatPointer.pointer).getFloatBuffer(length);
    }

    public static long address(NativePointer pointer) {
        return ((Pointer) pointer.pointer).getPeer();
    }
}
