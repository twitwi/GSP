/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import fr.prima.gsp.framework.nativeutil.NativeFunctionFinder;
import fr.prima.gsp.framework.nativeutil.NativeSymbolDemangler;
import fr.prima.gsp.framework.nativeutil.NativeType;
import fr.prima.gsp.framework.nativeutil.NativeSymbolInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author emonet
 */
public class CModuleFactory {

    private void debug(String message) {
        Logger.getLogger(CModuleFactory.class.getName()).log(Level.FINEST, message);
    }
    private void log(String message) {
        Logger.getLogger(CModuleFactory.class.getName()).log(Level.FINE, message);
    }

    {
        // code to avoid freeze in library loading (don't know why)
        // we just access to the Native class before anything else
        if (Native.POINTER_SIZE < 4 || NativeLibrary.class.getName().isEmpty()) {
            System.err.println("This is quite strange");
        }
    }

    Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    List<Module> modules = new LinkedList<Module>();
    CModuleFactory() {
    }


    public Module createModule(String bundleName, String moduleTypeName) {
        //System.err.println("loading " + bundleName);
        Bundle bundle = getBundle(bundleName);
        if (bundle == null) {
            return null;
        }
        //System.err.println("creating "+bundleName+":"+moduleTypeName);
        CModule newModule = createCModule(bundleName, moduleTypeName);
        //System.err.println("done");
        return newModule;
    }

    private Bundle getBundle(String bundleName) {
        Bundle bundle = bundles.get(bundleName);
        if (bundle == null) {
            NativeLibrary.addSearchPath(bundleName, ".");

            String module_path = System.getenv("GSP_MODULES_PATH");
            if (module_path != null && !module_path.isEmpty()) {
                String delims = ":";
                String[] tokens = module_path.split(delims);
                for (int i = 0; i < tokens.length; i++) {
                    if(!tokens[i].isEmpty())
                        NativeLibrary.addSearchPath(bundleName, tokens[i]);
                }
            }

            module_path = System.getProperty("gsp.module.path");
            if (module_path != null && !module_path.isEmpty()) {
                String delims = ":";
                String[] tokens = module_path.split(delims);
                for (int i = 0; i < tokens.length; i++) {
                    if(!tokens[i].isEmpty())
                        System.err.println("ADDED "+tokens[i]);
                        NativeLibrary.addSearchPath(bundleName, tokens[i]);
                }
            }

            try {
                bundle = new Bundle(NativeLibrary.getInstance(bundleName), NativeFunctionFinder.create(bundleName));
                bundles.put(bundleName, bundle);
            } catch (Exception e) {
                // TODO test NativeFunctionFinder also (add exceptions in it, or in the create or ...)

                // cannot load
                // TODO report
            }
        }
        return bundle;
    }

    private CModule createCModule(String bundleName, String moduleTypeName) {
        Bundle bundle = bundles.get(bundleName);
        CModule module = new CModule(bundle, bundleName, moduleTypeName);
        if (module.that != Pointer.NULL) {
//            long moduleNumber = module.number;
//            String moduleId = bundleName + " " + moduleTypeName + " " + moduleNumber;
            modules.add(module);
            return module;
        } else {
            return null;
        }
    }

    public static String capFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String setter(String parameterName) {
        return "set" + capFirst(parameterName);
    }
    
    private static String sep = "__v__";

    private class Bundle {

        NativeLibrary library;
        NativeFunctionFinder finder;

        private Bundle(NativeLibrary library,  NativeFunctionFinder finder) {
            this.library = library;
            this.finder = finder;
        }

        private Pointer createModule(String moduleTypeName, FrameworkCallback f) {
            //Pointer res = f(moduleTypeName, "create").invokePointer(new Object[]{f.toPointer()});
            Pointer res = f(moduleTypeName, "create").invokePointer(new Object[]{f});
            if (res != Pointer.NULL) {
                callCFunctionOptionally(moduleTypeName, "created", res);
            }
            return res;
        }

        private void setModuleParameter(String moduleTypeName, Pointer that, String parameterName, Object value) {
            callCFunctionOrCppMethodOptionally(moduleTypeName, "set" + sep + parameterName, setter(parameterName), that, value);
        }

        private void initModule(String moduleTypeName, Pointer that) {
            callCFunctionOrElseCppMethodOptionallyIsCppCalled(moduleTypeName, "init", "initModule", that);
        }

        private void stopModule(String moduleTypeName, Pointer that) {
            if (callCFunctionOrElseCppMethodOptionallyIsCppCalled(moduleTypeName, "stop", "stopModule", that)) {
                f(moduleTypeName, "delete").invoke(new Object[]{that});
            }
        }

