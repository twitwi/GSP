#include "ImageViewer.hpp"

#include <opencv/highgui.h>
#include <cstring>
#include <gtk/gtk.h>
#include <boost/bind.hpp>

#include <stdio.h>
#include <iostream>

#include <unistd.h>
#include <algorithm>

using namespace boost;
using namespace std;

ImageViewer::ImageViewer()
  :name_(0)
  ,img_(0)
  ,stop_pending(false)
{
  selection = {0,0,0,0,Selection::STATE_NONE};
}

void ImageViewer::initModule()
{
  my_thread = thread(bind(&ImageViewer::mainThread, this));
}

void ImageViewer::stopModule()
{
  stop_pending = true;
  my_thread.join();
  
  if(name_)
  {
//     gdk_threads_enter();
    cvDestroyWindow( name_ );
//    cvWaitKey(20);
//     gdk_threads_leave();
    delete[] name_;
  }
  if(img_)
    cvReleaseImage(&img_);
}

void ImageViewer::setName(char *name)
{
  if(name_)
  {
//     gdk_threads_enter();
    cvDestroyWindow( name_ );
//     gdk_threads_leave();
    delete[] name_;
  }
  
  name_ = new char[strlen(name)];

  strcpy(name_, name);
  
//   gdk_threads_enter();
  cvNamedWindow(name_);
  cvSetMouseCallback(name_, ImageViewer::staticMouseCallback, this);
//   gdk_threads_leave();
 
}


void ImageViewer::input(IplImage* img)
{
  mut.lock();
  
  if(!img_)
    img_ = cvCloneImage(img);
  else{
    if(img->width != img_->width
       || img->height != img_->height
       || img->nChannels != img_->nChannels
       || img->depth != img_->depth)
    {
      cvReleaseImage(&img_);
      img_ = cvCloneImage(img);
    }else{
      cvCopy(img, img_);
    }
  }

  mut.unlock();
}

void ImageViewer::mainThread()
{
  cvSetMouseCallback(name_, ImageViewer::staticMouseCallback, this);
  
  while(!stop_pending)
  {
    if(img_){

//       gdk_threads_enter();

      cvWaitKey(50);
      
      mut.lock();

      if(selection.state == Selection::STATE_SELECTING)
      {
        cvRectangle(img_,
                    cvPoint(selection.x0, selection.y0),
                    cvPoint(selection.x1, selection.y1),
                    CV_RGB(0,255,0) );
      }else if(selection.state == Selection::STATE_FINISHED)
      {
        cvRectangle(img_,
                    cvPoint(selection.x0, selection.y0),
                    cvPoint(selection.x1, selection.y1),
                    CV_RGB(255,0,0));
      }
      
      cvShowImage(name_, img_);
      mut.unlock();
      //       gdk_threads_leave();      
      
      //cvWaitKey(5);
    }
    else{
      usleep(50000);
    }
  }
}

void ImageViewer::staticMouseCallback(int event, int x, int y, int flags, void* param)
{
  ImageViewer * ptr = static_cast<ImageViewer*>(param);
  ptr->mouseCallback(event, x, y, flags);
}

void ImageViewer::mouseCallback(int event, int x, int y, int flags)
{
  if(event == CV_EVENT_LBUTTONDOWN)
  {
    selection.x0 = x;
    selection.y0 = y;
    selection.x1 = x;
    selection.y1 = y;
    
    selection.state = Selection::STATE_SELECTING;
  }
  else if(event == CV_EVENT_MOUSEMOVE && (flags & CV_EVENT_FLAG_LBUTTON) && selection.state == Selection::STATE_SELECTING)
  {
    selection.x1 = x;
    selection.y1 = y;
  }
  else if(event == CV_EVENT_LBUTTONUP)
  {
    selection.x1 = x;
    selection.y1 = y;

    if(selection.x0 == selection.x1 && selection.y0==selection.y1)
    {
      mut.lock();
      emitNamedEvent("click", selection.x0, selection.y0, img_);
      mut.unlock();
    }else if(selection.x0 != selection.x1 && selection.y0!=selection.y1){
      int xmin = min(selection.x0, selection.x1);
      int xmax = max(selection.x0, selection.x1);
      int ymin = min(selection.y0, selection.y1);
      int ymax = max(selection.y0, selection.y1);
      mut.lock();
      emitNamedEvent("selection", xmin,  ymin,  xmax,  ymax, img_);
      mut.unlock();
    }
    selection.state = Selection::STATE_FINISHED;
  }else if(event == CV_EVENT_RBUTTONDOWN)
  {
    selection.state = Selection::STATE_NONE;
  }
}
