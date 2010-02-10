#include "GLModule.hpp"

#include <iostream>
#include <sstream>
#include <stdexcept>
#include <stdarg.h>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <boost/date_time.hpp>

using namespace std;
using namespace boost;
using namespace boost::posix_time;

bool GLModule::t = true;
Display* GLModule::dpy;
Window GLModule::win;
GLXContext GLModule::ctx;
GLuint GLModule::base;
int GLModule::key=-1;
bool GLModule::button_pressed=false;
bool GLModule::button_released=false;
int GLModule::mouse_x = 0;
int GLModule::mouse_y = 0;
bool GLModule::ready = false;

boost::thread GLModule::gl_thread;
boost::condition_variable GLModule::cond;
boost::mutex GLModule::mut;
boost::mutex GLModule::mut2;
std::queue< GLModule::Task > GLModule::task_queue;

void GLModule::initGL()
{
	dpy = XOpenDisplay( NULL );
	if ( !dpy )
	{
 		fprintf( stderr, "Error: XOpenDisplay failed\n" );
 		abort();
	}
	// Choose GLX visual / pixel format
 	int visAttributes[] = {
 		GLX_RGBA,
 		GLX_DOUBLEBUFFER,
		GLX_DEPTH_SIZE, 16,
		GLX_RED_SIZE, 8,
		GLX_GREEN_SIZE, 8,
		GLX_BLUE_SIZE, 8,
		None
 	};
	
	XVisualInfo *visinfo = glXChooseVisual( dpy, DefaultScreen(dpy), visAttributes );
	if ( !visinfo )
	{
 		fprintf( stderr, "Error: couldn't get an RGB, Double-buffered "
				 "visual\n" );
 		abort();
	}
	
	// Create the window
	Window root = RootWindow( dpy, 0 );
	XSetWindowAttributes attr;
	attr.background_pixel = 0;
	attr.border_pixel = 0;
	attr.colormap = XCreateColormap( dpy, root, visinfo->visual, AllocNone );
	
	unsigned long attrMask;
	attrMask = CWBackPixel | CWBorderPixel | CWColormap;
	
	win = XCreateWindow( dpy, root,
						 0, 0,
						 512, 512,
 						 0, visinfo->depth, InputOutput,
 						 visinfo->visual, attrMask, &attr );
	if ( !win )
	{
 		fprintf( stderr, "Error: XCreateWindow failed\n" );
 		abort();
	}
	
	// Set standard and WM properties
	
// 	XSetCommand( dpy, win, argv, argc );
	XStoreName( dpy, win, "GL Window" );
	XSetIconName( dpy, win, "GL Window" );


// Ask for event notify
	long event_mask =
		StructureNotifyMask
		| KeymapStateMask
		| KeyPressMask
		| KeyReleaseMask
		| ButtonPressMask
		| ButtonReleaseMask
		| ExposureMask
		| PointerMotionMask
		| Button1MotionMask
		| Button2MotionMask
		| Button3MotionMask
		| Button4MotionMask
		| Button5MotionMask
		| ButtonMotionMask
		| FocusChangeMask ;
    XSelectInput( dpy, win, event_mask );
	
	XMapWindow( dpy, win );
	
	// Create GLX rendering context
	ctx = glXCreateContext( dpy, visinfo, NULL, True );
	if ( !ctx )
	{
		fprintf( stderr, "Error: glXCreateContext failed\n" );
 		abort();
	}
	
	// Bind the rendering context and window
	glXMakeCurrent( dpy, win, ctx );

	// Init GLEW
	GLenum err = glewInit();
	
	if (GLEW_OK != err)
    {
		std::cout << "Error:" << glewGetErrorString(err) << std::endl;
		return;
    }
	cout << "OpenGL Vendor: " << (char*) glGetString(GL_VENDOR) << "\n";
	cout << "OpenGL Renderer: " << (char*) glGetString(GL_RENDERER) << "\n";
	cout << "OpenGL Version: " << (char*) glGetString(GL_VERSION) << "\n\n";
	

	//create Font
	buildFont();
	
	glClearColor(0,0,0,1);
	
	glClear(GL_COLOR_BUFFER_BIT);
	glFlush();
	
	glPushAttrib(GL_ALL_ATTRIB_BITS);

}

