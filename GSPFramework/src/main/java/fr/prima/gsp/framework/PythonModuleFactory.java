/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import com.heeere.python26.PyObject;
import fr.prima.gsp.framework.pythonutil.PythonRuntime;
import fr.prima.gsp.framework.pythonutil.PythonRuntime.PythonContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridj.Pointer;
import org.w3c.dom.Element;

/**
 *
 * @author twilight
 */
public class PythonModuleFactory {

    final static List<File> path = new ArrayList<File>();
    public static final String gspModulePath = CModuleFactory.gspModulePath;

    static {
        String module_path = System.getenv(gspModulePath);
        if (module_path != null && !module_path.isEmpty()) {
            String delims = ":";
            String[] tokens = module_path.split(delims);
            for (int i = 0; i < tokens.length; i++) {
                if (!tokens[i].isEmpty()) {
                    File dir = new File(tokens[i]);
                    if (dir.isDirectory()) {
                        path.add(dir);
                    } else {
                        System.err.println("In " + gspModulePath + " found a non directory entry '" + tokens[i] + "' ... ignored");
                        //TODO better way of reporting
                    }
                    //NativeLibrary.addSearchPath(bundleName, tokens[i]);
                }
            }
        }
        path.add(new File("."));
    }

    private void debug(String message) {
        Logger.getLogger(PythonModuleFactory.class.getName()).log(Level.FINEST, message);
    }

    private void log(String message) {
        Logger.getLogger(PythonModuleFactory.class.getName()).log(Level.FINE, message);
    }
    Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    List<Module> modules = new LinkedList<Module>();

    public Module createModule(String bundleName, String moduleTypeName) {
        Bundle bundle = getBundle(bundleName);
        PythonModule module = new PythonModule(bundle, bundleName, moduleTypeName);
        if (module.that != Pointer.NULL) {
//            long moduleNumber = module.number;
//            String moduleId = bundleName + " " + moduleTypeName + " " + moduleNumber;
            modules.add(module);
            return module;
        } else {
            return null;
        }
    }

    private Bundle getBundle(String bundleName) {
        Bundle bundle = bundles.get(bundleName);
        if (bundle == null) {
            File pythonFile = Utilities.findFileInDirectories(bundleName + ".py", path);
            if (pythonFile == null) {
                System.err.println("Could not find python file '" + bundleName + ".py' in your " + gspModulePath + ": " + path.toString());
            }
            try {
                bundle = new Bundle(pythonFile);
                bundles.put(bundleName, bundle);
            } catch (Exception e) {
                // cannot load
                // TODO report better
                System.err.println("Could not load python file '" + pythonFile.getAbsolutePath() + "'");
            }
        }
        return bundle;
    }

    private class Bundle {

        PythonContext context;

        private Bundle(File pythonFile) throws IOException {
            context = PythonRuntime.get().createContext();
            context.executeFile(pythonFile);
        }
    }

    private static class PythonModule extends BaseAbstractModule {

        Pointer<PyObject> that;
        Bundle bundle;

        private PythonModule(Bundle bundle, String bundleName, String moduleTypeName) {
            this.bundle = bundle;
            // TODO make this call safer (to external attack)
            that = this.bundle.context.executeString(moduleTypeName + "()");
        }

        @Override
        protected void initModule() {
        }

        @Override
        protected void stopModule() {
        }

        public void addConnector(String portName, EventReceiver eventReceiver) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void configure(Element conf) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public EventReceiver getEventReceiver(String portName) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
