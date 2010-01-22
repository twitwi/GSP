/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import org.w3c.dom.Element;

/**
 *
 */
public interface Module {

    public EventReceiver getEventReceiver(String portName);
    public void addConnector(String portName, EventReceiver eventReceiver);
    public void configure(Element conf);
    public void init();
    public void stop();
}
