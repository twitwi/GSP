#include "ColorAnalyser.hpp"

#include <iostream>

using namespace std;

void ColorAnalyser::input(int x, int y, IplImage* img)
{
  CvScalar val = cvGet2D( img, y, x );
  std::cout << val.val[0] << " "
            << val.val[1] << " "
            << val.val[2] << endl;
}