        private void receiveEvent(String moduleTypeName, Pointer that, String portName, Event e) {
            e = e.getCView();
            Object[] information = e.getInformation();
            Object[] thatAndParams = new Object[information.length + 1];
            System.arraycopy(information, 0, thatAndParams, 1, information.length);
            thatAndParams[0] = that;
            if (!callCFunctionOrCppMethodOptionally(moduleTypeName, "event" + sep + portName, portName, thatAndParams)) {
                throw new RuntimeException("cannot receive this event " + moduleTypeName + "." + portName + Arrays.asList(e.getInformation()) + " " + Arrays.asList(e.getAdditionalTypeInformation()));
            }
        }

        private Function f(String functionName) {
            return f(null, functionName);
        }
        private Function f(String prefix, String functionName) {
            return library.getFunction((prefix == null ? "" : prefix + sep) + functionName);
        }

        /**
         * @return whether the cpp method was called (we need this particular case so we take it :) )
         */
        private boolean callCFunctionOrElseCppMethodOptionallyIsCppCalled(String moduleTypeName, String c, String cpp, Object that) {
            // try c function
            debug("Trying to call C function '" + c + "'");
            if (!callCFunctionOptionally(moduleTypeName, c, that)) {
                debug("Falling back on C++ method '" + cpp + "'");
                // try c++ method
                return callCppMethodOptionally(moduleTypeName, cpp, that);
            }
            return false;
        }

        /**
         * @return whether something has been called
         */
        private boolean callCFunctionOrCppMethodOptionally(String moduleTypeName, String c, String cpp, Object... thatAndParams) {
            debug("Trying to call C function '" + c + "'");
            if (!callCFunctionOptionally(moduleTypeName, c, thatAndParams)) {
                debug("Falling back on C++ method '" + cpp + "'");
                return callCppMethodOptionally(moduleTypeName, cpp, thatAndParams);
            }
            return true;
        }

        private boolean callCFunctionOptionally(String prefix, String functionName, Object... params) {
            try {
                f(prefix, functionName).invoke(resolvePointers(params));
                return true;
            } catch (UnsatisfiedLinkError err) {
                debug("Failed calling C function '" + functionName + "'");
                // swallow exception
                return false;
            }
        }

        private boolean callCppMethodOptionally(String moduleTypeName, String cpp, Object... thatAndParams) {
            try {
                NativeType[] types = new NativeType[thatAndParams.length - 1];
                for (int i = 0; i < types.length; i++) {
                    types[i] = getNativeTypeForObject(thatAndParams[i+1]);
                }
                NativeSymbolInfo info = finder.findAnyMethodForParameters(moduleTypeName, cpp, types);
                //System.err.println("FOR: " + moduleTypeName + " " + cpp + " " + Arrays.asList(types) + " -> " + info);
                debug("Mangled cpp method '" + moduleTypeName + "::" + cpp + "' as '" + (info == null ? "MISSING" : info.mangledName) + "'");
                if (info != null) {
                    f(info.mangledName).invoke(resolvePointers(thatAndParams));
                    return true;
                }
            } catch (UnsatisfiedLinkError err2) {
                debug("Failed calling on C++ method '" + moduleTypeName + "::" + cpp + "'");
                // swallow exception
            }
            return false;
        }

        private Object[] resolvePointers(Object[] thatAndParams) {
            Object[] res = new Object[thatAndParams.length];
            System.arraycopy(thatAndParams, 0, res, 0, thatAndParams.length);
            for (int i = 0; i < res.length; i++) {
                if (res[i] instanceof NativePointer) {
                    res[i] = ((NativePointer) res[i]).pointer;
                }
            }
            return res;
        }

    }

    //private static abstract class FrameworkCallback extends Callback {
    private static interface FrameworkCallback extends Callback {
        public abstract void callback(String commandName, Pointer parameters);
    }

    private static Pointer[] extractNullTerminatedPointerArray(Pointer args) {
        return args.getPointerArray(0);
        /*
        int pointerSize = JNI.POINTER_SIZE;
        ArrayList<Pointer> res = new ArrayList<Pointer>();
        Pointer cur;
        int index = 0;
        while (Pointer.NULL != (cur = args.getPointer(index * pointerSize))) {
            res.add(cur);
            index++;
        }
        return res.toArray(new Pointer[res.size()]);
         */
    }

