#ifndef __GL_CONTEXT_HPP
#define __GL_CONTEXT_HPP

#include <X11/Xlib.h>
#include <GL/glew.h>
#include <GL/glx.h>
#include <string>

#include <queue>

#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>
#include <boost/thread.hpp>

class GLModule
{
private:
	static bool t;
	static Display* dpy;
	static Window win;
	static GLXContext ctx;
	static GLuint base; //base display list for the font set.
	static void buildFont();
	static void killFont();
	
	static int key;
	static bool button_pressed, button_released;
	static int mouse_x, mouse_y;

	static bool ready;
	static boost::thread gl_thread;
	static boost::condition_variable cond;
	static boost::mutex mut;
	static boost::mutex mut2;

	typedef struct {
		GLModule *obj;
		void (GLModule::*f)(void*);
		void * data;
		boost::condition_variable * cond;
	} Task;
	
	static std::queue< Task > task_queue;
	static void mainThread();

	// initialize the OpenGL context
	static void initGL();

protected:
	
	// swap buffers
	void swap();
	
	// print error
	void printErrors(const char *file, int line);
	
	// display text
	void glPrint(char *text);
	void glPrintf(const char *fmt, ...);
	
	// draw a quad
	void drawQuad();
	void drawQuad(int w, int h);
	
	void drawEllipse(float x0, float y0,
					 float ax, float ay,
					 float angle, int resolution = 64);
	/*
	 * draw a filled ellipse
	 * X0,y0: position of the center
	 * @ax,ay: 
	 * @angle (radians): angle of the pricipal axis
	 * @resolution: number of line segments used to draw the ellipse
	 */ 
	void drawFilledEllipse(float x0, float y0,
                           float ax, float ay,
                           float angle, int resolution = 64);
	
	// Set a RGB color from a float value
	void setColorFromValue(float val, float min=0., float max=1.);
	
	// update key and mouse events
	void updateEvents();
	
	// return the keycode if a key has been pressed
	// or -1 if not
	inline int getKey(){
		return key;
	}
	
	// return true and mouse position if a button has been pressed
	inline bool mousePressed(){
		return button_pressed;
	}
	inline bool mouseReleased(){
		return button_released;
	}
	
	inline void getMouse(int &x, int &y){
		x=mouse_x; y=mouse_y;
	}
	
	
	void exec(GLModule* obj, void (GLModule::*f)(void *), void* data=0 );

#define GLModule_EXEC(c, func, data) exec(static_cast<GLModule*>(this), static_cast<void (GLModule::*)(void*)>(&c::func), data)

	
public:
	GLModule();
	~GLModule();
};

#endif // __GL_CONTEXT_HPP
