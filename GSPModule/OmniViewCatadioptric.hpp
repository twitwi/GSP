#ifndef __OMNIVIEW_CATADIOPTRIC_HPP
#define __OMNIVIEW_CATADIOPTRIC_HPP

#include "OmniView.hpp"

#include <X11/Xlib.h>
#include <GL/glew.h>
#include <GL/glx.h>
#include <vector>
#include <opencv/cxcore.h>

class OmniViewCatadioptric : public OmniView
{
private:
	
	GLuint vertexBuffer;
	GLuint texCoordBuffer;
	GLuint texture;
	
	float coeff[5];
	float xc, yc;
	
	float width, height;
	
	static const int nbCircles, nbRays;
	
protected:
	virtual void renderScene();
	
public:
	OmniViewCatadioptric();
	~OmniViewCatadioptric();
	
	void init(const char *filename);
	
	void updateFrame( IplImage* image );
};

#endif // __OMNIVIEW_CATADIOPTRIC_HPP
