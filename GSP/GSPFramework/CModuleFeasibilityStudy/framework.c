
#include "framework.h"
#include <stdio.h>

Framework framework = 0;

// framework function impl
void emitNamedEvent(const char* name, ...) {
    printf("invoking %d\n", name); 
}


C_FUNCTION__ void injectFramework(Framework f) {
    framework = f;
}

