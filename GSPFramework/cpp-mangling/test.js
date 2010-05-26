
importPackage(Packages.fr.prima.gsp.framework.nativeutil);

var dem = NativeSymbolDemangler.create();
var err = java.lang.System.err;
var t = NativeType;
var p = NativeType.pointer;
var st = NativeType.struct;
var test = TestGccDemangler;

function pN(n, what) {return n==0 ? what : pN(n-1, p(what));}

test.assertDemangle(dem, "_Z7func013Pcc", Array(t.CHAR_POINTER, t.CHAR));
test.assertDemangle(dem, "_Z7func013iiiff", Array(t.INT, t.INT, t.INT, t.FLOAT, t.FLOAT));
test.assertDemangle(dem, "_Z7func013PiiS_fPf", Array(p(t.INT), t.INT, p(t.INT), t.FLOAT, p(t.FLOAT)));
test.assertDemangle(dem, "_Z7func013PPPPcS1_S0_S_c", Array(p(p(p(t.CHAR_POINTER))), p(p(t.CHAR_POINTER)), p(t.CHAR_POINTER), t.CHAR_POINTER, t.CHAR));
test.assertDemangle(dem, "_Z7func013P3AAAS_", Array(p(st("AAA")), p(st("AAA"))));
test.assertDemangle(dem, "_Z7func016PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPcSZ_", Array(pN(37, t.CHAR), pN(37, t.CHAR)));
test.assertDemangle(dem, "_Z7func017PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPcS10_", Array(pN(38, t.CHAR), pN(38, t.CHAR)));
test.assertDemangle(dem, "_Z7func017PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPcS10_S_", Array(pN(38, t.CHAR), pN(38, t.CHAR), t.CHAR_POINTER));

test.assertDemangle(dem, "_Z5inputP5ThingS_", Array( p(st("Thing")), p(st("Thing")) ));
test.assertDemangle(dem, "_ZN13CompareThings5inputEP5ThingS1_", Array( p(st("Thing")), p(st("Thing")) ));

