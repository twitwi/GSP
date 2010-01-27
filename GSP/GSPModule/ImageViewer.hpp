#include <framework.h>
#include <cxcore.h>

class ImageViewer
{
private:
	char *name_;
	
public:
	ImageViewer();
	void initModule();
	void stopModule();
	void image(IplImage* img);
	void setName(char *name);
	Framework _framework;

};

CLASS_AS_MODULE(ImageViewer);
