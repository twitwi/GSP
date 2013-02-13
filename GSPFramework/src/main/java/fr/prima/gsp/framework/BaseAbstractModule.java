/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    // generates a pseudo element to delegate
    @Override
    public final void configure(String attributeName, String value) {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element e = doc.createElement("m");
            e.setAttribute(attributeName, value);
            configure(e);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BaseAbstractModule.class.getName()).log(Level.SEVERE, null, ex);
        }
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
