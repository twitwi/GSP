/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import fr.prima.gsp.framework.nativeutil.NativeType;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author twilight
 */
public class NativePointer {

    public final Object pointer;
    public final NativeType nativeType;

    public NativePointer(Object pointer, NativeType nativeType) {
        this.pointer = pointer;
        this.nativeType = nativeType;
    }

    @Override
    public String toString() {
        return "NP<" + nativeType.toString() + ">(" + pointer + ")";
    }

    public IntBuffer toIntBuffer(int length) {
        return NativePointerUtils.intBuffer(length, this);
    }
    public FloatBuffer toFloatBuffer(int length) {
        return NativePointerUtils.floatBuffer(length, this);
    }

}
