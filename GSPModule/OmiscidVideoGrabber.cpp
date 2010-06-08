#include "OmiscidVideoGrabber.hpp"
#include <boost/bind.hpp>
#include <boost/date_time.hpp>

#include <iostream>

using namespace std;
using namespace boost;
using namespace boost::posix_time;

OmiscidVideoGrabber::OmiscidVideoGrabber()
  : ovs(0)
  , hostname("localhost")
  , id(0)
  , stop_pending(false)
{
}

OmiscidVideoGrabber::~OmiscidVideoGrabber()
{
}

void OmiscidVideoGrabber::initModule()
{
  ovs = new OmiscidVideoSource(hostname, id);
  
  stop_pending = false;
  my_thread = thread(bind(&OmiscidVideoGrabber::mainThread, this));

  cout << "grabber initialized" << endl;
}

void OmiscidVideoGrabber::stopModule()
{
  stop_pending = true;
  my_thread.join();
  if(ovs)
  {
    delete ovs;
  }
}

void OmiscidVideoGrabber::setHostname( const char* hostname )
{
  this->hostname = string(hostname);
}

void OmiscidVideoGrabber::setServiceId(int id)
{
  this->id = id;
}

void OmiscidVideoGrabber::mainThread()
{
  while(!stop_pending)
  {
    IplImage* img = ovs->getImage();
    
    emitNamedEvent("output", img);
    
//     boost::this_thread::sleep(boost::posix_time::milliseconds(20));
    
  }
}
