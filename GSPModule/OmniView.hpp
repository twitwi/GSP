#ifndef __OMNIVIEW_HPP
#define __OMNIVIEW_HPP

#include <X11/Xlib.h>
#include <GL/glew.h>
#include <GL/glx.h>
#include <vector>
#include <opencv/cxcore.h>

class OmniView
{
private:
	static Display* dpy;
	static Window win;
	static GLXContext ctx;
	static bool t;
	static const int size_uv_grid_u;
	static const int size_uv_grid_v;	

	GLuint pantiltTexture;
	GLuint uvVertexBuffer;
	GLuint uvTexCoordBuffer;
	GLuint uvTexture;
	
	GLuint cubeMapTex, fbo_map;
	GLuint pantiltfbo;
	GLuint uvfbo;
	GLUquadric* quadric;
	
	IplImage* pantiltImage;
	IplImage* uvImage;
	
	double pan, tilt, zoom;
	
	void initUVBuffers();

	bool PanTiltEnabled;
	bool UVEnabled;
	
protected:
	void outputGlError( const char* pszLabel );
	// initialize the OpenGL context
	// swap buffers
	void swap();

	void renderCubeMap();
	void drawPanTilt();
	void drawUV();
	
	virtual void renderScene() = 0;
	
public:
	OmniView();
	~OmniView();

	void draw();
	
	void init();
	void initGL();

	inline void EnablePanTilt(bool b) {PanTiltEnabled = b; }
	inline void EnableUV(bool b) { UVEnabled = b; }
	
	inline IplImage * getPanTiltImage(){
		return pantiltImage;
	}
	
	inline IplImage * getUVImage(){
		return uvImage;
	}
	
	inline void setPanTiltZoom(double p, double t, double z){
		pan = p;
		tilt = t;
		zoom = z;
	}
};

#endif // __OMNIVIEW_HPP
