/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author twilight
 */
public class Clock extends AbstractModuleEnablable {

    @ModuleParameter(initOnly = true)
    public long period = 1000;

    @ModuleParameter(initOnly = true)
    public long warmupDelay = 0;

    @ModuleParameter(initOnly=true)
    public int maxCount = -1;

    @ModuleParameter
    public boolean stopAtEnd = true;

    // this will be automatically injected by the framework
    @ModuleParameter(initOnly=true)
    public Assembly assembly;

    private int count = 0;

    Timer timer = null;

    @Override
    protected void setEnabled(boolean enabled) {
        boolean old = this.enabled;
        super.setEnabled(enabled);
        if (old == this.enabled) {
            return;
        }
        if (this.enabled) {
            startTimer();
        } else {
            timer.cancel();
        }
    }
    
    @Override
    protected synchronized void initModule() {
        startTimer();
    }
    private synchronized void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("Timer Thread for the Clock GSP Module in Baseutils");
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

    public void interrupt() {
        timer.cancel();
        assembly.stop();
    }

    private void output() {
        emitEvent();
        s();
        i();
        count++;
        if (count == maxCount) {
            timer.cancel();
            if (stopAtEnd) {
                assembly.stop();
            }
        }
    }

    private void s() {
        emitEvent("" + count);
    }

    private void i() {
        emitEvent(count);
    }

}
