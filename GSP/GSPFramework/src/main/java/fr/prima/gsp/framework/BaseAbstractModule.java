/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base helper class for modules.
 * This class is INTERNAL, module provider should use the AbstractModule class in .spi package.
 */
public abstract class BaseAbstractModule implements Module {
    ////////////////////////////////////////////
    // implementation of the module interface //
    ////////////////////////////////////////////
    public final void init() {
        if (moduleIsInited) throw new IllegalStateException("module is already inited");
        initModule();
        moduleIsInited = true;
    }

    public final void stop() {
        listeners.clear();
        stopModule();
    }

    ///////////////////////////////////////////////////
    // extension and helper interface for subclasses //
    ///////////////////////////////////////////////////
    protected abstract void stopModule();
    protected abstract void initModule();

    protected boolean allowOpenEvents = false;
    
    protected final void emitNamedEvent(String eventName, Object... args) {
        List<EventReceiver> targets = listenersFor(eventName);
        for (EventReceiver eventReceiver : targets) {
            eventReceiver.receiveEvent(new Event(args));
        }
    }

    protected final List<EventReceiver> listenersFor(String port) {
        if (!listeners.containsKey(port)) {
            listeners.put(port, new LinkedList<EventReceiver>());
        }
        return listeners.get(port);
    }


    //////////////////
    // private part //
    //////////////////
    private boolean moduleIsInited = false;
    private Map<String, List<EventReceiver>> listeners = new LinkedHashMap<String, List<EventReceiver>>();

}
