#include <framework.h>

#include <cv.h>

class GTKModule
{
public:
	Framework _framework;

	GTKModule(void);

	void initModule(void);
    void stopModule(void);

    void setName(const char *name);

    void inputRGB(const unsigned char *rgb, int width, int height);
    void input(const IplImage* ipl);

    void refreshWindowImage(void);

private:
    GtkWidget *window;
    GtkWidget *image;
    GMutex *mutex;
    GThread *gtkmainThread;
};
CLASS_AS_MODULE(GTKModule)

