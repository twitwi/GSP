#ifndef DETECTOR_HPP
#define DETECTOR_HPP

#include "ROIExtend.hpp"
#include <opencv/highgui.h>
#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <string>
#include <iostream>
#include <list>

using namespace std;

/**
 * Classe abstraite implémentant les méthodes utillisés par les Detector (prend en entrée une image et fournit en sortie l'image de détection)
 */
class Detector {
protected:
  /// largeur de l'image
  unsigned int  width_; 
  /// hauteur de l'image
  unsigned int  height_;
  /// frequence de traitement des pixels
  unsigned int  processStep_;
  /// image de détection
  //IplImage* detectionImage_;
  /// image mask
  IplImage* deadImage_;
  /// image courante
  IplImage* currentImage_;
  /// boolean pour savoir si le detector travaille sur une zone suivie
  bool istarget_;
  /// boolean pour savoir si le detector travaille sur une image panoramique
  bool circular_;

  /// images de detection
  std::list<IplImage*> detectionImages_;
  
public:
  /**
   * Constructor
   */
  Detector();
  /// Destructor
  virtual ~Detector();

  /**
   * 
   * @param processStep frequence de traitement des pixels
   */
  void setProcessStep( int processStep);
  
  /**
   * Fonction retournant la frequence de traitements des pixels
   */
  unsigned int getProcessStep();
  /**
   * Fonction retournant l'image de detection
   */
  const std::list<IplImage*>& getDetectionImages();
  
  /**
   * Fonction retournant l'image mask
   */
  IplImage* getDeadImage();
  /**
   * Fonction retournant l'image courante
   */
  IplImage* getCurrentImage();
  /**
   * Fonction pour mettre l'image courante
   */
  void setCurrentImage(IplImage* currentImage);
  /**
   * Fonction pour mettre l'image de detection
   */
  //void setDetectionImage(IplImage* detectImage);
  
  /**
   * Fonction d'initialisation du mask
   */
  void initDeadImage( IplImage* deadImage);
  /**
   * Fonction pour mettre l'information du traitement ou non sur une cible suivie
   * @param target booleen pour savoir si c'est une cible suivie
   */
  void computeTarget(bool target) { istarget_ = target; }
  /**
   * Fonction pour mettre l'information du traitement ou non sur une cible suivie
   * @param target booleen pour savoir si c'est une cible suivie
   */
  void setCircular(bool circular) { circular_ = circular; }

  /**
   * Fonction qui calcule l'image de detection pour une liste de ROIs
   */
  void computeDetectionImages(std::list<ROIExtend> & rois);
  
  /**
   * Fonction virtuelle pure de traitement de l'image pour obtenir l'image de détection
   * @param roi zone traité
   */
  virtual IplImage* computeDetectionImage(ROIExtend & roi) = 0;
//   virtual void computeDetectionImage(int nbpixel, int* pixel) = 0;
  virtual void init() {};
  virtual void clean() {};
};

/**
 * ProcessStep setter function
 */
inline void Detector::setProcessStep( int processStep )
{ processStep_ = processStep; }

/**
 * Fonction retournant la frequence de traitements des pixels
 */
inline unsigned int Detector::getProcessStep()
{ return processStep_; }

/**
 * Fonction retournant l'image mask
 */
inline IplImage* Detector::getDeadImage()
{ return deadImage_; }

/**
 * Fonction retournant l'image de detection
 */
inline const std::list<IplImage*>& Detector::getDetectionImages()
{ return detectionImages_; }

/**
 * Fonction retournant l'image courante
 */
inline IplImage* Detector::getCurrentImage()
{ return currentImage_; }

/**
 * Fonction pour mettre l'image courante
 */
inline void Detector::setCurrentImage(IplImage* currentImage) {
  width_ = currentImage->width;
  height_ = currentImage->height;
  currentImage_ = currentImage;
  
//   IplImage* tmpcur = cvCreateImage(cvSize(currentImage->width,currentImage->height),IPL_DEPTH_8U,3);
//   cvZero(tmpcur);
//   cvCopy(currentImage,tmpcur,deadImage_);
//   cvResize(tmpcur,currentImage_); 
//   cvReleaseImage(&tmpcur);
}

/**
 * Fonction pour mettre l'image de detection
 */
// inline void Detector::setDetectionImage(IplImage* detectImage)
// { detectionImage_ = detectImage; }

#endif // DETECTOR_HPP


