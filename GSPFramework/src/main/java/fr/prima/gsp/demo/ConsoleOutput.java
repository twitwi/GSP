/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;

/**
 *
 * @author twilight
 */
public class ConsoleOutput extends AbstractModule {

    @ModuleParameter
    public int level = 10;

    public void info(Object msg) {
        if (level > 5) System.err.println("INFO: " + msg);
    }
    public void error(Object msg) {
        if (level > 0) System.err.println("ERROR: " + msg);
    }
    public void warning(Object msg) {
        if (level > 1) System.err.println("WARNING: " + msg);
    }

}
