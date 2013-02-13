/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.Event;
import fr.prima.gsp.framework.EventReceiver;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleWithoutEventReceiver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author twilight
 */
public class GroupingBuffer extends AbstractModuleWithoutEventReceiver {

    @ModuleParameter
    public int queueCapacity = 100;
    //
    Thread thread = null;

    public GroupingBuffer() {
        this.allowOpenEvents = true;
    }

    @Override
    protected void initModule() {
        events = new ArrayBlockingQueue<Event>(100);
        thread = new Thread(new Runnable() {
            public void run() {
                runningThread();
            }
        }, "Thread for module " + GroupingBuffer.class.getName());
        thread.start();
    }

    @Override
    protected void stopModule() {
        thread.interrupt();
        thread = null;
    }
    BlockingQueue<Event> events;

    private void runningThread() {
        try {
            while (true) {
                List<Event> lastEvents = new ArrayList<Event>();
                lastEvents.add(events.take());
                events.drainTo(lastEvents);
                Event lastEvent = lastEvents.get(lastEvents.size() - 1);
                List<EventReceiver> targets = listenersFor("output");
                for (EventReceiver eventReceiver : targets) {
                    eventReceiver.receiveEvent(lastEvent);
                    // hum, could have a thread per input? (or just as now, considers only 1 input/output)
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GroupingBuffer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //
    //
    Map<String, EventReceiver> receiversCache = new HashMap<String, EventReceiver>();

    public EventReceiver getEventReceiver(final String portName) {
        EventReceiver res = receiversCache.get(portName);
        if (res == null) {
            res = new EventReceiver() {
                public void receiveEvent(Event e) {
                    try {
                        events.put(e);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GroupingBuffer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            receiversCache.put(portName, res);
        }
        return res;
    }
}
