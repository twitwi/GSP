/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.spi;

import fr.prima.gsp.framework.Event;
import fr.prima.gsp.framework.EventReceiver;
import fr.prima.gsp.framework.Module;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for Java modules.
 */
public class AbstractModule extends AbstractModuleWithoutEventReceiver {
    ////////////////////////////////////////////
    // implementation of the module interface //
    ////////////////////////////////////////////
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
    
    public static Method findUniqueMethod(Class<?> cl, String methodName) {
        Method res = null;
        for (Method m : cl.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                if (res != null) {
                    throw new IllegalArgumentException("Multiple methods named '" + methodName + "' in class '" + cl.getName() + "'");
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


}
