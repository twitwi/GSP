/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author remonet
 */
public class Utils {

    public static String streamToString(InputStream inputStream) throws IOException {
        StringBuilder res = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = in.readLine()) != null) {
            res.append(line).append("\n");
        }
        return res.toString();
    }

    public static String replaceAll(String input, String regexp, Replacement replacement) {
        StringBuffer res = new StringBuffer();
        Matcher matcher = Pattern.compile(regexp).matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(res, Matcher.quoteReplacement(replacement.getReplacement(matcher.group())));
        }
        matcher.appendTail(res);
        return res.toString();
    }

    public static interface Replacement {
        String getReplacement(String matched);
    }
}
