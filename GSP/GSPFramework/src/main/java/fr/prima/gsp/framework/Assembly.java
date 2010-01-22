/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import fr.prima.gsp.Main;
import fr.prima.videoserviceclient.BufferedImageSourceListener;
import fr.prima.videoserviceclient.ServiceImageSource;
import java.awt.image.BufferedImage;
import java.io.InputStream;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * Stores a set of Modules and Connectors with an identifier associated to each.
 * Stores the namespaces and dispatches the creation of modules to the right mechanism.
 */
public class Assembly {
    public static String portSeparator = "@";

    // associations id->module and id->connector
    Map<String, Module> modules = new HashMap<String, Module>();
    Map<String, Connector> connectors = new HashMap<String, Connector>();

    Map<String, StringRewriter> prefixes = new HashMap<String, StringRewriter>();
    ServiceImageSource source;
    BufferedImageSourceListener sourceListener;
    List<BufferedImageSourceListener> grabbers = new ArrayList<BufferedImageSourceListener>();

    CModuleFactory cModuleFactory = new CModuleFactory();

    public Assembly(ServiceImageSource source) {
        this.source = source;
        this.addPrefix("c", "[C-CODE]");
        this.addPrefix("java", identityStringRewriter());
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

    public void addModule(String moduleId, final String typeAttribute, Element conf) {
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
        newModule.init();
    }


    
    private Module createJavaModule(String className) {
        try {
            Class<? extends Module> cl = (Class<? extends Module>) Class.forName(className);
            return cl.newInstance();
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
        } else {
            throw new IllegalArgumentException("Cannot find a single '.' in the C module type name: " + cLibAndType);
        }
    }




    public void addModule(String moduleId, Module m) {
        if (modules.containsKey(moduleId)) {
            throw new IllegalArgumentException("Module with id '" + moduleId + "' is already present.");
        }
        modules.put(moduleId, m);
        Logger.getLogger(Assembly.class.getName()).log(Level.FINER, "Added module " + moduleId + " -> " + modules.get(moduleId));
        if (m instanceof BufferedImageSourceListener) {
            grabbers.add((BufferedImageSourceListener) m);
            Logger.getLogger(Assembly.class.getName()).log(Level.FINER, "Added grabber " + moduleId + " -> " + modules.get(moduleId));
        }
    }

    public int generatedId = 999999;
    public String generatedIdPrefix = "_auto_gen_";
    public void readFromXML(InputStream input) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            Element root = doc.getDocumentElement();
            for (Element e : list(root.getElementsByTagName("ns"), root.getElementsByTagName("namespace"))) {
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
            for (Element e : list(root.getElementsByTagName("m"), root.getElementsByTagName("module"))) {
                addModule(e.getAttribute("id"), e.getAttribute("type"), e);
            }
            for (Element e : list(root.getElementsByTagName("c"), root.getElementsByTagName("connector"))) {
                String id = e.getAttribute("id");
                if (id.isEmpty()) id = generatedIdPrefix+(generatedId--);
                addConnector(id, e.getAttribute("from"), e.getAttribute("to"));
            }
        } catch (Exception ex) {
            Logger.getLogger(Assembly.class.getName()).log(Level.SEVERE, null, ex);
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
        mSource.addConnector(sourcePort, mTarget.getEventReceiver(targetPort));
        connectors.put(connectorId, new Connector(mSource, sourcePort, mTarget, targetPort));
        Logger.getLogger(Assembly.class.getName()).log(Level.FINER, "Added connector " + connectorId + " -> " + connectors.get(connectorId));
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

}
