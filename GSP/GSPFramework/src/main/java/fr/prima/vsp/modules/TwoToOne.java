/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules;

/**
 *
 * @author emonet
 */
public class TwoToOne extends AbstractModule {

    Object mem1 = null;
    Object mem2 = null;

    public synchronized void in1(Object o) {
        mem1 = o;
        out();
    }
    public synchronized void in2(Object o) {
        mem2 = o;
        out();
    }
    void out() {
        if (mem1 != null && mem2 != null) {
            emitEvent(mem1, mem2);
        }
    }

}
