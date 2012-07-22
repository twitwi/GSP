

package fr.prima.gspexample;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import org.bridj.Pointer;


public class CharPointerToString extends AbstractModule {

    private void input(Pointer charPointer) {
        output(charPointer.getCString());
    }

    private void output(String s) {
        emitEvent(s);
    }

}
