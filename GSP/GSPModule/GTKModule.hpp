#include <framework.h>
#include <boost/thread.hpp>

class GTKModule
{
private:
	static bool already_initialized;
	static boost::thread gtk_thread;
	
public:
	Framework _framework;
	GTKModule();
	void initModule();
};

CLASS_AS_MODULE(GTKModule)
