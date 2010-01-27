#include "ImageViewer.hpp"

#include <highgui.h>
#include <cstring>

#include <stdio.h>

ImageViewer::ImageViewer()
	:name_(0)
{}

void ImageViewer::initModule()
{
}

void ImageViewer::stopModule()
{
	if(name_)
	{
		cvDestroyWindow( name_ );
		delete[] name_;
	}
}

void ImageViewer::setName(char *name)
{
	if(name_)
	{
		cvDestroyWindow( name_ );
		delete[] name_;
	}
	
	name_ = new char[strlen(name)];
	
	strcpy(name_, name);
	cvNamedWindow(name_);
}


void ImageViewer::image(IplImage* img)
{
	cvShowImage(name_, img);
}
