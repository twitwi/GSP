/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import com.heeere.python27.PyMethodDef;
import com.heeere.python27.PyObject;
import static com.heeere.python27.Python27Library.*;
import com.heeere.python27.Python27Library.Py_ssize_t;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bridj.BridJ;
import org.bridj.CLong;
import org.bridj.Pointer;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author remonet
 * 
 * 
 * TODO: each call to python should test for return and print/clear the errors
 */
class PythonModuleFactory {

    private boolean inited = false;
    Pointer<PyObject> pyTrue;
    Pointer<PyObject> pyFalse;
    Pointer<PyObject> pyBool;

    private void lazyInit() {
        if (inited) {
            return;
        }
        // Need LD_PRELOAD...
        BridJ.addNativeLibraryAlias("python27", "python2.7");
        Py_Initialize();
        Pointer<PyObject> dict = PyImport_GetModuleDict();
        Pointer<PyObject> builtin = PyDict_GetItemString(dict, s("__builtin__"));
        pyTrue = PyObject_GetAttrString(builtin, s("True"));
        pyFalse = PyObject_GetAttrString(builtin, s("False"));
        pyBool = PyObject_Type(pyTrue);
        inited = true;
    }
    // NOTE: what is called a module in Python (that can be imported) is called a Bundle here, to avoid conflict with PythonModule (for the gsp)
    Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    List<Module> modules = new LinkedList<Module>();

    public Module createModule(String pythonModuleName, String typeName) {
        lazyInit();
        Bundle bundle = getBundle(pythonModuleName);
        PythonModule module = new PythonModule(bundle, pythonModuleName, typeName);
        modules.add(module);
        return module;
    }

    private Bundle getBundle(String pythonModuleName) {
        Bundle bundle = bundles.get(pythonModuleName);
        if (bundle == null) {
            bundle = new Bundle(pythonModuleName);
            bundles.put(pythonModuleName, bundle);
        }
        return bundle;
    }

    public static Pointer<Byte> s(String string) {
        return Pointer.pointerToCString(string);
    }

    public static Pointer<PyObject> sp(String string) {
        return Py_BuildValue(s("s"), s(string));
    }

    public static Pointer<PyObject> pyNone() {
        return Py_BuildValue(s(""));
    }

    private static class Bundle {

        String name;
        Pointer<PyObject> pythonModule;
        Pointer<PyObject> pythonModuleDict;

        private Bundle(String pythonModuleName) {
            this.name = pythonModuleName;
            this.pythonModule = PyImport_ImportModule(s(pythonModuleName));
            this.pythonModuleDict = PyModule_GetDict(pythonModule);
        }
    }

    public static abstract class FrameworkCallback extends org.bridj.Callback {

        public abstract Pointer<PyObject> callback(Pointer<PyObject> self, Pointer<PyObject> args, Pointer<PyObject> keywds);
        /*  static PyObject *keywdarg_parrot(PyObject *self, PyObject *args, PyObject *keywds)  */
    }

    private class PythonModule extends BaseAbstractModule {

        Bundle bundle;
        //String pyClassName;
        //Pointer<PyObject> pyClass;
        Pointer<PyObject> pyClassInstance;
        //FrameworkCallback frameworkCallback;
        //PyMethodDef callbackMethodDef = new PyMethodDef();
        //Pointer<PyObject> callbackMethodObject;

        public PythonModule(Bundle bundle, String pythonModuleName, String typeName) {
            this.bundle = bundle;
            Pointer<PyObject> pyClass = PyDict_GetItemString(bundle.pythonModuleDict, s(typeName));
            pyClassInstance = PyObject_CallFunctionObjArgs(pyClass, (Object) null);
            FrameworkCallback frameworkCallback = new FrameworkCallback() {
                @Override
                public Pointer<PyObject> callback(Pointer<PyObject> self, Pointer<PyObject> args, Pointer<PyObject> keywds) {
                    pythonCallback(self, args);
                    return pyNone();
                }
            };
            PyMethodDef callbackMethodDef = new PyMethodDef();
            callbackMethodDef.ml_name(s("emitNamedEvent"));
            callbackMethodDef.ml_meth(frameworkCallback.toPointer());
            callbackMethodDef.ml_flags(METH_VARARGS);
            Pointer<PyObject> callbackMethodObject = PyCFunction_NewEx(Pointer.pointerTo(callbackMethodDef), pyClassInstance, pyNone());
            PyObject_SetAttrString(pyClassInstance, s("emitNamedEvent"), callbackMethodObject);
        }

        @Override
        protected void initModule() {
            callIfExists("initModule");
        }

        @Override
        protected void stopModule() {
            callIfExists("stopModule");
        }

