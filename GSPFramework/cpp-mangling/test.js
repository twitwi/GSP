
importPackage(Packages.fr.prima.gsp.framework.nativeutil);

var dem = NativeSymbolDemangler.create();
var err = java.lang.System.err;
var t = NativeType;
var p = NativeType.pointer;
var st = NativeType.struct;
var test = TestGccDemangler;


function pN(n, what) {return n==0 ? what : pN(n-1, p(what));}

if (true) NativeSymbolLister.main(Array("build/libManySymbols.so"));

test.assertDemangle(dem, "_Z7func013Pcc", Array(t.CHAR_POINTER, t.CHAR));
test.assertDemangle(dem, "_Z7func013iiiff", Array(t.INT, t.INT, t.INT, t.FLOAT, t.FLOAT));
test.assertDemangle(dem, "_Z7func013PiiS_fPf", Array(p(t.INT), t.INT, p(t.INT), t.FLOAT, p(t.FLOAT)));
test.assertDemangle(dem, "_Z7func013PPPPcS1_S0_S_c", Array(p(p(p(t.CHAR_POINTER))), p(p(t.CHAR_POINTER)), p(t.CHAR_POINTER), t.CHAR_POINTER, t.CHAR));
test.assertDemangle(dem, "_Z7func013P3AAAS0_", Array(p(st("AAA")), p(st("AAA"))));
test.assertDemangle(dem, "_Z7func016PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPcSZ_", Array(pN(37, t.CHAR), pN(37, t.CHAR)));
test.assertDemangle(dem, "_Z7func017PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPcS10_", Array(pN(38, t.CHAR), pN(38, t.CHAR)));
test.assertDemangle(dem, "_Z7func017PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPcS10_S_", Array(pN(38, t.CHAR), pN(38, t.CHAR), t.CHAR_POINTER));

test.assertDemangle(dem, "_Z5inputP5ThingS_", Array( p(st("Thing")), st("Thing") ));
test.assertDemangle(dem, "_ZN13CompareThings5inputEP5ThingS1_", Array( p(st("Thing")), p(st("Thing")) ));


var stdstring = NativeType.stdString();
test.assertDemangle(dem, "_Z7func200SsSs", Array(stdstring,stdstring));

var stdvectorofCCCpointer = NativeType.stdVector(p(st("CCC")));
test.assertDemangle(dem, "_Z7func201St6vectorIP3CCCSaIS1_EES3_", Array(stdvectorofCCCpointer, stdvectorofCCCpointer));



test.assertDemangle(dem, "_ZN9AVASToIpl5inputEPSt6vectorISsSaISsEEPS0_IPN16StreamFacilities13EnrichedFrameESaIS6_EE", Array(p(st("NOPE")),p(st("NOPE"))));
test.assertDemangle(dem, "_Z7streamsSd", Array(p(st("NOPE"))) );
