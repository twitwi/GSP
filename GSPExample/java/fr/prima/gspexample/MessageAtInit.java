
package fr.prima.gspexample;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;

public class MessageAtInit extends AbstractModule {

    @ModuleParameter
    public String msg = "Hello World";

    @Override public void initModule() {
        System.err.println("============================================================");
        String tab = "|  ";
        System.err.println(tab+msg.replaceAll("%n", "\n"+tab));
        System.err.println("============================================================");
    }
}
