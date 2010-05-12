package fr.prima.gsp;

import java.awt.MouseInfo;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main(String[] args) throws IOException {
        final JFrame f = new JFrame();
        {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new JLabel("close me to quit"));
            f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
            f.pack();
            f.setLocation(MouseInfo.getPointerInfo().getLocation());
            f.setVisible(true);
        }
        String imageInput = "imageInput";

        String stringFilter =
                "and(" +
                "    nameIs(\"ServiceVideo\")," +
//                "    not(hasVariable(\"field\")),"+ // avoid isight for the demo
//                "    hasVariable(\"id\", \"10\")," +
                "    or(" +
                "       ownerIs(\"emonet\")," +
                "       ownerIs(\"twilight\")" +
                "    )" +
                ")";
        if (args.length > 0) {
            stringFilter = args[0];
        }

        /*
        Handler[] handlers =
        Logger.getLogger( "" ).getHandlers();
        for ( int index = 0; index < handlers.length; index++ ) {
        handlers[index].setLevel( Level.ALL );
        }


        Logger.getLogger(Pipeline.class.getName()).setLevel(Level.ALL);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        Logger.getLogger(Pipeline.class.getName()).addHandler(consoleHandler);
         */

    }        /*
        Service vsp = new ServiceFactoryImpl().create("VSPJava");
        vsp.addConnector(imageInput, "to input images/headers from video service", ConnectorType.INPUT);
        ServiceFilter filter = readServiceFilter(stringFilter);
        ServiceProxy videoService = vsp.findService(filter);

        final ServiceImageSource source = ServiceImageSource.createSmartImageSourceAndConnect(vsp, imageInput, videoService);
        for (final File file : new File(".").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml") && !Arrays.asList("pom.xml", "nbactions.xml").contains(name);
            }
        })) {
            f.getContentPane().add(new JButton(new AbstractAction("start: " + file.getName()) {

                public void actionPerformed(final ActionEvent e) {
                    if (SwingUtilities.isEventDispatchThread()) {
                        new Thread(new Runnable() {

                            public void run() {
                                actionPerformed(e);
                            }
                        }).start();
                        return;
                    }
                    final Assembly assembly = new Assembly(source);
                    assembly.addPrefix("o", OmiscidTools.class.getPackage().getName() + ".Omiscid");
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file.getName());
                        assembly.readFromXML(fileInputStream);
                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    final JButton but = new JButton();
                    but.setAction(new AbstractAction("remove ("+file.getName()+")") {

                        public void actionPerformed(ActionEvent e) {
                            assembly.stop();
                            f.getContentPane().remove(but);
                            f.pack();
                        }
                    });
                    f.getContentPane().add(but);
                    f.pack();
                }
            }));
        }
        f.pack();

        {
            Service vspFactory = new ServiceFactoryImpl().create("JVSPFactory");
            String factoryConnector = "create";
            String videoPeeridVariable = "sourceId";
            vspFactory.addVariable(videoPeeridVariable, "peerId", "video service peer id", VariableAccessType.CONSTANT);
            vspFactory.setVariableValue(videoPeeridVariable, videoService.getPeerIdAsString());

            vspFactory.addConnector(factoryConnector, "receive factory configurations", ConnectorType.INPUT);
            vspFactory.addConnectorListener(factoryConnector, new ConnectorListener() {
                public void messageReceived(Service arg0, String arg1, Message message) {
                    final Assembly assembly = new Assembly(source);
                    assembly.addPrefix("o", OmiscidTools.class.getPackage().getName() + ".Omiscid");
                    ByteArrayInputStream in = new ByteArrayInputStream(message.getBuffer());
                    assembly.readFromXML(in);
                    // useless in.close();
                }
                public void disconnected(Service arg0, String arg1, int arg2) {
                }
                public void connected(Service arg0, String arg1, int arg2) {
                }
            });
            vspFactory.start();
        }
    }

    private static void view(final ServiceImageSource source) {
        final JFrame viewer = new JFrame();
        final BufferedImageSourceListener l = new BufferedImageSourceListener() {

            ImageIcon imageIcon = null;

            public void bufferedImageReceived(BufferedImage image, ByteBuffer imageDataOrNull) {
                if (imageIcon == null) {
                    imageIcon = new ImageIcon(image);
                    viewer.setContentPane(new JLabel(imageIcon));
                    viewer.pack();
                } else {
                    imageIcon.setImage(image);
                    viewer.repaint();
                }
            }

            public void stopped() {
                source.stop();
                viewer.dispose();
            }

        };
        source.addBufferedImageSourceListener(l);
        viewer.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                source.stop();
            }
        });
        viewer.pack();
        viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewer.setVisible(true);
    }
         */

    // TODO INTEGRATE IN OMISCID (REFERENCE IS NOW IN THE OMiSCIDServiceTree netbeans module)
    /*
     * Grammar:
     *       F -> NAME ( ListP )
     *    NAME -> word
     *   ListP -> P , ListP
     *         -> eps
     *       P -> F
     *         -> STRING
     *         -> INTEGER
     *         -> CONSTANT | READ | READ_WRITE
     *         -> INPUT | OUTPUT | INOUTPUT
     */
    /*
    public static ServiceFilter readServiceFilter(String stringFilter) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("js");
        //scriptEngine.put("sf", ServiceFilters.class);
        try {
            StringBuilder prog = new StringBuilder();
            prog.append(
                    "__A__ = Packages."+Array.class.getCanonicalName()+";" +
                    "__F__ = Packages."+ServiceFilter.class.getCanonicalName()+";" +
                    "__SF__ = Packages." + ServiceFilters.class.getCanonicalName() + ";" +
                    "__F__array = function() {" +
                    "  var res = Packages.java.lang.reflect.Array.newInstance(__F__, arguments.length);" +
                    "  for (var i = 0; i < arguments.length; i++){" +
                    "    __A__.set(res, i, arguments[i]);" +
                    "  }" +
                    "  return res;" +
                    "};" +
                    "and = function() { return __SF__.and(__F__array.apply(null, arguments));};" +
                    "or = function() { return __SF__.or(__F__array.apply(null, arguments));};"
                    );
            Set<String> existingFilters = new HashSet<String>();
            for (Method method : ServiceFilters.class.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers())
                        && method.getReturnType().equals(ServiceFilter.class)
                        && !method.isVarArgs()) {
                    existingFilters.add(method.getName());
                }
            }
            if (existingFilters.contains("and") || existingFilters.contains("or")) {
                throw new RuntimeException("We have a bug on service filters and/or (please report with this message)");
            }
            for (String name : existingFilters) {
                prog.append(name).append(" = __SF__.").append(name).append(";");
            }
            scriptEngine.eval(prog.toString());
            return (ServiceFilter) scriptEngine.eval(stringFilter);
        } catch (ScriptException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    */
    /*
    public static ServiceFilter readServiceFilter(String stringFilter) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(stringFilter));
            ServiceFilter res = F(tokenizer);
            check(StreamTokenizer.TT_EOF, tokenizer.nextToken());
            return res;
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static ServiceFilter F(StreamTokenizer tokenizer) throws IOException {
        check(StreamTokenizer.TT_WORD, tokenizer.nextToken());
        Method m = ServiceFilters.class.getDeclaredMethods()
    }

    private static void check(String target, String remove) {
        if (!target.equals(remove)) {
            throw new IllegalArgumentException("Expected '" + target + "' but found '" + remove + "'");
        }
    }

    private static void readWord(StreamTokenizer tokenizer) {
        check
    }

    private static void check(int target, int remove) {
        if (target != remove) {
            throw new IllegalArgumentException("Expected '" + target + "' but found '" + remove + "'");
        }
    }*/

}
