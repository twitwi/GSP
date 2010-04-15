#include "GTKModule.hpp"
#include <gtk/gtk.h>
#include <boost/bind.hpp>

using namespace boost;

bool GTKModule::already_initialized = false;
boost::thread GTKModule::gtk_thread;

GTKModule::GTKModule()
{
}

void GTKModule::initModule()
{
	if(!already_initialized){
		int argc = 0;
		char **argv = 0;
		
		g_thread_init(NULL);
		gdk_threads_init();
		gdk_threads_enter();
		gtk_init(&argc, &argv);
		
		gtk_thread = thread(&gtk_main);
		gdk_threads_leave ();
		already_initialized = true;
	}

}
