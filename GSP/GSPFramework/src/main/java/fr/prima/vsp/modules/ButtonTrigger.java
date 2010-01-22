/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules;

import java.awt.event.ActionEvent;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author emonet
 */
public class ButtonTrigger extends AbstractModule {

    @ModuleParameter(change = "update")
    public String title = "Click";

    JFrame f = new JFrame();
    JButton b;

    @Override
    public void stopModule() {
        f.dispose();
    }

    public ButtonTrigger() {
        b = new JButton();
    }
    @Override
    public void initModule() {
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setContentPane(b);
        f.setLocation(0, new Random().nextInt(400));
        update();
    }
    private void out() {
        outThis();
        emitEvent();
    }
    private void outThis() {
        emitEvent(this);
    }

    public void update() {
        b.setAction(new AbstractAction(title) {
            public void actionPerformed(ActionEvent e) {
                out();
            }
        });
        f.pack();
        f.setVisible(true);
    }

}
