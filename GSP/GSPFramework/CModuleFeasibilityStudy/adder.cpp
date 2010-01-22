
#include "adder.h"

// #################### module .c ####################
void add(int p1, int p2) {
    emitNamedEvent("res", p1+p2);
}
void sub(int p1, int p2) {
    emitNamedEvent("res", p1-p2);
}


/* SPI */  void onInitFps(void*){/*here*/;}
