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
 * @author twilight
 */
public class Timestamper extends AbstractModuleWithoutEventReceiver {

    @ModuleParameter
    public int period = -1;
    @ModuleParameter
    public long origin = -1;
    @ModuleParameter
    public double scale = 1;
    //
    private long nextTime = 0;

    public Timestamper() {
        this.allowOpenEvents = true;
    }
    Map<String, EventReceiver> receiversCache = new HashMap<String, EventReceiver>();

    public EventReceiver getEventReceiver(final String portName) {
        EventReceiver res = receiversCache.get(portName);
        final String outputName = portName.equals("input") ? "output" : portName;
        if (res == null) {
            res = new EventReceiver() {
                public void receiveEvent(Event e) {
                    long time;
                    if (period == -1) { // using live timestamping
                        time = System.currentTimeMillis();
                    } else { // using regular timestamping
                        time = nextTime;
                        nextTime += period;
                    }
                    if (origin == -1) {
                        origin = time;
                    }
                    time = (long) ((time - origin) * scale);
                    Object[] params = new Object[e.getInformation().length + 1];
                    params[0] = time;
                    System.arraycopy(e.getInformation(), 0, params, 1, params.length - 1);
                    emitNamedEvent(outputName, params);
                }
            };
            receiversCache.put(portName, res);
        }
        return res;
    }
}
