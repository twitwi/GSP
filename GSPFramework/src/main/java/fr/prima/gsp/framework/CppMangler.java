/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author emonet
 */
public class CppMangler {

    public String mangleVoidMethod(String className, String method, Object[] params, String[] additionalParamTypes) {
        //new Function(Function.Type.CppMethod, null, )
        String base = "_ZN" + className.length() + className + method.length() + method;
        String paramSignature = "";
        for (int i = 0; i < params.length; i++) {
            paramSignature += additionalParamTypes[i] != null ? additionalParamTypes[i] : types.get(params[i].getClass());
        }
        if (paramSignature.isEmpty()) paramSignature = "v";
        return base + "E" + paramSignature;
    }

    public String mangleVoidMethod(String className, String method, Object ... params) {
        return mangleVoidMethod(className, method, params, new String[params.length]);
    }

    public String findSingleParameterTypeForSingleVoidMethod(NativeLibrary library, String className, String method) {
        String base = "_ZN" + className.length() + className + method.length() + method;
        for (Class pType : types.keySet()) {
            String test = base + "E" + types.get(pType);
            try {
                library.getFunction(test);
            } catch (UnsatisfiedLinkError e) {
                continue;
            }
            return readableTypes.get(types.get(pType));
        }
        return null;
    }

    static Map<Class, String> types = new LinkedHashMap<Class, String>();
    static Map<String, String> readableTypes = new LinkedHashMap<String, String>();
    static Map<String, String> patches = new HashMap<String, String>();

    class UINT{}
    static {
        types.put(Float.class, "f");
        types.put(Integer.class, "i");
        types.put(String.class, "Pc");
        types.put(Pointer.class, "Pv");
        types.put(Boolean.class, "b");
        types.put(UINT.class, "j");
        
        readableTypes.put("f", "float");
        readableTypes.put("i", "int");
        readableTypes.put("Pc", "char*");
        readableTypes.put("b", "bool");
        readableTypes.put("j", "unsigned int");
        /*
        signatures.put(Void.class, "v");
        signatures.put(Character.class, "c");
        signatures.put(Primitive.SChar, "c");
        signatures.put(Primitive.UChar, "h");
        signatures.put(Primitive.Long, "l");
        signatures.put(Primitive.LongLong, "x");
        signatures.put(Primitive.ULongLong, "y");
        signatures.put(Primitive.ULong, "m");
        signatures.put(Primitive.Int, "i");
        signatures.put(Primitive.UInt, "j");
        signatures.put(Primitive.Short, "s");
        signatures.put(Primitive.UShort, "t");
        signatures.put(Primitive.Bool, "b");
        signatures.put(Primitive.Float, "f");
        signatures.put(Primitive.Double, "d");
        signatures.put(Primitive.LongDouble, "e");
*/
    }

}
