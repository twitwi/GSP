
#include "framework.h"
#include <time.h>


// A simple example module

#define Mul(_, __, ...)                                    \
    _(__, param,    float, by)                             \
    _(__, receiver, input, mulInput, float, value)         \
    _(__, emitter,  float, emitMul, float, mul)            \
    _(__, emitter,  string, emitMulString, char*, mul)


DECLARE_MODULE(Mul)



// Same principle but in C++. It declare a class named Div.
// All generated members and methods are "public".
// Warning: the compiler won't tell you if you forget to implement "receivers" methods (such as "divInput" in this case).
#define Div(_, __, ...)                                    \
    _(__, param,    float, by)                             \
    _(__, receiver, input, divInput, float, value)         \
    _(__, emitter,  float, emitDiv, float, div)            \
    _(__, emitter,  string, emitDivString, char*, div)

DECLARE_CPP_MODULE(Div)



// A more complete module.
// Also, it is define using BEGIN/END_MODULE

#define FpsEstimator(_, __, ...)                            \
    _(__, param,    int, samples)                           \
    _(__, receiver, input, onInputReceived, float, v)       \
    _(__, emitter,  float, emitFps, float, fps)             \
    _(__, emitter,  string, emitFpsString, char*, fps)      \
                                                            \
    _(__, raw,      int count)                              \
                                                            \
    _(__, create, onCreateFps)                              \
    _(__, stop,   onStopFps)                                \
    _(__, init,   onInitFps) // after configuration

BEGIN_MODULE(FpsEstimator)
    // This is an additional field in the FpsEstimator struct
    // It could have also been added using raw, as for "int count" above.
    timeval last;
END_MODULE(FpsEstimator)



// Another simple example, with pointers
// We use void* as char* is misleading in C (string or byte array?)

#define TestImage(_, __, ...)                              \
    _(__, receiver, input, imageInput, void*, value)         \
    _(__, emitter,  output, emitImage, void*, image)

DECLARE_MODULE(TestImage)



// Other method using implicit convention (and name demangling on the java side)
class Div2 {
    float by;

public:
    Div2();
    Framework _framework; // the module must have such a public field
    // WITH_FRAMEWORK(); // could provide this macro

    void setBy(float by);

    void input(float value);
    // as we use the no-param constructor, we have no createModule() nor moduleCreated() callback
    //void initModule();
    //void stopModule();
};
CLASS_AS_MODULE(Div2);
// FIELD_AS_PARAMETER(Div2, float, by); // could provide this macro (to generate the setter using the other API: Div2__v__set__v__by)



