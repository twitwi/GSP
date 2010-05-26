
#include <stdio.h>


#define SOF(a) fprintf(stderr, "size of %s is %ld\n", #a, sizeof(a));

int main() {
    SOF(void*);
    SOF(char);
    SOF(int);
    SOF(size_t);
    SOF(char*);
}
