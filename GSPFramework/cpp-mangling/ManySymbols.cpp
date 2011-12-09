
#define SOMETHING int i = 0; i = (i+1)/i;

extern "C" {
    void cvoidfunction(char* whatever) {}
    int cfunction(char* what, int ever) {return ever;}
}

class CCC{};
class BCA{};
namespace NNN{class DDD{};}

void func000(char* whatever) {}
int func001(char* what, int ever) {return ever;}
void func002(char** what, int** ever) {}
void func003(CCC what, CCC* w, CCC** ww, CCC*** www, CCC**** wwww) {}
void func008(int i, int ii, int iii, CCC what, CCC* w, CCC** ww, CCC*** www, CCC**** wwww) {}
void func009(CCC what, CCC** w, CCC**** ww, CCC*** www, CCC******** wwww) {}
void func007(CCC what, int i, CCC* w, int i2,  CCC ww, int i3, CCC* www, int i4, CCC* wwww, int i5, CCC** wwwww) {}
void func004(CCC what, CCC* w, CCC ww, CCC* www, CCC* wwww, CCC** wwwww) {}
void func005(CCC**** base, CCC*** what, CCC** w, CCC* ww, CCC www) {}
void func010(CCC** a, CCC*** what, CCC w) {}
void func011(CCC** a, CCC* what, CCC*** w) {}
void func012(CCC*** a, CCC* what, CCC*** w) {}
void func006(CCC what, BCA* w,  BCA ww, CCC* www, CCC* wwww, CCC** wwwww) {}
void func013(char**** base, char*** what, char** w, char* ww, char www) {}
void func014(char************ a, char************ b) {}
void func015(char****************** a, char****************** b) {}
void func016(char************************************* a, char************************************* b) {}
void func017(char************************************** a, char************************************** b) {}
void func018(int*i, int ii, int* iii, float* f, float ff) {}
void func019(char************************************** a, char**************** b) {}
void func020(NNN::DDD**a){}

namespace NA{ // S_
    namespace NB { // S0_
        namespace NC { // S1_
            class YYY {};
            class ZZZ {};
            void func100(int*i, int ii){}
            void func101(ZZZ*i, ZZZ**ii){} // ZZZ S2_  ; ZZZ* S3_
            void func102(char* s, ZZZ*i, ZZZ**ii, char *s2){} // char* S2_ ; ZZZ S3_ ;  ZZZ S4_ 
            void func103(ZZZ*i, YYY*ii){}
        }
        void func110(NC::ZZZ*i, NC::ZZZ**ii){} // ZZZ S2_  ; ZZZ* S3_
        void func112(char* s, NC::ZZZ*i, NC::ZZZ**ii, char *s2){} // char* S1_ ; NC S2_ ; ZZZ S3_ ;  ZZZ* S4_ 
        namespace ND {
            void func120(NC::ZZZ*i, NC::ZZZ**ii){} // ZZZ S2_  ; ZZZ* S3_
            void func122(char* s, NC::ZZZ*i, NC::ZZZ**ii, char *s2){} // char* S2_ ; NC S3_ ; ZZZ S4_ ;  ZZZ* S5_ 
        }
    }
}
namespace std {
    void func130(int i){}
    namespace plop {
        void func131(int i){}
        namespace std {
            void func132(int i){}
        }
    }
}
void funcSt140(int i){}

// this might be of some help:
// https://llvm.org/svn/llvm-project/cfe/tags/Apple/objc_translate-34/src/tools/clang/lib/CodeGen/Mangle.cpp

#include <vector>
#include <string>
#include <map>

void func200(std::string da, std::string daa){} // Ss
void func201(std::vector<CCC*> da, std::vector<CCC*> daa){} // !!!
void func202(std::map<std::string, CCC*> da){} // !!!

// helper to write Bridj tests
void test_no_params(){}
long test_add9_long(long l1,long l2,long l3,long l4,long l5,long l6,long l7,long l8,long l9){}
class Ctest {
public:
    Ctest();
    int test_add(int a, int b);
    static int test_add_static(int a, int b);
};
Ctest::Ctest() {SOMETHING}
int Ctest::test_add(int a, int b) {return a+b;}
int Ctest::test_add_static(int a, int b) {return a+b;}
double sinInt(int a) {return a;}
int forwardCall(void*a, int b, int c) {return 0;}
void pointerAliases(void** a, void* b, void*** c, int** d) {}

#include <iostream>
void streams(std::istream i, std::ostream o, std::iostream io){}
void streamsI(std::basic_istream< char, std::char_traits<char> > i){}
void streamsO(std::basic_ostream< char, std::char_traits<char> > o){}
void streamsIO(std::basic_iostream< char, std::char_traits<char> > io){}

