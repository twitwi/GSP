/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.nativeutil;

import java.util.List;

/**
 * 
 */
public class NativeFunctionFinder {

    public static NativeFunctionFinder create(String libraryName) {
        return new NativeFunctionFinder(libraryName).checkFound();
    }

    NativeSymbolLister lister = NativeSymbolLister.create();
    NativeSymbolDemangler demangler = NativeSymbolDemangler.create();
    String libraryName;

    public NativeFunctionFinder(String libraryName) {
        this.libraryName = libraryName;
    }

    private NativeFunctionFinder checkFound() {
        return this;
    }

    public NativeSymbolInfo findAnyMethodForParameters(String className, String functionName, NativeType... types) {
        List<String> symbols = lister.getSymbols(libraryName);
        
        withNextSymbol:
        for (String symbol : symbols) {
            NativeSymbolInfo info = null;
            info = demangler.demangle(libraryName, symbol);
            if (info == null || !functionName.equals(info.name) || info.parameterTypes.length != types.length) {
                continue;
            }
            for (int i = 0; i < types.length; i++) {
                //System.err.println(symbol+": "+i + ") Close enough? " + types[i] + " " + info.parameterTypes[i]);
                if (!NativeType.areSame(types[i], info.parameterTypes[i])) {
                    //System.err.println("NO");
                    continue withNextSymbol;
                }
                //System.err.println("YES");
                //System.err.println(i + ": " + types[i] + " and " + info.parameterTypes[i] + " ARE CLOSE ENOUGH");
            }
            //System.err.println("FOUND " + className + "::" + functionName + " " + Arrays.toString(info.parameterTypes));
            return info;
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        {
            NativeFunctionFinder finder = new NativeFunctionFinder("NativeCppDemo");
            finder.findAnyMethodForParameters("Log", "input", NativeType.VOID_POINTER);
            finder.findAnyMethodForParameters("Log", "initModule", NativeType.FLOAT);
            finder.findAnyMethodForParameters("Div", "setDivisor", NativeType.INT);
            finder.findAnyMethodForParameters("Div", "setDivisor", NativeType.BOOL);
            finder.findAnyMethodForParameters("Div", "setDivisor", NativeType.FLOAT);
        }
        {
            NativeFunctionFinder finder = new NativeFunctionFinder("AdvancedDemo");
            finder.findAnyMethodForParameters("ToThing", "setForceInt", NativeType.BOOL);
            finder.findAnyMethodForParameters("ToThing", "input", NativeType.FLOAT);
            finder.findAnyMethodForParameters("ToThing", "intInput", NativeType.INT);
            finder.findAnyMethodForParameters("CompareThings", "input", NativeType.pointer(NativeType.VOID), NativeType.pointer(NativeType.VOID));
        }
    }

}
