/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author twilight
 */
public class DebugGUI extends AbstractModule {

    public static interface Controller {
        void set(String s);
    }
    public static interface TypePresenter {
        JComponent getPresenter(String fullType, Controller action);
    }
    public static String[] getParenthesisParameters(String fullType) {
        return fullType.trim().replaceAll("^[^(]*[(](.*)[)]$", "$1").split(" *, *");
    }

    @ModuleParameter(change = "onChangeContent")
    public String what;

    // this will be automatically injected by the framework after setup and before init
    @ModuleParameter(initOnly = true)
    public Assembly assembly;

    public void onChangeContent() {
        initUI();
    }

    final Map<String, TypePresenter> typeHandlers = new HashMap<String, TypePresenter>();
    @Override
    protected void initModule() {
        typeHandlers.put("int", new TypePresenter() {
            @Override
            public JComponent getPresenter(String fullType, final Controller action) {
                final JSpinner spinner = new JSpinner();
                spinner.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        try {
                            Integer.parseInt(spinner.getValue().toString());
                            action.set(spinner.getValue().toString());
                        } catch (NumberFormatException ex) {
                            System.err.println("NFE: " + spinner.getValue().toString());
                        }
                    }
                });
                String[] params = getParenthesisParameters(fullType);
                if (params != null && params.length == 1) {
                    spinner.setValue(Integer.parseInt(params[0]));
                }
                return spinner;
            }
        });
        // TODO java6 services extensibility on type->gui
        initUI();
    }

    JFrame f;
    private void initUI() {
        if (f == null) {
            f = new JFrame("GSP Debug UI");
            f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        f.getContentPane().removeAll();
        for (String element : what.trim().split(" *; *")) {
            String[] parts = element.split(" *: *");
            final String name = parts[0];
            String type = parts[1];
            String typeName = type.replaceAll("^([a-zA-Z0-9_]+).*", "$1");
            TypePresenter p = typeHandlers.get(typeName);
            if (p != null) {
                f.getContentPane().add(p.getPresenter(type, new Controller() {
                    @Override
                    public void set(String s) {
                        setParameterValue(name, s);
                    }
                }));
            } else {
                // TODO should use a logger
                System.err.println("Type not handled '" + type + "' '" + typeName + "'");
            }
        }
        f.getContentPane().setPreferredSize(new Dimension(400, 300));
        f.pack();
        f.repaint();
        f.setVisible(true);
    }

    private void setParameterValue(String fullName, String value) {
        System.err.println("Setting parameter '" + fullName + "' to '" + value + "'");
        assembly.setModuleParameter(fullName, value);
    }
}