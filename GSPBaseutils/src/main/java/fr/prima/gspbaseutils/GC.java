/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.spi.AbstractModuleEnablable;

/**
 *
 * @author remonet
 */
public class GC extends AbstractModuleEnablable {
    public void input() {
        if (isEnabled()) {
            System.gc();
        }
    }
}
