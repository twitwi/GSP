#include <stdlib.h>
#include <iostream>
#include <math.h>
#include "Detector.hpp"

/**
* Constructor
* @param width largeur de l'image
* @param height hauteur de l'image
* @param processStep frequence de traitement des pixels
*/
Detector::Detector()
  : width_(0)
  , height_(0)
  , processStep_(1)
  , deadImage_(0)
  , circular_(false)
{
}

/// Destructor
Detector::~Detector()
{
  for(std::list<IplImage*>::iterator it = detectionImages_.begin();
      it != detectionImages_.end();
      ++it)
  {
    cvReleaseImage(&(*it));
  }
  
  
  if (deadImage_)
    cvReleaseImage(&deadImage_);
//   if(currentImage_)
//     cvReleaseImage(&currentImage_);
}

/**
* Fonction d'initialisation du mask
*/
void Detector::initDeadImage( IplImage* deadImage)
{
  cvThreshold(deadImage,deadImage,20,255,CV_THRESH_BINARY);
  deadImage_ = deadImage;
}

void Detector::computeDetectionImages(std::list<ROIExtend> & rois)
{
  for(std::list<IplImage*>::iterator it = detectionImages_.begin();
      it != detectionImages_.end();
      ++it)
  {
    cvReleaseImage(&(*it));
  }
  
  detectionImages_.clear();
  
  for( std::list<ROIExtend>::iterator it = rois.begin();
       it != rois.end();
       ++it)
  {
    cout << "titi" << endl;
    detectionImages_.push_back(computeDetectionImage(*it));
  }
}
