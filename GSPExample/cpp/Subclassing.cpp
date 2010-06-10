
#include "Subclassing.h"
#include <stdio.h>

void Parent::initModule() {
    fprintf(stderr, "initModule in Parent class\n");
}

void Child::initModule() {
    fprintf(stderr, "initModule in Child class\n");
}

void GrandChild::initModule() {
    fprintf(stderr, "initModule in GrandChild class\n");
}

