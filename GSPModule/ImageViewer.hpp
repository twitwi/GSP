#include <framework.h>
#include <opencv/cxcore.h>
#include <boost/thread.hpp>
#include <boost/thread/mutex.hpp>


/**
 * Image Viewer Module
 * Inputs :
 *   - input (IplImage*)
 * Output :
 *   - selection (int, int, int, int, IplImage*)
 *   - click (int, int, IplImage*)
 *   - point (int, int, IplImage*)
 */
class ImageViewer
{
private:
  struct Selection{
    int x0;
    int y0;
    int x1;
    int y1;
    enum{ STATE_NONE,
          STATE_SELECTING,
          STATE_FINISHED
    } state;
  } selection;
  
  char *name_;
  IplImage *img_, *imgdraw_;

  boost::thread my_thread;
  bool stop_pending;
  boost::mutex mut;
  
  void mainThread();
  
  void mouseCallback(int event, int x, int y, int flags);
  static void staticMouseCallback(int event, int x, int y, int flags, void* param);
  
public:
  ImageViewer();
  void initModule();
  void stopModule();
  void input(IplImage* img);
  void setName(char *name);
  Framework _framework;
};

CLASS_AS_MODULE(ImageViewer);
