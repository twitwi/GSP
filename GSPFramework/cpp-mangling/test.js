
importPackage(Packages.fr.prima.gsp.framework.nativeutil);

var dem = NativeSymbolDemangler.create();
var err = java.lang.System.err;
var t = NativeType;
var p = NativeType.pointer;
var test = TestGccDemangler;
/*
err.println("hello");
err.println(t.CHAR);
err.println(p(t.CHAR));
*/

test.assertDemangle(dem, "_Z7func013Pcc", Array(t.CHAR_POINTER, t.CHAR));
test.assertDemangle(dem, "_Z7func013iiiff", Array(t.INT, t.INT, t.INT, t.FLOAT, t.FLOAT));
test.assertDemangle(dem, "_Z7func013PiiS_fPf", Array(p(t.INT), t.INT, p(t.INT), t.FLOAT, p(t.FLOAT)));
test.assertDemangle(dem, "_Z7func013PPPPcS1_S0_S_c", Array(p(p(p(t.CHAR_POINTER))), p(p(t.CHAR_POINTER)), p(t.CHAR_POINTER), t.CHAR_POINTER, t.CHAR));
test.assertDemangle(dem, "_Z7func013P3AAAS_", Array(t.VOID_POINTER, t.VOID_POINTER));