    private NativeType getNativeTypeForObject(Object o) {
        if (o == null) {
            return NativeType.VOID_POINTER;
        } else if (o instanceof NativePointer) {
            return ((NativePointer) o).nativeType;
        } else {
            return javaTypeToNativeType.get(o.getClass());
        }
    }
    private Map<Class<?>, NativeType> javaTypeToNativeType = new HashMap<Class<?>, NativeType>() {
        {
            put(Integer.TYPE, NativeType.INT);
            put(Integer.class, NativeType.INT);
            put(NativeLong.class, NativeType.LONG);
            put(Float.TYPE, NativeType.FLOAT);
            put(Float.class, NativeType.FLOAT);
            put(Double.TYPE, NativeType.DOUBLE);
            put(Double.class, NativeType.DOUBLE);
            put(Boolean.TYPE, NativeType.BOOL);
            put(Boolean.class, NativeType.BOOL);
            put(String.class, NativeType.CHAR_POINTER);
        }
    };

    private static class CModule extends BaseAbstractModule implements Module {

        Pointer that;
        FrameworkCallback framework;

        Bundle bundle;
        String bundleName;
        String moduleTypeName;
        Map<String, String> parameterTypes = new HashMap<String, String>();
        private Set<String> nonParameters = new HashSet<String>() {{
            add("id");
            add("type");
        }};

        private CModule(Bundle bundle, String bundleName, String moduleTypeName) {
            this.bundle = bundle;
            this.bundleName = bundleName;
            this.moduleTypeName = moduleTypeName;
            this.framework = new FrameworkCallback() {
                public void callback(String commandName, Pointer parameters) {
                    cCallback(commandName, extractNullTerminatedPointerArray(parameters));
                }
            };
            that = bundle.createModule(moduleTypeName, framework);
            if (that == Pointer.NULL) return;
        }

        public EventReceiver getEventReceiver(final String portName) {
            return new EventReceiver() {
                public void receiveEvent(Event e) {
                    bundle.receiveEvent(moduleTypeName, that, portName, e);
                }
            };
        }

        public void addConnector(String portName, EventReceiver eventReceiver) {
            listenersFor(portName).add(eventReceiver);
        }

        public void configure(Element conf) {
            NamedNodeMap attributes = conf.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node node = attributes.item(i);
                String parameterName = node.getNodeName();
                if (nonParameters.contains(parameterName)) {
                    continue;
                }
                String text = node.getTextContent();
                setParameter(parameterName, text);
            }
        }

        @Override
        protected void initModule() {
            bundle.initModule(moduleTypeName, that);
        }

        @Override
        protected void stopModule() {
            bundle.stopModule(moduleTypeName, that);
        }

        // callback from C
        private void cCallback(String commandName, Pointer[] parameters) {
            if ("param".equals(commandName)) {
                // this is of pure C (non C++)
                parameterTypes.put(parameters[1].getString(0), parameters[2].getString(0));
            } else if ("emit".equals(commandName)) {
                Object[] eventParameters = new Object[parameters.length / 2];
                String[] eventParametersTypes = new String[parameters.length / 2];
                for (int i = 1; i < parameters.length; i += 2) {
                    String type = patchReportedType(parameters[i].getString(0));
                    Object value = getValueFromNative(type, parameters[i + 1]);
                    eventParameters[i / 2] = value;
                    eventParametersTypes[i / 2] = type;
                }
                emitNamedEvent(parameters[0].getString(0), eventParameters, eventParametersTypes);
            } else {
                System.err.println("Unsupported callback type " + commandName);
            }
        }



        // used for xml parameter interpretation
        private Object getNativeValueFromString(NativeType type, String text) {
            // could we reuse some mapping for a lib? (not that useful)
            try {
                return stringToNatives.get(type).toNative(text);
            } catch (NullPointerException ex) {
                System.err.println("problem with type '" + type + "' to interpret '" + text + "'");
                throw ex;
            } catch (RuntimeException ex) {
                System.err.println("problem with type '" + type + "' to interpret '" + text + "'");
                throw ex;
            }
        }

        private void setParameter(String parameterName, String text) {
            String registeredType = parameterTypes.get(parameterName);
            Object value;
            if (registeredType != null) { // registered from plain C
                value = getNativeValueFromString(cStringTypeToNativeType.get(registeredType), text);
            } else {
                NativeType type = findBestParameterType(parameterName, text);
                if (type == null) {
                    throw new RuntimeException("Could not find any native type (setter) for '" + parameterName + "' (with value '" + text + "')");
                }
                value = getNativeValueFromString(type, text);
            }
            bundle.setModuleParameter(moduleTypeName, that, parameterName, value);
            // could cache here
        }
        private NativeType findBestParameterType(String parameterName, String text) {
            // TODO could infer type more precisely from text value (currently ignored)
            for (NativeType t : Arrays.asList(NativeType.INT, NativeType.LONG, NativeType.DOUBLE, NativeType.FLOAT, NativeType.BOOL, NativeType.CHAR_POINTER)) {
                NativeSymbolInfo f = bundle.finder.findAnyMethodForParameters(moduleTypeName, setter(parameterName), t);
                if (f != null) {
                    return t;
                }
            }
            return null;
        }


