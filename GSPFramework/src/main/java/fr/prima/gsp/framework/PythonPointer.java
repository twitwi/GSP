/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import com.heeere.python27.PyObject;
import org.bridj.Pointer;

/**
 *
 * @author remonet
 */
public class PythonPointer {
    
    public final Pointer<PyObject> pointer;

    public PythonPointer(Pointer<PyObject> pointer) {
        this.pointer = pointer;
    }
    
}
