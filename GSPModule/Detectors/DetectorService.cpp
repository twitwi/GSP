#include "DetectorService.hpp"

#include "ROIExtend.hpp"
#include "CVUtils.hpp"
#include <list>
#include <iostream>
#include <Messaging/Messaging.h>

using namespace Omiscid;
using namespace std;
using namespace Omiscid::Messaging;

DetectorService::DetectorService()
  :name("DetectorService")
{ 
}

DetectorService::~DetectorService()
{ 
}

void DetectorService::setName( char *name )
{
  this->name = name;
}

void DetectorService::initModule()
{
  service = ServiceFactory.Create(name.c_str());
  service->AddConnector("info2D","info 2D Service\n", AnInOutput );
  service->AddConnector("infoGui","info for Output gui\n", AnOutput );
  service->AddConnectorListener( "info2D", this );
  service->Start();
}

void DetectorService::stopModule()
{
  delete service;
}

void DetectorService::MessageReceived(Omiscid::Service& TheService,
                                      const Omiscid::SimpleString LocalConnectorName,
                                      const Omiscid::Message& Msg)
{
  locker.EnterMutex();
  
  StructuredMessage smsg( (char*)(Msg.GetBuffer()) );

  if(smsg.Has("ROIs"))
  {
    std::list<ROIExtend> rois;
    smsg.Get("ROIs", rois);      
//     void * p_rois = (void*) &rois;
//     emitNamedEvent("output", p_rois);
    std::list<ROIExtend>* p_rois = &rois;
    emitNamedEvent("output", p_rois);
  }

  if(smsg.Has("Points"))
  {
    std::vector<CvPoint> points;
    smsg.Get("Points", points);
    std::vector<CvPoint>* p_points = &points;
    emitNamedEvent("points", p_points);
  }
  locker.LeaveMutex();
}

void DetectorService::inputROI(std::list<IplImage*>* rois, int peerId)
{
  if(peerId!=0)
  {
    
  }
}

void DetectorService::inputPoints(std::vector<float>* values, int peerId)
{
  if(peerId!=0)
  {
    StructuredMessage smsg;
    smsg.Put("Points", *values);
    
    service->SendToOneClient("info2D", smsg.Encode(), peerId);
  }
}
