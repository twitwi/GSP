#include "HistoColorDetector.hpp"
#include <iostream>
#include <fstream>

using namespace std;

//#define DEBUG_AFFICHAGE 1

Histo2D::Histo2D(int w, int h)
{
    histVals = new float[w * h];
    width = w;
    height = h;
/*
gauss_ = new float[2000];
for(int i = 0; i < 2000; i++){
  gauss_[i] = exp( -i * i / 500.0 / 500.0 / 4.0 );
}
*/
}

Histo2D::Histo2D(const Histo2D&copy)
{
    delete [] histVals;
    histVals = new float[copy.width * copy.height];
    width = copy.width;
    height = copy.height;
    fprintf(stderr, "COPY\n");

    memcpy(copy.histVals, histVals, width * height * sizeof(float));

/*
gauss_ = new float[2000];
for(int i = 0; i < 2000; i++){
  gauss_[i] = exp( -i * i / 500.0 / 500.0 / 4.0 );

}
*/
}

Histo2D::~Histo2D()
{
    delete [] histVals;
}

void
Histo2D::setSize(int w, int h)
{
    if ((w == width) && (h == height))
        return;
    delete [] histVals;
    width = w;
    height = h;
    fprintf(stderr, "Histo size : %d x %d\n", width, height);
    histVals = new float[width * height];
}

float
Histo2D::getValue(int x, int y)
{
    return histVals[y * width + x];
}

void
Histo2D::normalize()
{

    fprintf(stderr, " MAX : %f\n", max);
    if (max != 0)
    {
        for (int i = 0; i < width * height; i++)
        {
            histVals[i] /= max;
        }

    }
    max = 1;
}



float
Histo2D::getNormalizedValue(float  x, float y)
{
//    if (((int)(y * height)) * width + (int)(x * width) >= width*height)
//      std::cout<<"Ouille:"<<y<<" "<<x<<" "<<width<<" "<<height<<std::endl;
    return histVals[((int)(y * height)) * width + (int)(x * width)];
}

istream&  operator >>(istream &is,Histo2D &obj)
{
    int width, height;
    is >> width >> height;
    obj.setSize(width, height);
    obj.max = 0;
    for (int i = 0; i < obj.width * obj.height; i++)
    {
        is >> obj.histVals[i];
        obj.max = obj.histVals[i] > obj.max ? obj.histVals[i] : obj.max;
    }
std::cout<<"w:"<<width<<" h:"<<height<<" max:"<<obj.max<<std::endl;

    return is;
}

float
Histo2D::getGourierValue(int x, int y, int w, int h, int r, int g) {
  int mx = (2*x + w) / 2;
  int my = (2*y + h) / 2;
  float gg1 = 0.0;
  float gg2 = 0.0;

  float sigma = 500;
  //float sigma = 50;
  int indice1 = (int)(sigma * (x - mx) / w + 0.5);
  if(indice1 < 0) indice1 = -indice1;
  int indice2 = (int)(sigma * (y - my) / h + 0.5);
  if(indice2 < 0) indice2 = -indice2;
//  std::cout<<"indice:"<< indice1<<" "<<indice2<<std::endl;
  if(indice1 < 2000)
    gg1 = gauss_[indice1];
  if(indice2 < 2000)
    gg2 = gauss_[indice2];

  int size = width*height;
  return histVals[r+64*g]*gg1*gg2;
}

HistoColorDetector::HistoColorDetector()
{
  int element_shape = CV_SHAPE_ELLIPSE;
  element_ = cvCreateStructuringElementEx( 4, 4, 1, 1, element_shape, 0 );
#ifdef DEBUG_AFFICHAGE
  cvNamedWindow("debughisto",0);
#endif
}



// Load histogram
void HistoColorDetector::setFilename( char* file_mixgauss )
{
  cout << "open histogram : \""<< file_mixgauss << "\"" << endl;
  ifstream file (file_mixgauss);
  file >> histo;
  histo.normalize();
  cout << "histogram loaded" << endl;
}

/// Destructor
HistoColorDetector::~HistoColorDetector()
{
  cvReleaseStructuringElement(&element_);
#ifdef DEBUG_AFFICHAGE
  cvDestroyWindow("debughisto");
#endif

}

