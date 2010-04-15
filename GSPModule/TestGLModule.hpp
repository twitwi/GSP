#include "GLModule.hpp"
#include <framework.h>

class TestGLModule : public GLModule
{
private:
	float t;
public:
	Framework _framework;
	TestGLModule();
	~TestGLModule();
	
	void initModule();

	void stopModule();

	void clock();

	//static void runCode(void *);
	void execCode(void*);
};

CLASS_AS_MODULE(TestGLModule);
