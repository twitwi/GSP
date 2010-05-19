

package fr.prima.gspexample;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.util.Timer;
import java.util.TimerTask;
import com.sun.jna.Pointer;


public class CharPointerToString extends AbstractModule {

    private void input(Pointer charPointer) {
        output(charPointer.getString(0));
    }

    private void output(String s) {
        emitEvent(s);
    }

}
