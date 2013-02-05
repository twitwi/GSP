/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import fr.prima.gsp.framework.nativeutil.NativeType;
import java.nio.*;
import java.util.HashMap;
import java.util.Map;
import org.bridj.Pointer;

/**
 *
 * Data inside this structure MUST not be modified! Be a good citizen.
 */
public class Event {

    private Object[] information;

    public Event(Object[] information) {
        this.information = information;
    }

    public Object[] getInformation() {
        return information;
    }

    public Object getInformation(int index) {
        return information[index];
    }
    private Map<Class, NativeType> cache = new HashMap<Class, NativeType>();

    private NativeType getType(Class cl) {
        NativeType res = cache.get(cl);
        if (res != null) {
            return res;
        }
        if (false) {
        } else if (IntBuffer.class.isAssignableFrom(cl)) {
            res = NativeType.pointer(NativeType.INT);
        } else if (FloatBuffer.class.isAssignableFrom(cl)) {
            res = NativeType.pointer(NativeType.FLOAT);
        } else if (DoubleBuffer.class.isAssignableFrom(cl)) {
            res = NativeType.pointer(NativeType.DOUBLE);
        } else if (ByteBuffer.class.isAssignableFrom(cl)) {
            res = NativeType.CHAR_POINTER;
        } else if (Integer.class.isAssignableFrom(cl)) {
            res = NativeType.INT;
        } else if (Float.class.isAssignableFrom(cl)) {
            res = NativeType.FLOAT;
        } else if (Double.class.isAssignableFrom(cl)) {
            res = NativeType.DOUBLE;
        } else if (Byte.class.isAssignableFrom(cl)) {
            res = NativeType.CHAR;
        } else if (Boolean.class.isAssignableFrom(cl)) {
            res = NativeType.BOOL;
        } else {
            System.err.println("Not found for class: " + cl);
            return null;
        }
        cache.put(cl, res);
        return res;
    }

    // TODO: maybe should not be in this class, maybe should not be split in this and the python one, maybe should be a common utility class
    public Event getCView() {
        //System.err.println(information.length + ": " + Arrays.toString(information) + " of types " + Arrays.toString(additionalTypeInformation));
        Object[] i = new Object[information.length];
        System.arraycopy(information, 0, i, 0, i.length);
        for (int c = 0; c < i.length; c++) {
            if (information[c] instanceof Buffer) {
                Buffer buf = (Buffer) information[c];
                org.bridj.Pointer<Object> p = null;
                i[c] = new NativePointer(Pointer.pointerToBuffer(buf), getType(buf.getClass()));
            } else { // we don't need to do anything to NativePointers or other types
                i[c] = information[c];
            }
        }
        return new Event(i);
    }

}
