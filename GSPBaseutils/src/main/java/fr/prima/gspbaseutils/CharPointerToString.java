/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.spi.AbstractModule;
import org.bridj.Pointer;

/**
 *
 * @author twilight
 */
public class CharPointerToString extends AbstractModule {

    public void input(NativePointer charPointer) {
        output(convert(charPointer));
    }

    public void output(String s) {
        emitEvent(s);
    }

    public static String convert(NativePointer intPointer) {
        return ((Pointer) intPointer.pointer).getCString();
    }
}
