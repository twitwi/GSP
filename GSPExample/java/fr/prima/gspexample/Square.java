

package fr.prima.gspexample;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;


public class Square extends AbstractModule {

    @ModuleParameter
    public float power = 2;

    public void input(float v) {
        output((float) Math.pow(v, power));
    }

    private void output(float s) {
        emitEvent(s);
    }

}
