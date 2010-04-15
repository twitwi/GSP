
#include "demo.h"

#include <sys/time.h>
#include <stdio.h>

// #################### module .c ####################

char *ftoa(char *buf, float data) {
    sprintf(buf,"%f", data);
    return buf;
}

// FpsEstimator
void onCreateFps(FpsEstimator* f) {
    f->samples = 1;
    f->count = -1;
}
void onInitFps(FpsEstimator* f) {
}
void onStopFps(FpsEstimator* f) {
}
void onInputReceived(FpsEstimator* f, float p) {
    if (f->count == -1) {
        gettimeofday(&f->last, NULL);
    }
    if (f->count >= f->samples) {
        char buf[200];
        timeval now;
        gettimeofday(&now, NULL);
        long duration = (now.tv_sec - f->last.tv_sec) * 1000 + (now.tv_usec - f->last.tv_usec)/1000;
        float fps = 1000.f / (float)duration * f->count;
        emitFps(f, fps);
        emitFpsString(f, ftoa(buf, fps));
        f->count = 0;
        f->last = now;
    }
    f->count++;
}

// Mul
void mulInput(Mul* m, float in) {
    char buf[200];
    float out = in * m->by;
    emitMul(m, out);
    emitMulString(m, ftoa(buf, out));
}

// Div
void Div::divInput(float in) {
    char buf[200];
    float out = in / this->by;
    this->emitDiv(out);
    emitDivString(ftoa(buf, out));
}

// TestImage
#include <string.h>
void imageInput(TestImage* m, void *image) {
    memset(image, 0, 10000);
    //    emitImage(m, image);
}

// Div2
Div2::Div2() : by(12) {

    // only for testing purpose (nothing to do with a classical module)
    float in;
    char buf[200];
    float fbuf[200];
    
    // testing typeid
#define doo(a) fprintf(stderr, "====== typeof(%s) is '%s'\n", #a, typeid(a).name());
    doo(buf);
    doo(fbuf);
    doo(int);
    doo(long);
    doo(double);
    doo(float);
    doo(in);
    doo((char*)buf);
    doo((const char *)buf);
    doo((char const *)buf);
    doo((void *)buf);
}

void Div2::input(float in) {
    fprintf(stderr, "by: %f\n", by);
    char buf[200];
    float out = in / this->by;
    emitNamedEvent("float", out);
    // framework constraint: requires lvalues, so cannot use
    //emitNamedEvent("string", ftoa(buf,out));
    //    char* toSend = ftoa(buf, out);
    char *toSend = ftoa(buf, out);
    emitNamedEvent("string", toSend);
}
void Div2::setBy(float by) {
    this->by = by;
}
