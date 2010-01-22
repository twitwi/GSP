/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import com.sun.jna.Pointer;
import fr.prima.gsp.framework.spi.AbstractModule;

/**
 *
 * @author emonet
 */
public class CharPointerToString extends AbstractModule {

    public void input(Pointer p) {
        output(p.getPointer(0).getString(0));
    }

    public void output(String s) {
        emitEvent(s);
    }
}
