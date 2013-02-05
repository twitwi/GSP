/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import com.heeere.python27.PyMethodDef;
import com.heeere.python27.PyObject;
import static com.heeere.python27.Python27Library.*;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author remonet
 */
class PythonModuleFactory {

    private boolean inited = false;

    private void lazyInit() {
        if (inited) {
            return;
        }
        // Need LD_PRELOAD...
        BridJ.addNativeLibraryAlias("python27", "python2.7");
        Py_Initialize();
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

    private static class PythonModule extends BaseAbstractModule {

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
                    //System.err.println("CALLED BACK! AMAZING");
                    return pyNone();
                }
            };
            PyMethodDef callbackMethodDef = new PyMethodDef();
            callbackMethodDef.ml_name(s("gsp"));
            callbackMethodDef.ml_meth(frameworkCallback.toPointer());
            Pointer<PyObject> callbackMethodObject = PyCFunction_NewEx(Pointer.pointerTo(callbackMethodDef), pyClassInstance, pyNone());
            PyObject_SetAttrString(pyClassInstance, s("gsp"), callbackMethodObject);
        }

        @Override
        protected void initModule() {
            callIfExists("initModule");
        }

        @Override
        protected void stopModule() {
            callIfExists("stopModule");
        }

        private void pythonCallback(Pointer<PyObject> self, Pointer<PyObject> args) {
            /*
             Object[] eventParameters = new Object[parameters.length / 2];
             String[] eventParametersTypes = new String[parameters.length / 2];
             for (int i = 1; i < parameters.length; i += 2) {
             String type = patchReportedType(parameters[i].getCString());
             Object value = getValueFromNative(type, parameters[i + 1]);
             eventParameters[i / 2] = value;
             eventParametersTypes[i / 2] = type;
             }
             emitNamedEvent(parameters[0].getCString(), eventParameters, eventParametersTypes);
             */
        }

        public EventReceiver getEventReceiver(String portName) {
            final Pointer<PyObject> method = PyObject_GetAttrString(pyClassInstance, s(portName));
            // TODO handle problem here
            return new EventReceiver() {
                public void receiveEvent(Event e) {
                    //Event pythonEvent = e.getPythonView();
                    //bundle.receiveEvent(moduleTypeName, that, portName, e);
                    PyObject_CallFunctionObjArgs(method, eventToParameters(e));
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
                if (attrType == attr) { // handle the Boolean case bool (DUMMY if now)
                    // TODO true,...
                } else {
                    Pointer<PyObject> newVal = PyObject_CallFunctionObjArgs(attrType, sp(text), null);
                    PyObject_SetAttrString(pyClassInstance, s(parameterName), newVal);
                }
                // TODO, maybe use eval as a last resort
            }
        }

        private void callIfExists(String methodName) {
            Pointer<PyObject> method = PyObject_GetAttrString(pyClassInstance, s(methodName));
            if (method != null) {
                PyObject_CallFunctionObjArgs(method, (Object) null);
            }
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
        } else {
            String pyBuildString = typeToBuildValueString.get(o.getClass());
            if (pyBuildString != null) {
                return Py_BuildValue(s(pyBuildString), o);
            } else {
                // wtd? TODO
                return null;
            }
        }
    }
}
