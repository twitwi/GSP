#ifndef __OMISCID_VIDEO_GRABBER_HPP
#define __OMISCID_VIDEO_GRABBER_HPP

#include <framework.h>
#include <servicevideo/OmiscidVideoSource.hpp>
#include <boost/thread.hpp>
#include <string>

class OmiscidVideoGrabber
{
private:
  OmiscidVideoSource *ovs;

  std::string hostname;
  int id;
  
  boost::thread my_thread;
  bool stop_pending;
  
  void mainThread();
  
public:
  OmiscidVideoGrabber();
  ~OmiscidVideoGrabber();
  void initModule();
  void stopModule();
  void setHostname( const char* hostname);
  void setServiceId(int id);
  Framework _framework;
};

CLASS_AS_MODULE(OmiscidVideoGrabber);

#endif // __OMISCID_VIDEO_GRABBER_HPP
