/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework.nativeutil;

import org.bridj.demangling.Demangler.DemanglingException;
import org.bridj.demangling.Demangler.MemberRef;
import org.bridj.demangling.Demangler.PointerTypeRef;
import org.bridj.demangling.Demangler.SpecialName;
import org.bridj.demangling.Demangler.TypeRef;
import org.bridj.demangling.GCC4Demangler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bridj.demangling.BridjDemanglerExportTools;
import org.bridj.demangling.Demangler.ClassRef;
import org.bridj.demangling.Demangler.Ident;
import org.bridj.demangling.Demangler.IdentLike;
import org.bridj.demangling.Demangler.TemplateArg;

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

    public NativeType demangleType(String libraryName, String type) {
        return demangleTypeImpl(libraryName, type);
    }

    // SPI
    protected abstract NativeSymbolInfo demangleImpl(String libraryName, String symbol);

    protected abstract NativeType demangleTypeImpl(String libraryName, String type);

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
                    res.fullName = fullName(parsed);
                    res.name = res.fullName[res.fullName.length - 1];
                    res.parameterTypes = getParameterTypes(parsed.paramTypes);
                    return res;
                } catch (DemanglingException ex) {
                    //Logger.getLogger(NativeSymbolDemangler.class.getName()).log(Level.SEVERE, null, ex);
                    // TODO could log for demangler improvement
                }
                return null;
            }

            @Override
            protected NativeType demangleTypeImpl(String libraryName, String type) {
                try {
                    GCC4Demangler dem = new GCC4Demangler(null, type); // we can pass null as a library, it is not used
                    TypeRef parsed = dem.parseType();
                    return getParameterType(parsed);
                } catch (DemanglingException ex) {
                    //Logger.getLogger(NativeSymbolDemangler.class.getName()).log(Level.SEVERE, null, ex);
                    // TODO could log for demangler improvement
                }
                return null;
            }

            private String[] fullName(MemberRef mr) {
                //System.err.println(mr.getEnclosingType() + " --- " + mr.getClass());
                List<String> res = new ArrayList<String>();
                if (mr.getEnclosingType() != null) {
                    res.addAll(Arrays.asList(mr.getEnclosingType().getQualifiedName(new StringBuilder(), false).toString().split("[.]")));
                }
                IdentLike memberIdentLike = mr.getMemberName();
                if (memberIdentLike instanceof SpecialName) {
                    switch ((SpecialName) memberIdentLike) {
                        // TODO?
                    }
                    res.add("Special: " + ((SpecialName) memberIdentLike).name());
                } else if (memberIdentLike instanceof Ident) {
                    Ident memberIdent = (Ident) memberIdentLike;
                    res.add((String) memberIdent.toString()); // not much more choice that this toString to access the "simpleName"
                } else {
                    throw new IllegalArgumentException("Wrong type " + memberIdentLike.getClass());
                }
                /*
                if (memberName instanceof List) {
                List<?> l = (List<?>) memberName;
                for (Object o : l) {
                res.add((String) o);
                }
                } else if (memberName instanceof String) {
                res.add((String) memberName);
                } else if (memberName instanceof SpecialName) {
                } else {
                throw new IllegalArgumentException("Wrong type " + memberName.getClass());
                }
                 */
                return res.toArray(new String[res.size()]);
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
            private final Map<String, NativeType> nativeTypes = new HashMap<String, NativeType>() {

                {
                    put("int", NativeType.INT);
                    put("org.bridj.CLong", NativeType.LONG);
                    put("byte", NativeType.CHAR);
                    put("float", NativeType.FLOAT);
                    put("double", NativeType.DOUBLE);
                    put("boolean", NativeType.BOOL);
                }
            };

            private NativeType getParameterType(TypeRef typeRef) {
                if (typeRef == null) {
                    return NativeType.VOID;
                } else if (typeRef instanceof PointerTypeRef) {
                    PointerTypeRef p = (PointerTypeRef) typeRef;
                    return NativeType.pointer(getParameterType(p.pointedType));
                } else {
                    String pType = typeRef.getQualifiedName(new StringBuilder(), true).toString();
                    NativeType basicType = nativeTypes.get(pType);
                    if (basicType != null) {
                        return basicType;
                    } else if (typeRef instanceof ClassRef) {
                        String[] ns = typeRef.getQualifiedName(new StringBuilder(), false).toString().split("[.]");
                        String simpleName = ns[ns.length - 1];
                        ns = Arrays.copyOfRange(ns, 0, ns.length - 1);
                        NativeType[] templ = null;
                        ClassRef cl = (ClassRef) typeRef;
                        if (BridjDemanglerExportTools.getTemplates(cl).length > 0) {
                            List<NativeType> templates = new ArrayList<NativeType>();
                            for (TemplateArg templateArg : BridjDemanglerExportTools.getTemplates(cl)) {
                                if (templateArg instanceof TypeRef) {
                                    templates.add(getParameterType((TypeRef) templateArg));
                                } else {
                                    throw new IllegalArgumentException("Unsupported int template argument");
                                }
                            }
                            templ = templates.toArray(new NativeType[0]);
                        }
                        return NativeType.struct(ns, simpleName, templ);
//                        ClassRef cl = (ClassRef) typeRef;
//                        String simpleName = cl.getIdent().toString().replaceAll("<.*","");
//                        return NativeType.struct(ns, simpleName, templates);
                    }
                    return NativeType.struct(pType);
                }
                /*
                if (res == null) {
                throw new UnsupportedOperationException("TODO for type '" + pType + "' (" + pType.getClass() + ")");
                }*/
            }
        };
    }
}
