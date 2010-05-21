
#include "framework.h"

//namespace Heeere {

    class Thing {
    public:
        int a;
        float b;
        char* c;
    };

    class  ToThing {

        bool forceInt;
        
    public:
        Framework _framework;
        
        // setters
        void setForceInt(bool b);

        // input port
        void input(float value);
        void intInput(int value);
    };
    CLASS_AS_MODULE(ToThing);

    class CompareThings {
    public:
        Framework _framework;
        void input(Thing *t1, Thing *t2);
    };
    CLASS_AS_MODULE(CompareThings);
    //}

