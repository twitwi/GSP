#ifndef HISTOCOLORDETECTOR_HPP
#define HISTOCOLORDETECTOR_HPP

#include "Detector.hpp"
#include <opencv/cv.h>
#include <list>
#include <framework.h>
#include <boost/thread/mutex.hpp>

/**
 * Classe réalisant la différence de fond bayesienne
 */

class Histo2D {
 private:
    float *histVals; 
    float *gauss_; 
    int width;
    int height;
    float max;
 public:

  Histo2D(int w = 1, int h = 1);
  Histo2D(const Histo2D&copy);
  ~Histo2D();

  float getValue(int x, int y);

  float getNormalizedValue(float x, float y);

  void normalize();

  void setSize(int w, int h);

  friend istream&  operator >>(istream &is,Histo2D &obj);

  float getGourierValue(int x, int y, int w, int h, int r, int g);
};

class HistoColorDetector : public Detector {
 private:
  
  Histo2D histo;
  IplConvKernel* element_;
  boost::mutex mut;
  
 public:
  /**
   * Constructor
   */
  HistoColorDetector();
  /// Destructor
  ~HistoColorDetector();

  /**
   * Fonction executant une difference de fond bayesienne
   * @param roi zone d'interet pour notre traitement
   */
  IplImage* computeDetectionImage( ROIExtend & roi);
//   void computeDetectionImage(int nbpixel, int* pixel);

  /**
   * set the file to read the histogram,
   * given by the parameter "filename"
   */
  void setFilename( char* file_mixgauss );
  
  void initImage(IplImage* initImg);


  /**
   * signal to compute new image
   */
  void input( IplImage* img );
  
  void inputROI(void* rois);
  
  Framework _framework;
};

CLASS_AS_MODULE(HistoColorDetector);

#endif // HISTOCOLORDETECTOR_HPP

