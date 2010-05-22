/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.nativeutil;

import static fr.prima.gsp.framework.nativeutil.NativeType.*;

/**
 *
 * @author twilight
 */
public class TestGccDemangler {

    public static void main(String[] args) {
        NativeSymbolDemangler dem = NativeSymbolDemangler.create();
        assertDemangle(dem, "_Z7func013Pcc", CHAR_POINTER, CHAR);
        assertDemangle(dem, "_Z7func013PPPPcS1_S0_S_c", p(p(p(CHAR_POINTER))), p(p(CHAR_POINTER)), p(CHAR_POINTER), CHAR_POINTER, CHAR);
    }

    public static NativeType p(NativeType base) {
        return pointer(base);
    }

    public static void assertDemangle(NativeSymbolDemangler dem, String mangledSymbol, NativeType... types) {
        NativeSymbolInfo info = dem.demangle(null, mangledSymbol);
        if (info == null) {
            err("ASSERT FAILED: no demangled info");
        }
        if (types.length != info.parameterTypes.length) {
            err("ASSERT FAILED: param length is " + info.parameterTypes.length + ", expected " + types.length);
        }
        for (int i = 0; i < types.length; i++) {
            if (types[i] != null) {
                if (!areSame(types[i], info.parameterTypes[i])) {
                    err("ASSERT FAILED: param " + i + " of type " + info.parameterTypes[i] + ", expected " + types[i]);
                }
            }
        }
    }

    private static void err(String string) {
        throw new RuntimeException(string);
    }
}
