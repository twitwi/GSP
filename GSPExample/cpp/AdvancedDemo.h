
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



struct CustomType { // for python ctypes test
private: // to check that it does not break the layout
    float score;
public:  // to check that it does not break the layout
    int x,y;
    friend class ProduceAndConsumeCustomType;
};

class ProduceAndConsumeCustomType {
public:
    Framework _framework;
    void tick(int i); // to produce
    void input(CustomType *value);
    void inputArray(int count, CustomType *values);
};
CLASS_AS_MODULE(ProduceAndConsumeCustomType);


namespace heeere {
    struct Type {int x,y;};

    class ProduceAndConsumeNSType {
    public:
        Framework _framework;
        void input(int i); // to produce
        void o(Type *value);
    };
    CLASS_AS_MODULE(ProduceAndConsumeNSType);
};
