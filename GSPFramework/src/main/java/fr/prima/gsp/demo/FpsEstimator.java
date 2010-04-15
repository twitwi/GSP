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
public class FpsEstimator extends AbstractModule {


    @ModuleParameter(change="processFps")
    public int samples = 1;

    public FpsEstimator() {
        allowOpenEvents = true; // to be able to emit "float" event if its a reserved keyword (can be also used in other cases)
    }

    int count = 0;
    Long last = null;
    public void input(Object o) {
        if (last == null) {
            last = System.currentTimeMillis();
            return;
        }
        if (count >= samples) {
            last = processFps();
        }
        count++;
    }

    private void string(float fps) {
        emitEvent("" + fps);
    }

    public long processFps() {
        if (last == null) return 0;
        long now = System.currentTimeMillis();
        float fps = 1000.f / (now - last) * count;
        count = 0;
        emitNamedEvent("float", fps);
        string(fps);
        return now;
    }

}
