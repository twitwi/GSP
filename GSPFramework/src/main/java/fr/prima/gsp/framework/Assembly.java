/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import fr.prima.gsp.Main;
import fr.prima.gsp.Option;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * Stores a set of Modules and Connectors with an identifier associated to each.
 * Stores the namespaces and dispatches the creation of modules to the right mechanism.
 */
public class Assembly {

    // TODO CLEAN THIS
    public static Assembly last;

    public static String portSeparator = "@";
    public static String chainSeparator = "\\s*-\\s*";
    public static String chainPortSeparator = "#";

    // associations id->module and id->connector
    Map<String, Module> modules = new HashMap<String, Module>();
    Map<String, Connector> connectors = new HashMap<String, Connector>();

    Map<String, StringRewriter> prefixes = new HashMap<String, StringRewriter>();

    CModuleFactory cModuleFactory = new CModuleFactory();


    public Assembly() {
        this.addPrefix("c", "[C-CODE]");
        this.addPrefix("java", identityStringRewriter());
    }
    public void stop() {
        // TODO could call the registered hook (registered by modules, e.g. the grabber)
        // not sure it is really usefull
    }
    public String addModule(String moduleId, final String typeAttribute, Element conf) {
        String typeDescriptor;
        if (typeAttribute.contains(":")) {
            String[] split = typeAttribute.split(":");
            if (split.length != 2) {
                throw new IllegalArgumentException("Syntax error in '" + typeAttribute + "': 0 or 1 ':' is allowed");
            }
            // we have a prefix
            String userPrefix = split[0];
            StringRewriter prefix = prefixes.get(userPrefix);
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix '" + userPrefix + "' is undefined");
            }
            typeDescriptor = prefix.rewrite(split[1]);
        } else {
            typeDescriptor = Main.class.getPackage().getName() + "." + typeAttribute;
        }
        Module newModule = null;
        if (typeDescriptor.startsWith("[C-CODE]")) {
            newModule = createCModule(typeDescriptor);
        } else {
            newModule = createJavaModule(typeDescriptor);
        }
        if (newModule == null) {
            throw new IllegalArgumentException("Module for type '" + typeAttribute + "' cannot be created");
        } else {
            addModule(moduleId, newModule);
        }
        newModule.configure(conf);
        return moduleId;
    }


    private Map<String, Callable<String>> factories = new HashMap<String, Callable<String>>();
    private void addModuleFactory(String factoryId, final String type, final Element e) {
        factories.put(factoryId, new Callable<String>() {
            public String call() {
                String moduleId = generateId();
                addModule(moduleId, type, e);
                debug("Instantiated module '" + moduleId + "' for type '" + type + "'");
                return moduleId;
            }
        });
    }
    private String createModuleIfIdIsFactory(String id) {
        debug("Looking for factory '" + id + "'");
        Callable<String> factory = factories.get(id);
        if (factory != null) {
            try {
                return factory.call();
            } catch (Exception ex) {
                Logger.getLogger(Assembly.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return id;
    }


    public static List<Field> getClassAndSuperClassFields(Class cl) {
        ArrayList<Field> res = new ArrayList<Field>();
        do {
            res.addAll(Arrays.asList(cl.getDeclaredFields()));
            cl = cl.getSuperclass();
        } while (cl != Object.class);
        return res;
    }

    private Module createJavaModule(String className) {
        try {
            Class<? extends Module> cl = (Class<? extends Module>) Class.forName(className);
            Module res = cl.newInstance();
            { // possibly inject the assembly in the module (if it has an Assembly assembly field)
                Class<?> classType = Assembly.class;
                String fieldName = classType.getSimpleName();
                fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                for (Field f : getClassAndSuperClassFields(cl)) {
                    debug("Looking for field " + fieldName + " for injection, have field " + f.getName() + " of type " + f.getType());
                    if (fieldName.equals(f.getName()) && classType.equals(f.getType())) {
                        // TODO could check for public, final etc
                        f.set(res, this);
                        log("Injected Assembly into field " + fieldName + " for instance of class " + className);
                    }
                }
            }
            return res;
        } catch (InstantiationException ex) {
            Logger.getLogger(Assembly.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Assembly.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("For module type '" + className + "': class '" + className + "' not found", ex);
        }
        return null;
    }
    private Module createCModule(String cLibAndType) {
        cLibAndType = cLibAndType.replaceFirst("\\[C-CODE\\]", "");
        String[] parts = cLibAndType.split("[.]");
        if (parts.length == 2) {
            return cModuleFactory.createModule(parts[0], parts[1]);
            //throw new IllegalStateException("commented out, change this");
        } else {
            throw new IllegalArgumentException("Cannot find a single '.' in the C module type name: " + cLibAndType);
        }
    }



    public void addModule(String moduleId, Module m) {
        if (modules.containsKey(moduleId)) {
            throw new IllegalArgumentException("Module with id '" + moduleId + "' is already present.");
        }
        modules.put(moduleId, m);
        log("Added module " + moduleId + " -> " + modules.get(moduleId));
        /*
        if (m instanceof BufferedImageSourceListener) {
            grabbers.add((BufferedImageSourceListener) m);
            Logger.getLogger(Assembly.class.getName()).log(Level.FINER, "Added grabber " + moduleId + " -> " + modules.get(moduleId));
        }
         */
    }

    private String generateId() {
        return generatedIdPrefix + (generatedId--);
    }

    private int is(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * This method can be called in initModule (or in a parameter setter listener)
     * to add an action to be executed after all modules are initialized.
     * @param runnable
     */
    public void addPostInitHook(Runnable runnable) {
        postInitHooks.add(runnable);
    }
    private void callPostInitHooks() {
        for (Runnable runnable : postInitHooks) {
            runnable.run();
        }
        postInitHooks.clear();
    }
    private final List<Runnable> postInitHooks = new ArrayList<Runnable>();

    public static abstract class ReadFromXMLHandler {
        public void namespace(Element e) {}
        public void module(Element e) {}
        public void connector(Element e) {}
        public void factory(Element e) {}
    }
    public int generatedId = 999999;
    public String generatedIdPrefix = "_auto_gen_";
    public void readFromXML(InputStream input, Option<ReadFromXMLHandler> obs) {
        try {
            ReadFromXMLHandler h = obs.getOr(new ReadFromXMLHandler() {});
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            Element root = doc.getDocumentElement();
            for (Element e : list(root.getElementsByTagName("ns"), root.getElementsByTagName("namespace"))) {
                h.namespace(e);
                NamedNodeMap attributes = e.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.item(i).getNodeName();
                    String content = attributes.item(i).getTextContent();
                    if (prefixes.containsKey(name)) {
                        throw new IllegalArgumentException("Namespace '" + name + "' is already defined as '" + prefixes.get(name) + "'.");
                    }
                    addPrefix(name, content + ".");
                }
            }
            for (Element e : list(root.getElementsByTagName("f"), root.getElementsByTagName("factory"))) {
                h.factory(e);
                String id = e.getAttribute("id");
                String type = e.getAttribute("type");
                addModuleFactory(e.getAttribute("id"), type, e);
                log("Registered factory with id '" + id + "' for type '" + type + "'");
            }
            for (Element e : list(root.getElementsByTagName("m"), root.getElementsByTagName("module"))) {
                h.module(e);
                addModule(e.getAttribute("id"), e.getAttribute("type"), e);
            }
            for (Element e : list(root.getElementsByTagName("c"), root.getElementsByTagName("connector"))) {
                h.connector(e);
                if (e.hasAttribute("chain")) {
                    String[] parts = e.getAttribute("chain").trim().split(chainSeparator);
                    //int imax = parts.length - 2;
                    for (int i = 0; i < parts.length - 1; i++) {
                        String e1 = parts[i];
                        String e2 = parts[i+1];

                        String id = generateId();
                        String fromModule;
                        String fromPort = "";
                        {
                            String[] from = e1.split(chainPortSeparator, -1);
                            if (from.length == 1) {
                                fromModule = from[0];
                            } else {
                                fromModule = from[1 - is(i == 0)];
                                fromPort = from[2 - is(i == 0)];
                            }
                            if (fromPort.isEmpty()) {
                                fromPort = "output";
                            }
                        }
                        String toModule;
                        String toPort = "";
                        String nextFrom = "";
                        {
                            String[] to = e2.split(chainPortSeparator, -1);
                            if (to.length ==  1) {
                                toModule = to[0];
                            } else {
                                toModule = to[1];
                                toPort = to[0];
                                if (to.length == 3) {
                                    nextFrom = to[2];
                                }
                            }
                            if (toPort.isEmpty()) {
                                toPort = "input";
                            }
                        }
                        fromModule = createModuleIfIdIsFactory(fromModule);
                        toModule = createModuleIfIdIsFactory(toModule);
                        addConnector(id, fromModule, fromPort, toModule, toPort);
                        parts[i + 1] = chainPortSeparator + toModule + chainPortSeparator + nextFrom;
                    }
                } else {
                    // TODO should report an error
                    /*
                    String id = e.getAttribute("id");
                    if (id.isEmpty()) {
                        id = generatedIdPrefix + (generatedId--);
                    }
                    addConnector(id, e.getAttribute("from"), e.getAttribute("to"));
                     */
                }
            }
            for (Map.Entry<String, Module> e : modules.entrySet()) {
                e.getValue().init();
            }
            callPostInitHooks();
        } catch (Exception ex) {
            Logger.getLogger(Assembly.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void addConnector(String id, String from, String to) {
        String[] fromSplit = from.split(portSeparator);
        String[] toSplit = to.split(portSeparator);
        if (fromSplit.length == 1) {
            fromSplit = new String[] {from, "output"};
        }
        if (toSplit.length == 1) {
            toSplit = new String[] {to, "input"};
        }
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
        mSource.addConnector(sourcePort, mTarget.getEventReceiver(targetPort));
        connectors.put(connectorId, new Connector(mSource, sourcePort, mTarget, targetPort));
        log("Added connector " + connectorId + ": " + sourceModule + "#" + sourcePort + " - " + targetPort + "#" + targetModule);
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

    private void debug(String message) {
        Logger.getLogger(Assembly.class.getName()).log(Level.FINEST, message);
    }
    private void log(String message) {
        Logger.getLogger(Assembly.class.getName()).log(Level.FINE, message);
    }

    public static interface StringRewriter {
        String rewrite(String s);
    }
    public static StringRewriter identityStringRewriter() {
        return new StringRewriter() {
            public String rewrite(String s) {
                return s;
            }
        };
    }
    final public void addPrefix(String prefix, StringRewriter r) {
        prefixes.put(prefix, r);
    }
    final public void addPrefix(String prefix, final String newPrefix) {
        prefixes.put(prefix, new StringRewriter() {
            public String rewrite(String s) {
                return newPrefix + s;
            }
        });
    }

}
