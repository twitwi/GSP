
package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.util.Formatter;

public class Console extends AbstractModule {

    @ModuleParameter
    public int level = 10;

    @ModuleParameter(initOnly=true)
    public String init = null;

    @ModuleParameter
    public String stop = null;

    private final String line = "======================================================================";
    @ModuleParameter
    public String format = line + "%s%n" + line + "%n";

    @ModuleParameter
    public String tab = "\n|  ";

    @Override
    protected void initModule() {
        lifecycleMessage(init);
    }

    @Override
    protected void stopModule() {
        lifecycleMessage(stop);
    }

    private void lifecycleMessage(String msg) {
        if (msg == null) {
            return;
        }
        msg = tab.replaceAll("%n", "\n") + msg.replaceAll("%n", tab.replaceAll("%n", "\n"));
        new Formatter(System.err).format(format, msg).flush();
    }

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
