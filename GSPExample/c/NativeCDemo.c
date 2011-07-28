#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//#include "framework.h"
typedef void (*Framework) (const char* command, ...);


struct div_module {
    Framework framework;

    float divisor;
};

struct div_module *
Div__v__create(Framework f)
{
    struct div_module *module = NULL;
    void *divisorparam[] = {
        "param",
        "divisor", "float",
        NULL
    };

    module = malloc(sizeof(struct div_module));
    assert(module != NULL);
    /*memset(module, 0, sizeof(struct div_module));*/
    module->framework = f;
    module->divisor = 1.f;

    module->framework("param", divisorparam);

    return module;
}

/* Not needed, not used
void
Div__v__init(struct div_module *module)
{
}
*/

void
Div__v__stop(struct div_module *module)
{
    free(module);
}


void
Div__v__set__v__divisor(struct div_module *module, float d)
{
    module->divisor = (d == 0.f) ? module->divisor : d;
}

void
Div__v__event__v__input(struct div_module *module, float value)
{
    float out = value / module->divisor;
    void *output[] = { /* "output" is the default when writing pipelines */
        "output",
        "float", NULL,
        NULL 
    };
    void *output_float[] = { /* but we can add any output */
        "float",
        "float", NULL,
        NULL 
    };
    void *output_log[] = { 
        "string",
        "char*", NULL,
        NULL 
    };
    char logbuf[128];
    char *logbufptr = logbuf; /* The GSP won't work with static arrray ptr */

    output[2] = &out;
    module->framework("emit", output);
    output_float[2] = &out;
    module->framework("emit", output_float);

    snprintf(logbuf, 127, "%f", out);
    output_log[2] = &logbufptr;
    module->framework("emit", output_log);
}

void
Div__v__event__v__intInput(struct div_module *module, int value)
{
    Div__v__event__v__input(module, (float)value);
}


struct log_module {
    Framework framework;

    char *hello;
};

struct log_module *
Log__v__create(Framework f)
{
    struct log_module *module = NULL;
    void *helloparam[] = {
        "param",
        "hello", "char*",
        NULL
    };

    printf("info: creating Log module\n");

    module = malloc(sizeof(struct log_module));
    assert(module != NULL);
    /*memset(module, 0, sizeof(struct log_module));*/
    module->framework = f;
    module->hello = NULL;

    module->framework("param", helloparam);

    return module;
}

void
Log__v__init(struct log_module *module)
{
    printf("info: init callback called\n");
}

void
Log__v__stop(struct log_module *module)
{
    printf("info: stop callback called\n");
    free(module);
}

void
Log__v__set__v__hello(struct log_module *module, const char *value) 
{
    printf("info: hello setter call with value '%s'\n", value);
}

void
Log__v__event__v__input(struct log_module *module, const char *message)
{
    printf("message: %s\n", message);
}

void
Log__v__event__v__highlight(struct log_module *module, const char *message)
{
#if 1
    printf("\e[1;37;41mMESSAGE: %s\e[m\n", message);
#else
    printf("MESSAGE: === %s ===\n", message);
#endif
}

