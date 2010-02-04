#ifndef __SERVICE_VIDEO_MODULE_HPP
#define __SERVICE_VIDEO_MODULE_HPP

#include <framework.h>
#include <VideoSource.hpp>
#include <SVPipeline.hpp>
#include <opencv/cxcore.h>
#include <ServiceControl/UserFriendlyAPI.h>

class ServiceVideoModule : public VideoSource
{
private:
	Omiscid::Service *service;
	SVPipelineEnding *svpipeline;
	int id;
	
public:
	ServiceVideoModule();
	~ServiceVideoModule();
	
	void initModule();
	void stopModule();

	void setServiceId(int id);
	
	void input(IplImage *img);
	void inputRaw(void *data, int w, int h, int widthStep, int type);
	
	Framework _framework;
};

CLASS_AS_MODULE(ServiceVideoModule);

#endif // __SERVICE_VIDEO_MODULE_HPP
