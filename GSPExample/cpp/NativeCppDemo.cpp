
#include "NativeCppDemo.h"
#include <stdio.h>

// a utility function
static char *ftoa(char *buf, float data) {
    sprintf(buf,"%f", data);
    return buf;
}

Div::Div() : divisor(1) {
}

void Div::setDivisor(float d) {
    if (d != 0.0) {
        this->divisor = d;
    }
}

void Div::input(float value) {
    float out = value / this->divisor;
    emitNamedEvent("output", out); // "output" is the default when writing pipelines
    emitNamedEvent("float", out); // but we can add any output
    char buf[128];
    char *toSend = ftoa(buf, out);
    emitNamedEvent("string", toSend);
}

void Div::intInput(int value) {
    input((float) value);
}

Log::Log() {
    printf("info: creating Log object\n");
}

void Log::initModule() {
    printf("info: receiving initModule callback\n");
}

void Log::stopModule() {
    printf("info: receiving stopModule callback\n");
}
Log::~Log() {
    printf("info: destroying Log object\n");
}
void Log::setHello(const char *value) {
    printf("info: setter call with value '%s'\n", value);
}
void Log::input(const char *message) {
    printf("message: %s\n", message);
}
void Log::highlight(const char *message) {
    printf("MESSAGE: === %s ===\n", message);
}
