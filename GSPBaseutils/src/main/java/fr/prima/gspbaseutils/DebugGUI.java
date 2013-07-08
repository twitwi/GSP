/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author twilight
 */
public class DebugGUI extends AbstractModule {

    @ModuleParameter(change = "onChangeExitOnClose")
    public boolean exitOnClose = false;

    public void onChangeExitOnClose() {
        loadF();
        f.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DO_NOTHING_ON_CLOSE);
    }

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
        typeHandlers.put("float", new TypePresenter() {
            @Override
            public JComponent getPresenter(String fullType, final Controller action) {
                final JSpinner spinner = new JSpinner();
                spinner.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        try {
                            Float.parseFloat(spinner.getValue().toString());
                            action.set(spinner.getValue().toString());
                        } catch (NumberFormatException ex) {
                            System.err.println("NFE: " + spinner.getValue().toString());
                        }
                    }
                });
                String[] params = getParenthesisParameters(fullType);
                if (params != null && params.length == 1) {
                    spinner.setValue(Float.parseFloat(params[0]));
                }
                return spinner;
            }
        });
        typeHandlers.put("boolean", new TypePresenter() {
            @Override
            public JComponent getPresenter(String fullType, final Controller action) {
                final JCheckBox cb = new JCheckBox();
                cb.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        action.set(cb.isSelected() ? "true" : "false");
                    }
                });
                String[] params = getParenthesisParameters(fullType);
                if (params != null && params.length == 1) {
                    cb.setSelected(Boolean.parseBoolean(params[0]));
                }
                return cb;
            }
        });
        typeHandlers.put("string", new TypePresenter() {
            @Override
            public JComponent getPresenter(String fullType, final Controller action) {
                final JTextField text = new JTextField();
                text.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        action.set(text.getText());
                    }
                });
                return text;
            }
        });
        typeHandlers.put("stringarea", new TypePresenter() {
            @Override
            public JComponent getPresenter(String fullType, final Controller action) {
                final JTextArea text = new JTextArea();
                text.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        text.setBackground(new Color(255, 130, 130));
                        super.keyTyped(e); //To change body of generated methods, choose Tools | Templates.

                        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                            //if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                            action.set(text.getText());
                            text.setBackground(Color.WHITE);
                        }
                    }

                });
                return text;
            }
        });
        // TODO java6 services extensibility on type->gui
        initUI();
    }

    JFrame f;

    private void loadF() {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        loadF();
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(DebugGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DebugGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        if (f == null) {
            f = new JFrame("GSP Debug UI");
            f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    private void initUI() {
        loadF();
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
