/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.spi;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.BaseAbstractModule;
import fr.prima.gsp.framework.Event;
import fr.prima.gsp.framework.EventReceiver;
import fr.prima.gsp.framework.Module;
import fr.prima.gsp.framework.ModuleParameter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * Base class for java modules.
 */
public class AbstractModule extends BaseAbstractModule {
    ////////////////////////////////////////////
    // implementation of the module interface //
    ////////////////////////////////////////////
    public final void addConnector(String portName, EventReceiver eventReceiver) {
        if (!allowOpenEvents) {
            if (1 != countMethods(this.getClass(), portName)) {
                throw new IllegalArgumentException();
                // TODO

//                throw new IllegalArgumentException("Source port is invalid: you must either have a method with its name or as set allowOpenEvents to true in your module", e);
            }
        }
        listenersFor(portName).add(eventReceiver);
    }

    public final EventReceiver getEventReceiver(String portName) {
        if (1 == countMethods(this.getClass(), portName)) {
            final Method m = findUniqueMethod(this.getClass(), portName);
            final Module that = this;
            return new EventReceiver() {
                public void receiveEvent(Event e) {
                    try {
                        m.invoke(that, e.getInformation());
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(AbstractModule.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(AbstractModule.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(AbstractModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
        } else {
            throw new IllegalArgumentException();
        // TODO handle the open-receiver model (e.g. the user will have its module implement OpenReceiver with a eventReceived method)
        }
    }

    public final void configure(Element conf) {
        configureMe(conf);
    }


    ///////////////////////////////////////////////////
    // extension and helper interface for subclasses //
    // (additions to the BaseAbstractModule class)   //
    ///////////////////////////////////////////////////
    protected void stopModule() {
    }
    protected void initModule() {
    }
    protected void emitEvent(Object... args) {
        String eventName = Thread.currentThread().getStackTrace()[2].getMethodName();
        emitNamedEvent(eventName, args);
    }

    //////////////////
    // private part //
    //////////////////
    private boolean moduleIsInited = false;
    private Map<String, List<EventReceiver>> listeners = new LinkedHashMap<String, List<EventReceiver>>();

    private int countMethods(Class<?> cl, String methodName) {
        int count = 0;
        for (Method m : cl.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                count++;
            }
        }
        return count;
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
    private void configureMe(Element conf) {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                ModuleParameter annotation = field.getAnnotation(ModuleParameter.class);
                if (annotation == null) continue;
                String parameterName = annotation.name().isEmpty() ? field.getName() : annotation.name();
                String attributeValue = conf.getAttribute(parameterName);
                Object value = readValue(field.getType(), attributeValue);
                if (value != null) {
                    if (annotation.initOnly()) {
                        this.checkInitOnlyForParameter(parameterName+" ("+field.getName()+")");
                    }
                    field.set(this, value);
                    invoke(this, annotation.change());
                }
            } catch (Exception ex) {
                // TODO
                Logger.getLogger(AbstractModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            Logger.getLogger(AbstractModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private Object readValue(Class<?> type, String svalue) throws Exception {
        Object value = null;
        if (Assembly.class.equals(type)) {
            value = Assembly.last;
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
    private final void checkInitOnlyForParameter(String informationalParameterName) {
        if (moduleIsInited) {
            throw new RuntimeException("Parameter cannot be modified after module initialization: "+informationalParameterName+".");
        }
    }

}
