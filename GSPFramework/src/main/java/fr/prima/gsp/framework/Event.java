/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import fr.prima.gsp.framework.nativeutil.NativeType;
import java.nio.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Data inside this structure MUST not be modified! Be a good citizen.
 */
public class Event {
    
    private Object[] information;
    private String[] additionalTypeInformation;

    public Event(Object[] information, String[] additionalTypeInformation) {
        this.information = information;
        this.additionalTypeInformation = additionalTypeInformation;
    }

    public String[] getAdditionalTypeInformation() {
        return additionalTypeInformation;
    }

    public String getAdditionalTypeInformation(int index) {
        return additionalTypeInformation[index];
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
        } else if (NativeLong.class.isAssignableFrom(cl)) {
            res = NativeType.LONG;
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

    private Map<Class, String> nameCache = new HashMap<Class, String>();
// TODO this is dirtily redundant
    private String getTypeName(Class cl) {
        String res = nameCache.get(cl);
        if (res != null) {
            return res;
        }
        if (false) {
        } else if (IntBuffer.class.isAssignableFrom(cl)) {
            res = "Pi";
        } else if (FloatBuffer.class.isAssignableFrom(cl)) {
            res = "Pf";
        } else if (DoubleBuffer.class.isAssignableFrom(cl)) {
            res = "Pd";
        } else if (ByteBuffer.class.isAssignableFrom(cl)) {
            res = "Pc";
        } else if (Integer.class.isAssignableFrom(cl)) {
            res = "i";
        } else if (Float.class.isAssignableFrom(cl)) {
            res = "f";
        } else if (Double.class.isAssignableFrom(cl)) {
            res = "d";
        } else if (Byte.class.isAssignableFrom(cl)) {
            res = "c";
        } else {
            System.err.println("Not found for class: " + cl);
            return null;
        }
        nameCache.put(cl, res);
        return res;
    }

    public Event getCView() {
        //System.err.println(information.length + ": " + Arrays.toString(information) + " of types " + Arrays.toString(additionalTypeInformation));
        Object[] i = new Object[information.length];
        System.arraycopy(information, 0, i, 0, i.length);
        String[] a = new String[additionalTypeInformation.length];
        System.arraycopy(additionalTypeInformation, 0, i, 0, i.length);
        for (int c = 0; c < i.length; c++) {
            if (information[c] instanceof Buffer) {
                Buffer buf = (Buffer) information[c];
                i[c] = new NativePointer(Native.getDirectBufferPointer((Buffer) buf), getType(buf.getClass()));
                a[c] = getTypeName(buf.getClass());
                //System.err.println("Translate buffer to: " + i[c]);
            } else if (information[c] instanceof NativePointer) { // we don't need to do anything to NativePointers
                i[c] = information[c];
                // isn't a[c] deprecated? => Check that and remove all of them if deprecated
            } else {
                i[c] = information[c];
                a[c] = additionalTypeInformation[c] == null ? getTypeName(information[c].getClass()) : additionalTypeInformation[c];
                //System.err.println("Copied: " + i[c] + " of type " + a[c]);
            }
        }
        return new Event(i, a);
    }

}
