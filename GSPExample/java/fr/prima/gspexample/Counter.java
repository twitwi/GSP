

package fr.prima.gspexample;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.util.Timer;
import java.util.TimerTask;


public class Counter extends AbstractModule {

    @ModuleParameter(initOnly=true)
    public long period = 1000;

    @ModuleParameter(initOnly = true)
    public long warmupDelay = 0;

    @ModuleParameter(initOnly=true)
    public int maxCount = -1;

    // this will be automatically injected by the framework
    @ModuleParameter(initOnly=true)
    public Assembly assembly;

    private int count = 0;

    Timer timer = new Timer("Timer Thread for the Clock GSP Module");

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
        emitEvent(count);
        asString();
        asVoid();
        count++;
        if (count == maxCount) {
            timer.cancel();
            assembly.stop();
        }
    }

    private void asString() {
        emitEvent("" + count);
    }

    private void asVoid() {
        emitEvent();
    }

}
