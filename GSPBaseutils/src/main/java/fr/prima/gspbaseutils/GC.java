/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;

/**
 *
 * @author remonet
 */
public class GC extends AbstractModuleEnablable {

    public void input() {
        if (isEnabled()) {
            System.gc();
            Runtime r = Runtime.getRuntime();
            output(r.totalMemory() - r.freeMemory());
            total(r.totalMemory());
            free(r.freeMemory());
        }
    }
    @ModuleParameter
    public double divisor = 1024. * 1024.;

    private void output(long usedMemory) {
        emitEvent(usedMemory / divisor);
    }

    private void free(long freeMemory) {
        emitEvent(freeMemory / divisor);
    }

    private void total(long totalMemory) {
        emitEvent(totalMemory / divisor);
    }
}
