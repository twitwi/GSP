
#include "framework.h"

class Parent {
public:
    Framework _framework;
    void initModule();
};
CLASS_AS_MODULE(Parent);

class Child : public Parent {
public:
    void initModule();
};
CLASS_AS_MODULE(Child);

class GrandChild : public Parent {
public:
    void initModule();
};
CLASS_AS_MODULE(GrandChild);
