#ifndef __DETECTOR_SERVICE_HPP
#define __DETECTOR_SERVICE_HPP

#include <ServiceControl/UserFriendlyAPI.h>
#include <framework.h>
#include <string>

class DetectorService : public Omiscid::ConnectorListener
{
private:
  Omiscid::Service * service;
  std::string name;
  Omiscid::Mutex locker;
  
public:
  DetectorService();
  ~DetectorService();
  
  virtual void MessageReceived(Omiscid::Service& TheService,
                               const Omiscid::SimpleString LocalConnectorName,
                               const Omiscid::Message& Msg);
  
  void initModule();
  void stopModule();
  
  void setName(char *name);
  Framework _framework;
};

CLASS_AS_MODULE( DetectorService );

#endif // __DETECTOR_SERVICE_HPP
