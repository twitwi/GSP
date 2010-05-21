
#include "AdvancedDemo.h"
#include <stdio.h>

//namespace Heeere {
    void ToThing::setForceInt(bool b) {
        forceInt = b;
    }
    void ToThing::intInput(int val) {
        input(val);
    }
    void ToThing::input(float val) {
        Thing t;
        t.c = new char[256];
        if (forceInt) {
            t.a = (int) val;
            t.b = t.a;
            sprintf(t.c,"%i", t.a);
        } else {
            t.b = val;
            t.a = (int) t.b;
            sprintf(t.c,"%f", t.b);
        }
        Thing *tt = &t;
        emitNamedEvent("string", t.c);
        emitNamedEvent("output", tt, tt);
        delete[] t.c;
    }
   void CompareThings::input(Thing *t1, Thing *t2) {
       int o = t1->b == t2->b ? 1 : 0;
       const char* s = t1->b == t2->b ? "equal" : "differ";
       emitNamedEvent("output", o);
       emitNamedEvent("string", s);
   }
//}
