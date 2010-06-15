/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import com.sun.jna.Pointer;
import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.spi.AbstractModule;

/**
 *
 * @author twilight
 */
public class CharPointerToString extends AbstractModule {

    public void input(NativePointer charPointer) {
        //output(charPointer.getString(0));
        output(((Pointer) charPointer.pointer).getString(0));
    }

    public void output(String s) {
        emitEvent(s);
    }

}
