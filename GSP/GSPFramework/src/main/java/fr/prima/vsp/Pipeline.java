/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp;

import fr.prima.videoserviceclient.BufferedImageSourceListener;
import fr.prima.videoserviceclient.ServiceImageSource;
import fr.prima.vsp.modules.AbstractModule;
import fr.prima.vsp.modules.ModuleParameter;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author emonet
 */
public class Pipeline {
    static String portSeparator = "@";

    Map<String, Module> modules = new HashMap<String, Module>();
    Map<String, Connector> connectors = new HashMap<String, Connector>();

    Map<String, StringRewriter> prefixes = new HashMap<String, StringRewriter>();
    ServiceImageSource source;
    BufferedImageSourceListener sourceListener;
    List<BufferedImageSourceListener> grabbers = new ArrayList<BufferedImageSourceListener>();

    public Pipeline(ServiceImageSource source) {
        this.source = source;
        source.addBufferedImageSourceListener(sourceListener = new BufferedImageSourceListener() {
            public void bufferedImageReceived(BufferedImage image, ByteBuffer imageDataOrNull) {
                for (BufferedImageSourceListener g : grabbers) {
                    g.bufferedImageReceived(image, imageDataOrNull);
                }
            }
            public void stopped() {
                for (BufferedImageSourceListener g : grabbers) {
                    g.stopped();
                }
            }
        });
    }

    public void stop() {
        source.removeBufferedImageSourceListener(sourceListener);
        for (Module module : modules.values()) {
            module.stop();
        }
    }

