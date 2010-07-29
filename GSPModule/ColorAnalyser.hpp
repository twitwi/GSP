#include <framework.h>
#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <string>

/**
 * Color Analyser Module
 * Inputs :
 *   - input (int, int, IplImage*)
 */

class ColorAnalyser
{
public:
  void input(int, int, IplImage*);

  Framework _framework;
};

CLASS_AS_MODULE(ColorAnalyser);
