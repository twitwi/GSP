#include <ServiceVideoModule.hpp>

#include <ServiceControl/UserFriendlyAPI.h>
#include <servicevideo/ConnectorsAndVariables.hpp>
#include <sys/time.h>
#include <iostream>

using namespace Omiscid;
using namespace std;

ServiceVideoModule::ServiceVideoModule()
{
}

ServiceVideoModule::~ServiceVideoModule()
{
}


void ServiceVideoModule::initModule()
{
	service = ServiceFactory.Create("ServiceVideo");

	SVPipelineEnding::SVProperties properties = {
		id,
		service,
		"/tmp/",
		"frame_",
		this,
		FMT_BGR24,
		1,
		false
	};

	svpipeline = new SVPipelineEnding(properties);
	
}

void ServiceVideoModule::stopModule()
{
	delete svpipeline;
	delete service;
}


void ServiceVideoModule::input( IplImage *img)
{
	struct timeval tv;
	gettimeofday(&tv, NULL);

	StdPixelFormat format = FMT_UNKNOWN;
	
	switch(img->nChannels)
	{
	case 1 :
		format = FMT_GRAY;
		break;
	case 3 :
		format = FMT_BGR24;
		break;
	};

	SendFrameCallback( img->imageData,
					   img->imageSize,
					   img->width,
					   img->height,
					   img->depth / 8 * img->nChannels,
					   tv,
					   format);
}

void ServiceVideoModule::inputRaw(void *data, int w, int h, int widthStep, int type)
{
	struct timeval tv;
	gettimeofday(&tv, NULL);
	StdPixelFormat format = FMT_UNKNOWN;
	switch(type)
	{
	case 8 :
		format = FMT_GRAY;
		break;
	case 24 :
		format = FMT_BGR24;
		break;
	};

	SendFrameCallback( (char*) data,
					   widthStep*h,
					   w,
					   h,
					   type / 8,
					   tv,
					   format);
}
	
void ServiceVideoModule::setServiceId( int id )
{
	this->id = id;
}
