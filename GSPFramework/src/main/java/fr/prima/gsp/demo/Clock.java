/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author emonet
 */
public class Clock extends AbstractModuleEnablable {

    @ModuleParameter(initOnly=true)
    public long period = 1000;

    @ModuleParameter(initOnly = true)
    public long warmupDelay = 0;

    Timer timer = new Timer("Timer Thread for the Clock GSP Module");
    @Override
    protected synchronized void initModule() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                output();
            }
        }, warmupDelay, period);
    }

    @Override
    protected synchronized void stopModule() {
        timer.cancel();
    }

    private void output() {
        emitEvent();
    }
}
