/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.nativeutil;

import com.bridj.BridJ;
import com.bridj.Demangler.DemanglingException;
import com.bridj.Demangler.MemberRef;
import com.bridj.Demangler.PointerTypeRef;
import com.bridj.Demangler.SpecialName;
import com.bridj.Demangler.TypeRef;
import com.bridj.cpp.GCC4Demangler;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author remonet
 */
public abstract class NativeSymbolDemangler {

    public static NativeSymbolDemangler create() {
        return createBridjGccBasedDemangler();
    }

    // API
    public final NativeSymbolInfo demangle(String libraryName, String symbol) {
        return demangleImpl(libraryName, symbol);
    }
    
    // SPI
    protected abstract NativeSymbolInfo demangleImpl(String libraryName, String symbol);

    // inner
    static NativeSymbolDemangler createBridjGccBasedDemangler() {
        return new NativeSymbolDemangler() {

            @Override
            protected NativeSymbolInfo demangleImpl(String libraryName, String symbol) {
                try {
                    NativeSymbolInfo res = new NativeSymbolInfo();
                    res.mangledName = symbol;
                    GCC4Demangler dem = new GCC4Demangler(null, symbol); // we can pass null as a library, it is not used
                    MemberRef parsed = dem.parseSymbol();
                    if (parsed == null) {
                        // not a c++ thing?
                        return null;
                    }
                    res.fullName = fullName(parsed.getMemberName());
                    res.name = res.fullName[res.fullName.length-1];
                    res.parameterTypes = getParameterTypes(parsed.paramTypes);
                    return res;
                } catch (DemanglingException ex) {
                    //Logger.getLogger(NativeSymbolDemangler.class.getName()).log(Level.SEVERE, null, ex);
                    // TODO could log for demangler improvement
                }
                return null;
            }

            private String[] fullName(Object memberName) {
                if (memberName instanceof List) {
                    List<?> l = (List<?>) memberName;
                    String[] res = new String[l.size()];
                    for (int i = 0; i < res.length; i++) {
                        res[i] = (String) l.get(i);
                    }
                    return res;
                } else if (memberName instanceof String) {
                    return new String[]{(String) memberName};
                } else if (memberName instanceof SpecialName) {
                    return new String[]{"Special: " + ((SpecialName) memberName).name()};
                } else {
                    throw new IllegalArgumentException("Wrong type " + memberName.getClass());
                }
            }


            private NativeType[] getParameterTypes(TypeRef[] paramTypes) {
                if (paramTypes == null) {
                    return new NativeType[0];
                }
                NativeType[] res = new NativeType[paramTypes.length];
                for (int i = 0; i < res.length; i++) {
                    res[i] = getParameterType(paramTypes[i]);
                }
                return res;
            }

            private final Map<String, NativeType> nativeTypes = new HashMap<String, NativeType>() {{
                put("int", NativeType.INT);
                put("byte", NativeType.CHAR);
                put("float", NativeType.FLOAT);
                put("boolean", NativeType.BOOL);
            }};

            private NativeType getParameterType(TypeRef typeRef) {
                String pType = typeRef.getQualifiedName(new StringBuilder(), true).toString();
                NativeType res = nativeTypes.get(pType);
                if (res == null && "com.bridj.Pointer".equals(pType)) {
                    PointerTypeRef p = (PointerTypeRef) typeRef;
                    return NativeType.pointer(getParameterType(p.pointedType));
                }
                if (res == null) {
                    throw new UnsupportedOperationException("TODO for type '" + pType + "' (" + pType.getClass() + ")");
                }
                return res;
            }

        };
    }

}
