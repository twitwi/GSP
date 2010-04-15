#include <framework.h>
#include <opencv/cxcore.h>

class ImageViewer
{
private:
	char *name_;
	
public:
	ImageViewer();
	void initModule();
	void stopModule();
	void input(IplImage* img);
	void setName(char *name);
	Framework _framework;

};

CLASS_AS_MODULE(ImageViewer);
