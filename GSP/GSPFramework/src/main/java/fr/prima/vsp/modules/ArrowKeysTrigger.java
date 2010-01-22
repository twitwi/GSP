/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author twilight
 */
public class ArrowKeysTrigger extends AbstractModule {

    @ModuleParameter(change = "update")
    public String title = "Use Arrow Keys";

    JFrame f = new JFrame();

    @Override
    public void stopModule() {
        f.dispose();
    }

    public ArrowKeysTrigger() {
    }
    @Override
    public void initModule() {
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setLocation(0, 400+new Random().nextInt(200));
        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    left();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    right();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    up();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    down();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    space();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enter();
                }
            }
        });
        update();
    }

    public void update() {
        f.setTitle(title);
        f.setSize(new Dimension(200, 200));
        f.setVisible(true);
    }
    
    public void left() {emitEvent();}
    public void right() {emitEvent();}
    public void up() {emitEvent();}
    public void down() {emitEvent();}
    public void space() {emitEvent();}
    public void enter() {emitEvent();}

}
