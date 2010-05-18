
package fr.prima.gspexample;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;

public class Console extends AbstractModule {

    @ModuleParameter
    public int level = 10;

    public void info(Object msg) {
        if (level > 5) System.err.println("J INFO: " + msg);
    }
    public void error(Object msg) {
        if (level > 0) System.err.println("J ERROR: " + msg);
    }
    public void warning(Object msg) {
        if (level > 1) System.err.println("J WARNING: " + msg);
    }

    public void input(Object msg) {
        info(msg);
    }
}
