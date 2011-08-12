/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 *
 * @author twilight
 */
public class Utilities {

    public static File findFileInDirectories(String filename, List<File> path) {
        File it;
        for (File dir : path) {
            if ((it = new File(dir, filename)).exists()) {
                return it;
            }
        }
        return null;
    }

    public static String fileContent(File file) throws IOException {
        StringBuilder b = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        try {
            while ((line = in.readLine()) != null) {
                b.append(line).append('\n');
            }
        } finally {
            in.close();
        }
        return b.toString();
    }
}
