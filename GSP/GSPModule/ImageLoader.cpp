#include "ImageLoader.hpp"
#include <opencv/highgui.h>
#include <boost/bind.hpp>
#include <unistd.h>

using namespace boost;

ImageLoader::ImageLoader()
	: img(0)
	, stop_pending(false)
{
}

void ImageLoader::setFilename( char *filename )
{
	if(img)
	{
		cvReleaseImage(&img);
	}
	
	img = cvLoadImage(filename);
}

void ImageLoader::initModule()
{
	my_thread = thread(bind(&ImageLoader::mainThread, this));
}

void ImageLoader::stopModule()
{
	stop_pending = true;
	my_thread.join();
}

void ImageLoader::mainThread()
{
	while(!stop_pending)
	{
		emitNamedEvent("output", img);
		usleep(50000);
	}
}
