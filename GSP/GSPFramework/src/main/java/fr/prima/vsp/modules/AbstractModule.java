/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules;

import fr.prima.vsp.Module;

/**
 *
 * @author emonet
 */
public abstract class AbstractModule extends Module {

    @ModuleParameter(change="enableChanged")
    public boolean enabled = true;

    public void enabledChanged() {
        setEnabled(enabled);
    }
    
    protected boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
        emitEvent(enabled);
    }

}