        private String repeat(String what, int times) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < times; i++) {
                res.append(what);
            }
            return res.toString();
        }

        private void pythonCallback(Pointer<PyObject> self, Pointer<PyObject> args) {
            int nArgs = nArgs(args);
            Pointer<CLong>[] parametersPointers = new Pointer[nArgs];
            for (int i = 0; i < parametersPointers.length; i++) {
                parametersPointers[i] = Pointer.allocateCLong();
            }
            int res;
            res = PyArg_ParseTuple(args, s(repeat("O", nArgs)), (Object[]) parametersPointers);
            Pointer<PyObject> obj = (Pointer<PyObject>) Pointer.pointerToAddress(parametersPointers[0].getCLong());
            String eventName = PyString_AsStringJava(obj);
            PythonPointer[] eventParameters = new PythonPointer[nArgs - 1];
            for (int i = 0; i < eventParameters.length; i++) {
                eventParameters[i] = new PythonPointer((Pointer<PyObject>) Pointer.pointerToAddress(parametersPointers[i + 1].getCLong()));
            }
            emitNamedEvent(eventName, (Object[]) eventParameters);
        }

        public EventReceiver getEventReceiver(String portName) {
            final Pointer<PyObject> method = PyObject_GetAttrString(pyClassInstance, s(portName));
            // TODO handle problem here
            return new EventReceiver() {
                public void receiveEvent(Event e) {
                    Pointer<PyObject> res = PyObject_CallFunctionObjArgs(method, eventToParameters(e));
                    if (res == Pointer.NULL) {
                        PyErr_Print();
                    }
                }
            };
        }

        private Object[] eventToParameters(Event e) {
            Object[] ei = e.getInformation();
            Object[] res = new Object[ei.length + 1]; // add a null at the end
            for (int c = 0; c < ei.length; c++) {
                res[c] = getPythonView(ei[c]);
            }
            return res;
        }

        public void addConnector(String portName, EventReceiver eventReceiver) {
            listenersFor(portName).add(eventReceiver);
        }

        public void configure(Element conf) {
            NamedNodeMap attributes = conf.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node node = attributes.item(i);
                String parameterName = node.getNodeName();
                if (CModuleFactory.nonParameters.contains(parameterName)) {
                    continue;
                }
                String text = node.getTextContent();
                setParameter(parameterName, text);
            }
        }

        private void setParameter(String parameterName, String text) {
            Pointer<PyObject> attr = PyObject_GetAttrString(pyClassInstance, s(parameterName));
            if (attr == null) {
                System.err.println("ERROR: could not find attribute '" + parameterName + "' in python object"); // TODO handle errors
            } else {
                Pointer<PyObject> attrType = PyObject_Type(attr);
                Pointer<PyObject> newVal;
                if (attrType.equals(pyBool)) { // handle the Boolean case bool (DUMMY if now)
                    newVal = Boolean.parseBoolean(text) ? pyTrue : pyFalse;
                } else {
                    newVal = PyObject_CallFunctionObjArgs(attrType, sp(text), null);
                }
                PyObject_SetAttrString(pyClassInstance, s(parameterName), newVal);
                Pointer<PyObject> notificationMethod = PyObject_GetAttrString(pyClassInstance, s(parameterName + "Changed"));
                if (notificationMethod != pyNone()) {
                    PyObject_CallFunctionObjArgs(notificationMethod, attr, newVal);
                }
                // TODO, maybe use eval as a last resort
            }
        }

        private void callIfExists(String methodName) {
            Pointer<PyObject> method = PyObject_GetAttrString(pyClassInstance, s(methodName));
            if (method != Pointer.NULL) {
                PyObject_CallFunctionObjArgs(method, (Object) null);
            } else {
                PyErr_Clear();
            }
        }

        private int nArgs(Pointer<PyObject> args) {
            Pointer<Py_ssize_t> nArgs = PyTuple_Size(args);
            // buggous wrapper?
            return (int) nArgs.getPeer();
            //System.err.println("+++++++++++++++++++++++++++++ "+Long.toHexString(nArgs.getPeer()));
            //return (int) nArgs.getSizeT();
        }
    }
    private static Map<Class, String> typeToBuildValueString = new IdentityHashMap<Class, String>() {
        {
            put(Integer.class, "i");
            put(Long.class, "l");
            put(Short.class, "h");
            put(Float.class, "f");
            put(Double.class, "d");
        }
    };

    private static Object getPythonView(Object o) {
        if (o == null) {
            return pyNone();
        } else if (o instanceof String) {
            return Py_BuildValue(s("s"), s((String) o));
        } else if (o instanceof PythonPointer) {
            return ((PythonPointer) o).pointer;
        } else if (o instanceof NativePointer) {
            return Py_BuildValue(s("l"), NativePointerUtils.address((NativePointer) o));
        } else {
            String pyBuildString = typeToBuildValueString.get(o.getClass());
            if (pyBuildString != null) {
                return Py_BuildValue(s(pyBuildString), o);
            } else {
                // wtd? TODO
                System.err.println("UNHANDLED TYPE " + o.getClass());
                return null;
            }
        }
    }

    private String PyString_AsStringJava(Pointer<PyObject> str) {
        Pointer<Byte> arr = PyString_AsString(str);
        return arr.getCString();
    }
}