void GLModule::swap()
{
	//glFinish();
	//glXWaitX();
	glXSwapBuffers(dpy,win);
	glClearColor(0., 0., 0., 1.);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

void GLModule::printErrors(const char *file, int line)
{
	GLenum glErr = glGetError();
	while ( glErr != GL_NO_ERROR )
	{
//     if ( glErr != GL_INVALID_VALUE )
		std::cerr<< "GL Error in the file \""<<file<<"\", line "<<line<<" : " << glErr;
		if(gluErrorString(glErr))
			std::cerr<<" "<<gluErrorString(glErr);
		std::cerr<<std::endl;
		
		glErr = glGetError();
	}
}

GLModule::GLModule()
{
	if(t){
		t = false;
		gl_thread = thread(mainThread);
	}
}

GLModule::~GLModule()
{
	//killFont();
}

void GLModule::drawQuad()
{
	glBegin( GL_QUADS );
	glTexCoord2f(0., 1.);
	glVertex2f(-1., 1.);
	glTexCoord2f(1., 1.);
	glVertex2f(1., 1.);
	glTexCoord2f(1., 0.);
	glVertex2f(1., -1.);
	glTexCoord2f(0., 0.);
	glVertex2f(-1., -1.);
	glEnd();
}

void GLModule::drawQuad(int w, int h)
{
	float epsw = 0.;//1/(2.*w);
	float epsh = 0.;//1/(2.*h);
 	glBegin( GL_QUADS );
 	glTexCoord2f(-epsw, 1.-epsh);
 	glVertex2f(-1., 1.);
 	glTexCoord2f(1.-epsw, 1.-epsh);
 	glVertex2f(1., 1.);
 	glTexCoord2f(1.-epsw, -epsh);
 	glVertex2f(1., -1.);
 	glTexCoord2f(-epsw, -epsh);
 	glVertex2f(-1., -1.);

//  	glTexCoord2f(0.-epsw, 1.+epsh);
//  	glVertex2f(-1., 1.);
//  	glTexCoord2f(1.+epsw, 1.+epsh);
//  	glVertex2f(1., 1.);
//  	glTexCoord2f(1.+epsw, 0.-epsh);
//  	glVertex2f(1., -1.);
//  	glTexCoord2f(0.-epsw, 0.-epsh);
//  	glVertex2f(-1., -1.);

	glEnd();
}

void GLModule::drawEllipse(float x0, float y0,
							  float ax, float ay,
							  float angle, int resolution)
{
	glBegin(GL_LINE_LOOP);
	
	float alpha = cos(angle);
	float beta = -sin(angle);
	for(float i=0.; i < 2.*M_PI; i+=2.*M_PI / resolution)
	{
		float x = ax * cos(i);
		float y = ay * sin(i);
		glVertex2f(x0 + x * alpha + y * beta,
				   y0 - x * beta + y * alpha);
	}
	glEnd();
}

void GLModule::drawFilledEllipse(float x0, float y0,
                              float ax, float ay,
                              float angle, int resolution)
{
  glBegin(GL_POLYGON);
	
  float alpha = cos(angle);
  float beta = -sin(angle);
  for(float i=0.; i < 2.*M_PI; i+=2.*M_PI / resolution)
  {
    float x = ax * cos(i);
    float y = ay * sin(i);
    glVertex2f(x0 + x * alpha + y * beta,
               y0 - x * beta + y * alpha);
  }
  glEnd();
}
// method to display text
void GLModule::buildFont()
{
	Display *dpy;
	XFontStruct *fontInfo;  // storage for our font.
	
    base = glGenLists(96); // storage for 96 characters.

    // load the font.  what fonts any of you have is going
    // to be system dependent, but on my system they are
    // in /usr/X11R6/lib/X11/fonts/*, with fonts.alias and
    // fonts.dir explaining what fonts the .pcf.gz files
    // are.  in any case, one of these 2 fonts should be
    // on your system...or you won't see any text.

    // get the current display.  This opens a second
    // connection to the display in the DISPLAY environment
    // value, and will be around only long enough to load
    // the font.
    dpy = XOpenDisplay(NULL); // default to DISPLAY env.
	
    fontInfo = XLoadQueryFont(dpy, "-adobe-helvetica-bold-r-normal--24-*-*-*-p-*-iso8859-1");
    if (fontInfo == NULL) {
		fontInfo = XLoadQueryFont(dpy, "fixed");
		if (fontInfo == NULL) {
			printf("no X font available?\n");
		}
    }
	
    // after loading this font info, this would probably be the time
    // to rotate, scale, or otherwise twink your fonts.
	
    // start at character 32 (space), get 96 characters (a few characters past z), and
    // store them starting at base.
    glXUseXFont(fontInfo->fid, 32, 96, base);
	
    // free that font's info now that we've got the
    // display lists.
    XFreeFont(dpy, fontInfo);
	
    // close down the 2nd display connection.
    XCloseDisplay(dpy);
}

void GLModule::killFont()
{
	glDeleteLists(base, 96); // delete all 96 characters.
}

void GLModule::glPrint(char *text) // custom gl print routine.
{
    if (text == NULL) {                         // if there's no text, do nothing.
		return;
    }
	
    glPushAttrib(GL_LIST_BIT);                  // alert that we're about to offset the display lists with glListBase
    glListBase(base - 32);                      // sets the base character to 32.
	
    glCallLists(strlen(text), GL_UNSIGNED_BYTE, text); // draws the display list text.
	
	glPopAttrib();                              // undoes the glPushAttrib(GL_LIST_BIT);
}

void GLModule::glPrintf(const char *fmt, ...)
{
	char		text[256]; //Holds Our String
	va_list		ap;	//Pointer To List Of Arguments
	if (fmt == NULL) // If There's No Text
		return;	// Do Nothing

	va_start(ap, fmt); // Parses The String For Variables
	vsprintf(text, fmt, ap); // And Converts Symbols To Actual Numbers
	va_end(ap);
	
	glPrint(text);
}

void GLModule::setColorFromValue(float val, float min, float max)
{
	float nval = 2.-2.*(val-min)/(max-min);
	float r, g, b;
	if(nval<1.){
		r = 1.-nval;
		g = nval;
		b = 0.;
	}else{
		r = 0.;
		g = 2.-nval;
		b = nval - 1.;
	}
	
	glColor3f(r, g, b);
}

void GLModule::updateEvents()
{
	key = -1;
	button_pressed = false;
	button_released = false;
	XEvent e;
	XWindowAttributes attr;
	while ( XCheckMaskEvent( dpy, 0xFFFFFFFF,&e ) )
	{
		switch (e.type)
		{
		case KeyPress:
			key = XKeycodeToKeysym( dpy, e.xkey.keycode, 0);
			key &= 0xFF;
			break;
		case ButtonPress:
			button_pressed = true;
			break;				
		case ButtonRelease:
			button_released = true;
			break;
		case MotionNotify:
			XGetWindowAttributes(dpy, win, &attr);	
			mouse_x = e.xbutton.x;
			mouse_y = attr.height-e.xbutton.y;
			break;
		default:
			break;
		}	
	}
}

void GLModule::mainThread()
{
	initGL();

	cout << "OpenGL Context OK" << endl;
	
	ready = true;
	for(;;)
	{
		boost::unique_lock<boost::mutex> lock(mut);
		cond.timed_wait(lock, milliseconds(100));
		
		if(!task_queue.empty())
		{
			Task &task = task_queue.front();

			// If you don't understand RTC++FM :)
			((task.obj)->*(task.f))(task.data);
			
			task.cond->notify_one();
			
			mut2.lock();
			task_queue.pop();
			mut2.unlock();
		}
	}
}


void GLModule::exec( GLModule* obj, void (GLModule::*f)(void *), void* data )
{
	if(ready)
	{
		boost::condition_variable tmp_cond;
		Task task = {obj, f, data, &tmp_cond};
		mut2.lock();
		task_queue.push(task);
		mut2.unlock();
		
		cond.notify_one();
		
		boost::mutex mut3;
		boost::unique_lock<boost::mutex> lock(mut3);

		tmp_cond.wait(lock);
	}
}
