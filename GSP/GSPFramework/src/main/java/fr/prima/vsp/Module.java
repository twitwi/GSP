/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author emonet
 */
public class Module {

    private boolean moduleIsInited = false;
    private Map<String, Map<Object, Method>> listeners = new LinkedHashMap<String, Map<Object, Method>>();
    private Map<Object, Method> listenersFor(String port) {
        if (!listeners.containsKey(port)) {
            listeners.put(port, new LinkedHashMap<Object, Method>());
        }
        return listeners.get(port);
    }
    private Method findUniqueMethod(Class<?> cl, String methodName) {
        Method res = null;
        for (Method m : cl.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                if (res != null) {
                    throw new IllegalArgumentException("Multiple methods named '"+methodName+"' in class '"+cl.getName()+"'");
                } else {
                    res = m;
                }
            }
        }
        if (res == null) {
            throw new IllegalArgumentException("No method named '" + methodName + "' in class '" + cl.getName() + "'");
        }
        return res;
    }
    private Method findAMethod(Class<?> cl, String methodName) {
        for (Method m : cl.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        throw new IllegalArgumentException("No method named '" + methodName + "' in class '" + cl.getName() + "'");
    }

    public void checkInitOnly(String informationalParameterName) {
        if (moduleIsInited) {
            throw new RuntimeException("Parameter cannot be modified after module initialization.");
        }
    }
    public void addConnector(String sourcePort, Module mTarget, String targetPort) {
        if (!allowOpenEvents) {
            try {
                findAMethod(this.getClass(), sourcePort);
            } catch (Exception e) {
                throw new IllegalArgumentException("Source port is invalid: you must either have a method with its name or as set allowOpenEvents to true in your module", e);
            }
        }
        Method method = findUniqueMethod(mTarget.getClass(), targetPort);
        if (Modifier.isPrivate(method.getModifiers())) {
            throw new IllegalArgumentException("Target port '" + targetPort + "' is private and can't be bound");
        }
        listenersFor(sourcePort).put(mTarget, method);
    }

    public final void init() {
        if (moduleIsInited) throw new IllegalStateException("module is already inited");
        initModule();
        moduleIsInited = true;
    }

    public final void stop() {
        listeners.clear();
        stopModule();
    }

    public void stopModule() {

    }

    public void initModule() {
    }

    protected boolean allowOpenEvents = false;
    protected void emitNamedEvent(String eventName, Object... args) {
        Map<Object, Method> targets = listenersFor(eventName);
        for (Map.Entry<Object, Method> entry : targets.entrySet()) {
            try {
                entry.getValue().invoke(entry.getKey(), args);
            } catch (Exception ex) {
                Logger.getLogger(Module.class.getName()).log(Level.SEVERE, "Error while invoking " + entry.getKey().getClass().getName() + Pipeline.portSeparator + entry.getValue().getName() + " " + Arrays.toString(args), ex);
            }
        }
    }
    protected void emitEvent(Object... args) {
        String eventName = Thread.currentThread().getStackTrace()[2].getMethodName();
        emitNamedEvent(eventName, args);
    }

}
