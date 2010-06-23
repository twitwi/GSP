
#include <framework.h>
#include <string>
#include <opencv/cxcore.h>
#include <opencv/highgui.h>

class ImageSource
{
private:
    bool stop_pending;

    bool gray;
    std::string url;
    int pixelStep;

    int mode; // 0 files, 1 video
    // mode dependent fields
public:    int imageIndex; // 0, 1
private:   CvCapture* video; // 1
public:
    IplImage *currentImage;
    
public:
    Framework _framework;
    ImageSource();
    void initModule();
    void stopModule();
    void setUrl(char *url);
    void setGray(bool gray);
    void setStart(int imageIndex);
    void setPixelStep(int pixelStep);
    void input();
    void skip();
};

CLASS_AS_MODULE(ImageSource);
