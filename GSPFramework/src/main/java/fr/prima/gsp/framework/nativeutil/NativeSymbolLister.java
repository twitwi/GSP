/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.nativeutil;

import org.bridj.demangling.Demangler.Symbol;
import org.bridj.BridJ;
import org.bridj.Platform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author remonet
 * 
 * will return only C++ symbols
 *
 */
public abstract class NativeSymbolLister {

    public static NativeSymbolLister create() {
        boolean disableFilter = true;
        // ^ WARNING the filter might be mangling dependant
        if (!disableFilter && !Platform.isLinux()) throw new IllegalStateException("check the filter is ok for your (non-linux) os");
        return createBridjNativeSymbolsLister(disableFilter);
    }

    // API

    public final List<String> getSymbols(String libraryName) {
        return getSymbolsImpl(libraryName);
    }

    // SPI
    protected NativeSymbolLister() {}
    protected abstract List<String> getSymbolsImpl(String libraryName);

    // inner (could be externalized in another file to lower depencies)
    static NativeSymbolLister createBridjNativeSymbolsLister(final boolean disableFilter) {
        return new NativeSymbolLister() {

            @Override
            protected List<String> getSymbolsImpl(String libraryName) {
                try {
                    List<String> res = new ArrayList<String>();
                    for (Symbol symbol : BridJ.getNativeLibrary(libraryName).getSymbols()) {
                        String s = symbol.getName();
                        if (disableFilter || s.matches("^_Z.*")) {
                            res.add(s);
                        }
                    }
                    return res;
                } catch (IOException ex) {
                    Logger.getLogger(NativeSymbolLister.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
    }

    public static void main(String[] args) {
        List<String> l = create().getSymbols(args[0]);
        System.out.println("Listing symbols for " + args[0]);
        for (String s : l) {
            System.out.println("  " + s);
        }

    }
}
