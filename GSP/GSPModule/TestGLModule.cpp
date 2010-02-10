#include "TestGLModule.hpp"

#include <iostream>

#include <boost/bind.hpp>

using namespace std;
using namespace boost;

TestGLModule::TestGLModule() : t(0.) {}

TestGLModule::~TestGLModule() {};

void TestGLModule::initModule(){
}

void TestGLModule::stopModule() {};

void TestGLModule::clock()
{
	//cout << "clock" << endl;

	GLModule_EXEC(TestGLModule, execCode, 0);
}


// void TestGLModule::runCode( void* ptr )
// {
// 	TestGLModule* p = reinterpret_cast<TestGLModule*>(ptr);
// 	if(p){
// 		p->execCode();
// 	}
// }

void TestGLModule::execCode( void* )
{
	drawEllipse(0., 0., 1., 0.5, t);
	t+= 0.01;
  	swap();
}
