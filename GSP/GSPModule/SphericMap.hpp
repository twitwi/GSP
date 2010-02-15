#ifndef __SPHERIC_MAP_HPP
#define __SPHERIC_MAP_HPP

#include <opencv/cxcore.h>
#include <GLModule.hpp>
#include <framework.h>

class SphericMap : public GLModule
{
protected:
	int width;
	int height;

	int size_uv_grid_u;
	int size_uv_grid_v;
	
	GLuint uvVertexBuffer;
	GLuint uvTexCoordBuffer;
	GLuint uvTexture;
	GLuint uvfbo;
	IplImage* uvImage;

	void initUVBuffers();
	void cleanGL( void* );
	
public:
	SphericMap();
	void initModule();
	void stopModule();
	
	void setWidth( int width );
	void setHeight( int height );
	
	void input( GLuint tex );

	void initUV( void* );
	void drawUV( void* texPtr);
	
	Framework _framework;
	
};
CLASS_AS_MODULE(SphericMap);

#endif //__SPHERIC_MAP_HPP
