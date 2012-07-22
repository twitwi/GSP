/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import fr.prima.gsp.framework.spi.AbstractModule;
import org.bridj.Pointer;

/**
 *
 * @author emonet
 */
public class CharPointerToString extends AbstractModule {

    public void input(Pointer p) {
        output(p.getCString());
    }

    public void output(String s) {
        emitEvent(s);
    }
}
