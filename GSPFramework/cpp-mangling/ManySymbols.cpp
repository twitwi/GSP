
extern "C" {
    void cvoidfunction(char* whatever) {}
    int cfunction(char* what, int ever) {return ever;}
}

class CCC{};
class BCA{};

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