IplImage *
HistoColorDetector::computeDetectionImage(ROIExtend & roi) {

  cout << "HistoColorDetector::computeDetectionImage" << endl;
  
  int y1 = roi.getTop();
  int y2 = roi.getBottom();
  int x1 = roi.getLeft();
  int x2 = roi.getRight();
  int w = (roi.getRight()-roi.getLeft()) / processStep_;
  int h = (roi.getBottom()-roi.getTop()) / processStep_;
  

#ifdef DEBUG_AFFICHAGE
  IplImage* affich_img = cvCreateImage(cvSize(currentImage_->width,currentImage_->height),IPL_DEPTH_8U,1);
  cvZero(affich_img);
  unsigned char* ptr_affich_img_line = (unsigned char*)affich_img->imageData;
  int step4       = affich_img->widthStep/sizeof(uchar);
#endif

  IplImage *detectionImage = cvCreateImage(cvSize(w,h),IPL_DEPTH_8U,1);
  
  IplImage* yuvImage = cvCreateImage(cvSize(currentImage_->width,currentImage_->height),IPL_DEPTH_8U,3);

  cvCvtColor(currentImage_,yuvImage,CV_BGR2YCrCb);
  
  
  unsigned char* ptr_current_img_line = (unsigned char*)yuvImage->imageData;
  //unsigned char* ptr_current_img_line = (unsigned char*)currentImage_->imageData;
  unsigned char* ptr_prob_img_line = (unsigned char*)detectionImage->imageData;

  int step1       = yuvImage->widthStep/sizeof(uchar);
  int channels   = yuvImage->nChannels;
/*
  int step1       = currentImage_->widthStep/sizeof(uchar);
  int channels   = currentImage_->nChannels;
*/
  int step2       = detectionImage->widthStep/sizeof(uchar);

  int j = 0;
  for (int y=y1/processStep_; j<h; y++, j++) {
    int i = 0;
    for (int x=x1/processStep_;i<w;x++,i++) {

      int lum = (int)((ptr_current_img_line[y*step1+x*channels + 0]));
      int cr = (int)((ptr_current_img_line[y*step1+x*channels + 2]));
      int cb = (int)((ptr_current_img_line[y*step1+x*channels + 1]));

/*
      float bx = ptr_current_img_line[y*step1+x*channels + 0];
      float ry = ptr_current_img_line[y*step1+x*channels + 2];
      float gz = ptr_current_img_line[y*step1+x*channels + 1];

      int r = (int)(64*ry/(ry+gz+bx+1)+0.5);
      int g = (int)(64*gz/(ry+gz+bx+1)+0.5);
*/
      int val = (histo.getNormalizedValue(cr / 255.0, cb / 255.0 ))*255;
/*
if (val > 0)
  val = 255;
*/
 //     int val = (histo.getNormalizedValue((x / 640.0), (y/480.0)))*255;
/*
      if (lum < 25 || lum > 225)
        val = 0;

      if (val > 180)
        val = 255;
      else
        val = 0;


      //float val = histo.getGourierValue(x,y,w,h,r,g);
      if (val > 0.5)
*/
      if (val > 25)
        val = 255;


      

      ptr_prob_img_line[j*step2+i]= (uchar)val;
 #ifdef DEBUG_AFFICHAGE
      ptr_affich_img_line[y*step4+x] = (unsigned char)val;
#endif
    }
  }
//  cvErode(detectionImage_,detectionImage_,element_,1);
//  cvDilate(detectionImage_,detectionImage_,element_,3);
#ifdef DEBUG_AFFICHAGE
//  cvDilate(affich_img,affich_img,element_,1);
  cvErode(affich_img,affich_img,element_,1);
  cvDilate(affich_img,affich_img,element_,1);

  cvShowImage("debughisto",affich_img);
  cvWaitKey(2);
  cvReleaseImage(&affich_img);
#endif

  
  cvReleaseImage(&yuvImage);

  return detectionImage;
}

void
HistoColorDetector::initImage(IplImage* initImg) {
  currentImage_ = cvCloneImage(initImg);
}


// void
// HistoColorDetector::computeDetectionImage(int nbpixel, int* pixel) {
//   if (detectionImage_)
//     cvReleaseImage(&detectionImage_);
  
// #ifdef DEBUG_AFFICHAGE
//   IplImage* affich_img = cvCreateImage(cvSize(currentImage_->width,currentImage_->height),IPL_DEPTH_8U,1);
//   cvZero(affich_img);
//   unsigned char* ptr_affich_img_line = (unsigned char*)affich_img->imageData;
//   int step4       = affich_img->widthStep/sizeof(uchar);
// #endif


//   detectionImage_ = cvCreateImage(cvSize(nbpixel,1),IPL_DEPTH_32F,1);
//   IplImage* yuvImage = cvCreateImage(cvSize(currentImage_->width,currentImage_->height),IPL_DEPTH_8U,3);

//   cvCvtColor(currentImage_,yuvImage,CV_BGR2YCrCb);

//   unsigned char* ptr_current_img_line = (unsigned char*)yuvImage->imageData;
//   float* ptr_prob_img_line = (float*)detectionImage_->imageData;

//   int step1       = yuvImage->widthStep/sizeof(uchar);
//   int channels   = yuvImage->nChannels;
  
//   int count = 0;
//   for (int i=0; i<nbpixel; i+=2) {
//     int x = pixel[i];
//     int y = pixel[i+1];
//     if (!(x >= currentImage_->width || x < 0 || y < 0 || y >= currentImage_->height)) {
//       int lum = (int)((ptr_current_img_line[y*step1+x*channels + 0]));
//       int cr = (int)((ptr_current_img_line[y*step1+x*channels + 2]));
//       int cb = (int)((ptr_current_img_line[y*step1+x*channels + 1]));


//       int val = (histo.getNormalizedValue(cb / 255.0, cr / 255.0 ))*255;
//       ptr_prob_img_line[count]= (float)val;

//  #ifdef DEBUG_AFFICHAGE
//       ptr_affich_img_line[y*step4+x] = (unsigned char)val;
// #endif

//     }
//     else
//       ptr_prob_img_line[count]= -1.0;
//     count++;
//   }
//   cvReleaseImage(&yuvImage);

// #ifdef DEBUG_AFFICHAGE
//   cvDilate(affich_img,affich_img,element_,1);
//   cvErode(affich_img,affich_img,element_,1);

//   cvShowImage("debughisto",affich_img);
//   cvWaitKey(2);
//   cvReleaseImage(&affich_img);
// #endif
// }


void HistoColorDetector::input( IplImage* img )
{
  mut.lock();
  
  setCurrentImage(img);

  mut.unlock();
  
//   std::list<ROIExtend> rois;
//   rois.push_back( ROIExtend(0,0,width_, 0, width_, height_, 0, height_));
//   computeDetectionImages( rois );
  
//   emitNamedEvent("output", getDetectionImages().front());  
}

void HistoColorDetector::inputROI( void* rois )
{
  mut.lock();
  
  std::list<ROIExtend> * p_rois = static_cast<std::list<ROIExtend>*>(rois);
  computeDetectionImages( *p_rois );
  
  mut.unlock();
  if( !p_rois->empty())
    emitNamedEvent("output", getDetectionImages().front());
}
