
#include "framework.h"

namespace Heeere {

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

}


#include <string>
#include <vector>

class ProduceStl {
public:
    Framework _framework;
    void input(int value);
};
CLASS_AS_MODULE(ProduceStl);
class ConsumeStl {
public:
    Framework _framework;
    void input(std::string *str, std::vector<int> *vec);
};
CLASS_AS_MODULE(ConsumeStl);