    public static interface StringRewriter {
        String rewrite(String s);
    }
    public void addPrefix(String prefix, StringRewriter r) {
        prefixes.put(prefix, r);
    }
    public void addPrefix(String prefix, final String newPrefix) {
        prefixes.put(prefix, new StringRewriter() {
            public String rewrite(String s) {
                return newPrefix + s;
            }
        });
    }
    public void addModule(String moduleId, String typeName, Element conf) {
        String className = AbstractModule.class.getPackage().getName() + "." + typeName;
        if (typeName.contains(":")) {
            String[] split = typeName.split(":");
            if (split.length != 2) {
                throw new IllegalArgumentException("Syntax error in '" + typeName + "': 0 or 1 ':' is allowed");
            }
            // we have a prefix
            String userPrefix = split[0];
            StringRewriter prefix = prefixes.get(userPrefix);
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix '" + userPrefix + "' is undefined");
            }
            className = prefix.rewrite(split[1]);
        }
        try {
            Class<? extends Module> cl = (Class<? extends Module>) Class.forName(className);
            addModule(moduleId, cl, conf);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("For module type '" + typeName + "': class '" + className + "' not found", ex);
        }
    }
    public void addModule(String moduleId, Class<? extends Module> moduleClass, Element conf) {
        try {
            Module m = moduleClass.newInstance();
            configureModule(m, conf);
            m.init();
            addModule(moduleId, m);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to instantiate class '" + moduleClass.getCanonicalName() + "'", ex);
        }
    }
    public void addModule(String moduleId, Module m) {
        if (modules.containsKey(moduleId)) {
            throw new IllegalArgumentException("Module with id '" + moduleId + "' is already present.");
        }
        modules.put(moduleId, m);
        Logger.getLogger(Pipeline.class.getName()).log(Level.FINER, "Added module " + moduleId + " -> " + modules.get(moduleId));
        if (m instanceof BufferedImageSourceListener) {
            grabbers.add((BufferedImageSourceListener) m);
            Logger.getLogger(Pipeline.class.getName()).log(Level.FINER, "Added grabber " + moduleId + " -> " + modules.get(moduleId));
        }
    }

    public void addConnector(String id, String from, String to) {
        String[] fromSplit = from.split(portSeparator);
        String[] toSplit = to.split(portSeparator);
        if (fromSplit.length != 2) {
            throw new IllegalArgumentException("While creating connector, 'from' must be of the form 'module"+portSeparator+"port' but is '"+from+"'");
        }
        if (toSplit.length != 2) {
            throw new IllegalArgumentException("While creating connector, 'to' must be of the form 'module"+portSeparator+"port' but is '"+to+"'");
        }
        addConnector(id, fromSplit[0], fromSplit[1], toSplit[0], toSplit[1]);
    }


    public void addConnector(String connectorId, String sourceModule, String sourcePort, String targetModule, String targetPort) {
        if (connectors.containsKey(connectorId)) {
            throw new IllegalArgumentException("Connector with id '" + connectorId + "' is already present.");
        }
        Module mSource = modules.get(sourceModule);
        Module mTarget = modules.get(targetModule);
        if (mSource == null) {
            throw new IllegalArgumentException("Source module '" + sourceModule + "' not present when adding connector '" + connectorId + "'");
        }
        if (mTarget == null) {
            throw new IllegalArgumentException("Target module '" + targetModule + "' not present when adding connector '" + connectorId + "'");
        }
        mSource.addConnector(sourcePort, mTarget, targetPort);
        connectors.put(connectorId, new Connector(mSource, sourcePort, mTarget, targetPort));
        Logger.getLogger(Pipeline.class.getName()).log(Level.FINER, "Added connector " + connectorId + " -> " + connectors.get(connectorId));
    }

    public int generatedId = 999999;
    public String generatedIdPrefix = "_auto_gen_";
    public void readFromXML(InputStream input) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            Element root = doc.getDocumentElement();
            for (Element e : list(root.getElementsByTagName("m"), root.getElementsByTagName("module"))) {
                addModule(e.getAttribute("id"), e.getAttribute("type"), e);
            }
            for (Element e : list(root.getElementsByTagName("c"), root.getElementsByTagName("connector"))) {
                String id = e.getAttribute("id");
                if (id.isEmpty()) id = generatedIdPrefix+(generatedId--);
                addConnector(id, e.getAttribute("from"), e.getAttribute("to"));
            }
        } catch (Exception ex) {
            Logger.getLogger(Pipeline.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private static List<Element> list(NodeList ...lists) {
        ArrayList<Element> res = new ArrayList<Element>();
        for (NodeList nodeList : lists) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    res.add((Element) nodeList.item(i));
                }
            }
        }
        return res;
    }
    private Map<Class, Class> buildableFromString = new HashMap<Class, Class>() {{
        put(String.class, String.class);
        put(Long.class, Long.class);
        put(Long.TYPE, Long.class);
        put(Integer.class, Integer.class);
        put(Integer.TYPE, Integer.class);
        put(Float.class, Float.class);
        put(Float.TYPE, Float.class);
        put(Double.class, Double.class);
        put(Double.TYPE, Double.class);
        put(Boolean.class, Boolean.class);
        put(Boolean.TYPE, Boolean.class);
    }};
    private void invoke(Module m, String change) {
        try {
            if (change.isEmpty()) {
                return;
            }
            m.getClass().getMethod(change).invoke(m);
        } catch (Exception ex) {
            Logger.getLogger(Pipeline.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void configureModule(Module m, Element conf) {
        Logger.getLogger(Pipeline.class.getName()).log(Level.FINER, "Configuring module " + m);
        for (Field field : m.getClass().getDeclaredFields()) {
            try {
                ModuleParameter annotation = field.getAnnotation(ModuleParameter.class);
                if (annotation == null) continue;
                String parameterName = annotation.name().isEmpty() ? field.getName() : annotation.name();
                String attributeValue = conf.getAttribute(parameterName);
                Object value = readValue(field.getType(), attributeValue);
                if (value != null) {
                    if (annotation.initOnly()) {
                        m.checkInitOnly(parameterName+" ("+field.getName()+")");
                    }
                    field.set(m, value);
                    invoke(m, annotation.change());
                }
            } catch (Exception ex) {
                // TODO
                Logger.getLogger(Pipeline.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private Object readValue(Class<?> type, String svalue) throws Exception {
        Object value = null;
        if (Pipeline.class.equals(type)) {
            value = this;
            //Logger.getLogger(Pipeline.class.getName()).log(Level.FINER, "Injecting pipeline into " + m.getClass() + portSeparator + field.getName());
        } else if (svalue.isEmpty()) {
        } else if (buildableFromString.containsKey(type)) {
            value = buildableFromString.get(type).getConstructor(String.class).newInstance(svalue);
        } else if (svalue.contains(",")) {
            String[] split = svalue.split(" *, *");
            value = invokeConstructor(type, split);
        }
        return value;
    }
    private <T> T invokeConstructor(Class<T> cl, String ...sargs) throws Exception {
        Constructor<T> constructor = findUniqueConstructor(cl, sargs);
        Object[] args = new Object[sargs.length];
        for (int i = 0; i < args.length; i++) {
            System.err.println("sargs("+i+") is "+sargs[i]+" that we will “ cast ” to "+constructor.getParameterTypes()[i].getName());
            args[i] = readValue(constructor.getParameterTypes()[i], sargs[i]);
        }
        return constructor.newInstance(args);
    }
    private static <T> Constructor<T> findUniqueConstructor(Class<T> cl, String... params) {
        Constructor<T> res = null;
        for (Constructor<?> m : cl.getDeclaredConstructors()) {
            if (m.getParameterTypes().length == params.length) {
                if (res != null) {
                    throw new IllegalArgumentException("Multiple constructors with "+params.length+" parameters in class '"+cl.getName()+"'");
                } else {
                    res = (Constructor<T>) m;
                }
            }
        }
        if (res == null) {
            throw new IllegalArgumentException("No constructor with "+params.length+" parameters in class '"+cl.getName()+"'");
        }
        return res;
    }

}