        private Map<String, String> cTypeToTypeid = new HashMap<String, String>() {{
            put("int", "i");
            put("unsigned int", "j");
            put("float", "f");
            put("long", "l");
            put("unsigned long", "m");
            put("double", "d");
            put("char", "c");
            put("bool", "b");
        }};
        private String patchReportedType(String type) {
            String prefix = "";
            while (true) {
                if (type.startsWith("A")) {
                    prefix += "P";
                    type = type.replaceFirst("A\\d+_", "");
                } else if (type.startsWith("P")) {
                    prefix += "P";
                    type = type.substring(1);
                } else if (type.endsWith("*")) {
                    prefix += "P";
                    type = type.substring(0, type.length() - 1);
                } else {
                    break;
                }
            }
            type = type.trim();
            String res = cTypeToTypeid.get(type);
            if (res != null) type = res;
            return prefix + type;
        }

        private static Map<String, NativeType> cStringTypeToNativeType = new HashMap<String, NativeType>() {
            {
                put("char*", NativeType.CHAR_POINTER);
                put("float", NativeType.FLOAT);
                put("double", NativeType.DOUBLE);
                put("int", NativeType.INT);
                put("long", NativeType.LONG);
                put("unsigned long", NativeType.LONG);
                put("bool", NativeType.BOOL);
                //put("", NativeType.);
            }
        };

        private static interface StringToNative {
            Object toNative(String text);
        }
        private static Map<NativeType, StringToNative> stringToNatives = new HashMap<NativeType, StringToNative>() {
            {
                put(null, new StringToNative() {
                    public Object toNative(String text) {
                        throw new RuntimeException("null access in stringToNatives (missing mapping in cStringTypeToNativeType?)");
                    }
                });
                put(NativeType.CHAR_POINTER, new StringToNative() {
                    public Object toNative(String text) {
                        return text;
                    }
                });
                put(NativeType.FLOAT, new StringToNative() {
                    public Object toNative(String text) {
                        return Float.parseFloat(text);
                    }
                });
                put(NativeType.DOUBLE, new StringToNative() {
                    public Object toNative(String text) {
                        return Double.parseDouble(text);
                    }
                });
                put(NativeType.INT, new StringToNative() {
                    public Object toNative(String text) {
                        return Integer.parseInt(text);
                    }
                });
                put(NativeType.LONG, new StringToNative() {
                    public Object toNative(String text) {
                        return new NativeLong(Long.parseLong(text));
                    }
                });
                put(NativeType.BOOL, new StringToNative() {
                    public Object toNative(String text) {
                        return Boolean.parseBoolean(text);
                    }
                });
            }
        };
        private static interface NativeInterpreter {
            Object interpret(Pointer pointer);
        }
        private static Map<String, NativeInterpreter> nativeInterpreters = new HashMap<String, NativeInterpreter>() {
            {
                put("f", new NativeInterpreter() {
                    public Object interpret(Pointer pointer) {
                        return pointer.getFloat(0);
                    }
                });
                put("d", new NativeInterpreter() {
                    public Object interpret(Pointer pointer) {
                        return pointer.getDouble(0);
                    }
                });
                put("i", new NativeInterpreter() {
                    public Object interpret(Pointer pointer) {
                        return pointer.getInt(0);
                    }
                });
                put("j", new NativeInterpreter() {
                    public Object interpret(Pointer pointer) {
                        return pointer.getInt(0);
                    }
                });
                put("l", new NativeInterpreter() {
                    public Object interpret(Pointer pointer) {
                        return pointer.getNativeLong(0);
                    }
                });
                put("m", new NativeInterpreter() {
                    public Object interpret(Pointer pointer) {
                        return pointer.getNativeLong(0);
                    }
                });
            }
        };
        private Object getValueFromNative(String type, Pointer pointer) {
            NativeSymbolDemangler dem = NativeSymbolDemangler.create();
            NativeType t = dem.demangleType(bundleName, type);
            if (t == null) {
                throw new RuntimeException("Null native type for " + bundleName + " type " + type);
            }
            if (t.isPointer()) {
                return new NativePointer(pointer.getPointer(0), t);
            }
            if (t.isCompound()) {
                throw new UnsupportedOperationException("Unsupported direct stuct passing (use pointer or references): " + t.toString());
            }
            // could find a way to reuse jna mapping but I didn't managed to :(
            try {
                return nativeInterpreters.get(type).interpret(pointer);
            } catch (NullPointerException ex) {
                throw new RuntimeException("NPE while reading value for native type '" + type + "'", ex);
            }
        }

    }
}
