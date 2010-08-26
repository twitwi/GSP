#include <framework.h>
#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <string>

/**
 * Color Seq Detector Module
 * Inputs :
 *   - input (IplImage*)
 * Output :
 *   - output (IplImage*)
 * Parameter :
 *   - period (int) : color duration in frames
 */

class ColorSeqDetector
{
private:
  IplImage *img;
  
  IplImage *stateImg;

  int period;
  
  bool isRed(unsigned char r, unsigned char g, unsigned char b);
  bool isGreen(unsigned char r, unsigned char g, unsigned char b);
  bool isBlue(unsigned char r, unsigned char g, unsigned char b);
  bool isYellow(unsigned char r, unsigned char g, unsigned char b);
  
public:
  ColorSeqDetector();
  void initModule();
  void stopModule();
  void input(IplImage *inputImg);
  
  void setPeriod(int);
  
  Framework _framework;
};

CLASS_AS_MODULE(ColorSeqDetector);
