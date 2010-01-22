/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import fr.prima.gsp.framework.spi.AbstractModule;
import fr.prima.gsp.framework.ModuleParameter;

/**
 *
 * @author emonet
 */
public class Mul extends AbstractModule {


    @ModuleParameter
    public float by = 1;

    public Mul() {
        allowOpenEvents = true; // to be able to emit "float" event if its a reserved keyword (can be also used in other cases)
    }

    public void input(float f) {
        f = f * by;
        string(f);
        emitNamedEvent("float", f);
    }

    private void string(float val) {
        emitEvent("" + val);
    }

}
