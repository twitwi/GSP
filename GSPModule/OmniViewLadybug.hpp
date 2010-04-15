#ifndef __OMNIVIEW_LADYBUG_HPP
#define __OMNIVIEW_LADYBUG_HPP

#include "OmniView.hpp"

#include <X11/Xlib.h>
#include <GL/glew.h>
#include <GL/glx.h>
#include <vector>
#include <opencv/cxcore.h>

class OmniViewLadybug : public OmniView
{
private:
	
	GLuint vertexBuffers[6];
	GLuint texCoordBuffers[6];
	GLuint textures[6];
	
	IplImage* alphaMasks[6];
	IplImage* rgbaFrames[6];
	
	int gCols, gRows;
	
protected:
	virtual void renderScene();
	
public:
	OmniViewLadybug();
	~OmniViewLadybug();
	
	void init();
	
	// load mesh
	bool loadMesh( const char *filename );
	
	//load alpha masks
	bool loadAlphaMasks( const char *basename );
	
	void updateFrame( IplImage* image );
};

#endif // __OMNIVIEW_LADYBUG_HPP
