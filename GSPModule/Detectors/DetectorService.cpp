#include "DetectorService.hpp"

#include "ROIExtend.hpp"
#include <list>
#include <iostream>

using namespace Omiscid;
using namespace std;

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
  xmlDocPtr doc = xmlParseMemory((char*)(Msg.GetBuffer()),Msg.GetLength());
  if( doc ) {
    cout << "xml doc received : " << endl;
    cout << Msg.GetBuffer() << endl;
    
    XMLMessage* xmlmsg = new XMLMessage();
    xmlmsg->doc = doc;

    xmlNodePtr node = xmlmsg->GetRootNode();
    
//     xmlNodePtr nextNode = node->children;  // next node in the list
//     while(nextNode && nextNode->type != XML_ELEMENT_NODE)
//       nextNode = nextNode->next;
    
    std::list<ROIExtend> rois;
    
    while(node){
      cout << "next node" << endl;
      
      
      xmlAttrPtr attr_id = xmlmsg->FindAttribute("id",node);
      int id = atoi((const char*) attr_id->children->content);

      xmlAttrPtr attr_x1 = xmlmsg->FindAttribute ("x1", node);
      int x1 = atoi((const char*) attr_x1->children->content);
      xmlAttrPtr attr_y1 = xmlmsg->FindAttribute ("y1", node);
      int y1 = atoi((const char*) attr_y1->children->content);
      xmlAttrPtr attr_x2 = xmlmsg->FindAttribute ("x2", node);
      int x2 = atoi((const char*) attr_x2->children->content);
      xmlAttrPtr attr_y2 = xmlmsg->FindAttribute ("y2", node);
      int y2 = atoi((const char*) attr_y2->children->content);
      xmlAttrPtr attr_x3 = xmlmsg->FindAttribute ("x3", node);
      int x3 = atoi((const char*) attr_x3->children->content);
      xmlAttrPtr attr_y3 = xmlmsg->FindAttribute ("y3", node);
      int y3 = atoi((const char*) attr_y3->children->content);
      xmlAttrPtr attr_x4 = xmlmsg->FindAttribute ("x4", node);
      int x4 = atoi((const char*) attr_x4->children->content);
      xmlAttrPtr attr_y4 = xmlmsg->FindAttribute ("y4", node);
      int y4 = atoi((const char*) attr_y4->children->content);

/*      float dx=0.0;
      float dy=0.0;
      xmlAttrPtr attr_dx = xmlmsg->FindAttribute ("dx", node);
      if (attr_dx)
        dx= atof((const char*) attr_dx->children->content);
      xmlAttrPtr attr_dy = xmlmsg->FindAttribute ("dy", node);
      if (attr_dy)
        dy= atof((const char*) attr_dy->children->content);
*/
      node = node->next;
      while(node && node->type != XML_ELEMENT_NODE)
        node = node->next;
      
      rois.push_back(ROIExtend(x1, y1, x2, y2, x3, y3, x4, y4));
      
    }

    
    void * p_rois = (void*) &rois;
    emitNamedEvent("output", p_rois);
  }
  locker.LeaveMutex();
}
