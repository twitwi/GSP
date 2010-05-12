/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp;

import fr.prima.gsp.framework.Assembly;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author remonet
 */
public class Launcher {

    public static void main(String[] args) throws IOException {
        // TODO catch anything and display correct message (and localized)
        if (args.length == 0) {
            main(new String[]{"pipeline-simple-with-parameter.xml", "p=200", "s=1"});
            main(new String[]{"--help"});
            return;
        }
        if (Arrays.asList(args).contains("--help")) {
            showHelp();
            return;
        }
        Launcher cli = new Launcher();
        Assembly assembly = cli.load(args).get();
    }

    private static void showHelp() {
        System.err.println("TODO");
    }

    private Option<Assembly> load(FileInputStream inputStream, String[] parameters) throws IOException {
        String content = Utils.streamToString(inputStream);
        final Map<String, String> settings = readParameters(parameters);
        final List<String> unreplaced = new ArrayList<String>();
        content = Utils.replaceAll(content, "[$][{][a-zA-Z0-9-_]*[}]", new Utils.Replacement() {
            public String getReplacement(String matched) {
                String var = matched.substring(2, matched.length() - 1);
                String res = settings.get(var);
               if (res == null) {
                    unreplaced.add(var);
                    return matched;
                }
                return res;
            }
        });
        if (!unreplaced.isEmpty()) {
            throw new IllegalArgumentException("Found some unreplaced variables " + unreplaced.toString());
        }
        Assembly a = new Assembly();
        a.readFromXML(new ByteArrayInputStream(content.getBytes()));
        // TODO there should be some error reporting when readFromXML fails (currently it swallows exceptions)
        return Option.create(a);
    }

    public Map<String, String> readParameters(String[] parameters) {
        Map<String, String> res = new HashMap<String, String>();
        // TODO use iterator and implement advanced stuffs (grouping)
        for (String p : parameters) {
            System.err.println("P:"+p+";");
            if (p.matches("[a-zA-Z0-9-_.]*=.*")) {
                String[] parts = p.split("=", 2);
                res.put(parts[0], parts[1]);
            }
        }
        return res;
    }

    // --------------------------------- //

    public Option<Assembly> load(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        Option<File> xmlDescriptorFile = findXmlDescriptor(args[0]);
        if (!xmlDescriptorFile.isPresent()) {
            return Option.create((Assembly) null);
        }
        String[] params = Arrays.copyOfRange(args, 1, args.length);
        return load(new FileInputStream(xmlDescriptorFile.get()), params);
    }

    private Option<File> findXmlDescriptor(String basename) {
        File res = new File(basename);
        // TODO: add environment variables handling here
        if (res.exists() && res.canRead()) {
            return Option.create(res);
        }
        return Option.create((File) null);
    }
    
}
