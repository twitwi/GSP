/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.spi;

import fr.prima.gsp.framework.ModuleParameter;

/**
 *
 * @author emonet
 */
public class AbstractModuleEnablable extends AbstractModule {

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
