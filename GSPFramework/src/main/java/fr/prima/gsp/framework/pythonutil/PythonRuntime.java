/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework.pythonutil;

import com.heeere.python26.PyObject;
import fr.prima.gsp.framework.Utilities;
import static com.heeere.python26.Python26Library.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.bridj.BridJ;
import org.bridj.Pointer;

/**
 *
 * @author remonet
 */
public class PythonRuntime {

    private PythonRuntime() {
        BridJ.addNativeLibraryAlias("python26", "python2.6");
        Py_Initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                System.err.println("Finalising python");
                Py_Finalize();
            }
        }));
    }
    private static PythonRuntime instance = null;

    public static synchronized PythonRuntime get() {
        if (instance == null) {
            instance = new PythonRuntime();
        }
        return instance;
    }

    public static interface PythonContext {

        void executeFile(File pythonFile) throws IOException;
        public Pointer<PyObject> executeString(String string);
    }

    
    public PythonContext createContext() {
        return new PythonContextImpl();
    }

    private class PythonContextImpl implements PythonContext {

        Pointer<PyObject> module;
        String name;

        public PythonContextImpl() {
            name = String.format("GSP%04d", moduleCounter.incrementAndGet());
            //leak?
            module = PyModule_New(s(name));
            PyModule_AddStringConstant(module, s("__file__"), s("<synthetic-" + name + ">"));
        }

        public void executeFile(File pythonFile) throws IOException {
            String fileContent = Utilities.fileContent(pythonFile);
            executeString(fileContent);
        }

        public Pointer<PyObject> executeString(String code) {
            return PyRun_StringFlags(s(code), Py_file_input, module, null, null);
        }
    }
    private static AtomicInteger moduleCounter = new AtomicInteger(1);

    public static Pointer<Byte> s(String string) {
        return Pointer.pointerToCString(string);
    }
}
