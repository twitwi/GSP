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
public class StopWatch extends AbstractModuleEnablable {

    long lastTick;

    @Override
    protected void initModule() {
        lastTick = System.currentTimeMillis();
    }

    public void tick() {
        lastTick = System.currentTimeMillis();
    }

    public void tick1(Object o) {
        tick();
    }

    public void tick2(Object o1, Object o2) {
        tick();
    }

    public void tick3(Object o1, Object o2, Object o3) {
        tick();
    }

    public void tick4(Object o1, Object o2, Object o3, Object o4) {
        tick();
    }

    public void tick5(Object o1, Object o2, Object o3, Object o4, Object o5) {
        tick();
    }

    public void tick6(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        tick();
    }

    public void tick7(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        tick();
    }

    public void tick8(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        tick();
    }

    public void tick9(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        tick();
    }

    public void tock() {
        long diff = System.currentTimeMillis() - lastTick;
        output((int) diff);
    }

    public void tock1(Object o) {
        tock();
    }

    public void tock2(Object o1, Object o2) {
        tock();
    }

    public void tock3(Object o1, Object o2, Object o3) {
        tock();
    }

    public void tock4(Object o1, Object o2, Object o3, Object o4) {
        tock();
    }

    public void tock5(Object o1, Object o2, Object o3, Object o4, Object o5) {
        tock();
    }

    public void tock6(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        tock();
    }

    public void tock7(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        tock();
    }

    public void tock8(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        tock();
    }

    public void tock9(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        tock();
    }

    public void tocktick() {
        long now = System.currentTimeMillis();
        long diff = now - lastTick;
        lastTick = now;
        output((int) diff);
    }

    public void tocktick1(Object o) {
        tocktick();
    }

    public void tocktick2(Object o1, Object o2) {
        tocktick();
    }

    public void tocktick3(Object o1, Object o2, Object o3) {
        tocktick();
    }

    public void tocktick4(Object o1, Object o2, Object o3, Object o4) {
        tocktick();
    }

    public void tocktick5(Object o1, Object o2, Object o3, Object o4, Object o5) {
        tocktick();
    }

    public void tocktick6(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        tocktick();
    }

    public void tocktick7(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        tocktick();
    }

    public void tocktick8(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        tocktick();
    }

    public void tocktick9(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        tocktick();
    }

    private void output(int diff) {
        emitEvent(diff);
    }
}
