
#include "framework.h"


// testing a declarative interface
#define myADDER(_, ...) \
    _(collector, sub, int, int)                \
    _(collector, add, int, int)

DECLARE(adder, myADDER)

// this "adder" should correspond to the libadder.so

// interface of the module must be exported in C
// this could be in a .h file
/*extern "C" {
    void add(int,int);

    void emitNamedEvent(const char*, ...);
    }*/

/* SPI */ static void onInitFps(void*);

/* internal */ void FpsEstimator__init(void *m___m) {
    onInitFps(m___m);
}

