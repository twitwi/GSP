/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.Event;
import fr.prima.gsp.framework.EventReceiver;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleWithoutEventReceiver;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Usually, put the connections of this module first in the pipeline file. This
 * way, the module gets called first by the framework and you measure what you
 * want to measure.
 */
public class StopWatch extends AbstractModuleWithoutEventReceiver {

    // mixing with AbstractModuleEnablable
    @ModuleParameter(change = "enabledChanged")
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
    // ^ end of mixin with enableable
    long lastTick;
    Map<String, EventReceiver> receiversCache = new HashMap<String, EventReceiver>();

    public StopWatch() {
        EventReceiver tick = new EventReceiver() {
            public void receiveEvent(Event e) {
                tick();
            }
        };
        EventReceiver tock = new EventReceiver() {
            public void receiveEvent(Event e) {
                tock();
            }
        };
        EventReceiver tocktick = new EventReceiver() {
            public void receiveEvent(Event e) {
                tocktick();
            }
        };
        receiversCache.put("tick", tick);
        receiversCache.put("tock", tock);
        receiversCache.put("tocktick", tocktick);
        receiversCache.put(":", tick);
        receiversCache.put("+", tock);
        receiversCache.put("+:", tocktick);
    }

    @Override
    protected void initModule() {
        lastTick = System.currentTimeMillis();
    }

    public EventReceiver getEventReceiver(final String portName) {
        EventReceiver res = receiversCache.get(portName);
        if (res == null) {
            res = new EventReceiver() {
                public void receiveEvent(Event e) {
                    if (!enabled) {
                        return;
                    }
                    System.err.println("StopWatch: unrecognized port " + portName);
                }
            };
        }
        return res;
    }

    public void tick() {
        if (!enabled) {
            return;
        }
        lastTick = System.currentTimeMillis();
    }

    public void tock() {
        if (!enabled) {
            return;
        }
        long diff = System.currentTimeMillis() - lastTick;
        output((int) diff);
    }

    public void tocktick() {
        if (!enabled) {
            return;
        }
        long now = System.currentTimeMillis();
        long diff = now - lastTick;
        lastTick = now;
        output((int) diff);
    }

    private void output(int diff) {
        emitEvent(diff);
    }
}
