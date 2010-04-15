#include <framework.h>
#include <opencv/cxcore.h>
#include <boost/thread.hpp>


class ImageLoader
{
private:
	void mainThread();
	IplImage *img;
	boost::thread my_thread;
	bool stop_pending;
	
public:
	ImageLoader();
	void initModule();
	void stopModule();
	void setFilename(char *filename);
	Framework _framework;

};

CLASS_AS_MODULE(ImageLoader);
