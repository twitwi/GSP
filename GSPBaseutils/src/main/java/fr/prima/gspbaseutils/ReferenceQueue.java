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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author remonet
 */
public class ReferenceQueue extends AbstractModuleWithoutEventReceiver {

    @ModuleParameter
    public int warnFrom = 100;
    @ModuleParameter
    public boolean warnOverFree = true;
    //
    Queue<Event> q = new LinkedList<Event>();
    Map<String, EventReceiver> receiversCache = new HashMap<String, EventReceiver>();

    public ReferenceQueue() {
        this.allowOpenEvents = true;
        EventReceiver freeer = new EventReceiver() {
            public void receiveEvent(Event e) {
                if (q.isEmpty() && warnOverFree) {
                    System.err.println("ReferenceQueue: free received when queue is empty");
                }
                q.poll();
            }
        };
        EventReceiver flusher = new EventReceiver() {
            public void receiveEvent(Event e) {
                q.clear();
            }
        };
        receiversCache.put("free", freeer);
        receiversCache.put("poll", freeer);
        receiversCache.put("~", freeer);
        receiversCache.put("flush", flusher);
        receiversCache.put("!", flusher);
    }

    public EventReceiver getEventReceiver(final String portName) {
        EventReceiver res = receiversCache.get(portName);
        if (res == null) {
            res = new EventReceiver() {
                public void receiveEvent(Event e) {
                    q.add(e);
                    if (warnFrom >= 0) {
                        int qs = q.size();
                        if (qs >= warnFrom) {
                            System.err.println("ReferenceQueue: queue has reached size " + qs);
                        }
                    }
                }
            };
            receiversCache.put(portName, res);
        }
        return res;
    }
}
