#include "SphericMap.hpp"
#include <iostream>

using namespace std;

SphericMap::SphericMap()
	: width(1024)
	, height(576)
	, size_uv_grid_u(64)
	, size_uv_grid_v(32)
{
}

void SphericMap::initModule()
{
	uvImage = cvCreateImage(cvSize(width, height),
							IPL_DEPTH_8U, 3);
	
	GLModule_EXEC( SphericMap, initUV, 0 );
}

void SphericMap::initUV( void* )
{
	glGenTextures(1, &uvTexture);
	glBindTexture(GL_TEXTURE_2D, uvTexture);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
				 width, height,
				 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
  	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
  	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
	glBindTexture(GL_TEXTURE_2D, 0);
	
	glGenFramebuffersEXT(1, &uvfbo);
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, uvfbo);
	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,
							  GL_COLOR_ATTACHMENT0_EXT,
							  GL_TEXTURE_2D,
							  uvTexture,
							  0);
	
	initUVBuffers();

}

void SphericMap::initUVBuffers()
{
	double bufV[size_uv_grid_v*(size_uv_grid_u+1)*2*2];
	double bufT[size_uv_grid_v*(size_uv_grid_u+1)*2*3];
	
	double z1=-1., z2, c1=0., c2;
	
	glColor3f(1., 1., 1.);
	int idx = 0;
	for(int j=-size_uv_grid_v/2; j<size_uv_grid_v/2; j++)
	{
		z2 = z1;
		c2 = c1;
		sincos((j+1)* M_PI / size_uv_grid_v, &z1, &c1);
		double x, y;
		for(int i=-size_uv_grid_u/2; i<=size_uv_grid_u/2; i++)
		{
			sincos(i*M_PI*2./size_uv_grid_v, &y, &x);
			bufV[2*idx] = i*2./size_uv_grid_v;
			bufV[2*idx+1] = j*2./size_uv_grid_v;
			bufT[3*idx] = c2*x;
			bufT[3*idx+1] = c2*y;
			bufT[3*idx+2] = z2;
			idx++;
			
			bufV[2*idx] = i*2./size_uv_grid_v;
			bufV[2*idx+1] = (j+1)*2./size_uv_grid_v;
			bufT[3*idx] = c1*x;
			bufT[3*idx+1] = c1*y;
			bufT[3*idx+2] = z1;
			idx++;
		}
	}
	
	glGenBuffers(1, &uvVertexBuffer);
	glGenBuffers(1, &uvTexCoordBuffer);
	
	glBindBuffer(GL_ARRAY_BUFFER, uvVertexBuffer);
	glBufferData(GL_ARRAY_BUFFER, size_uv_grid_v*(size_uv_grid_u+1)*2*2 * sizeof(double), (const GLvoid *) bufV, GL_STATIC_DRAW);
	glBindBuffer(GL_ARRAY_BUFFER, uvTexCoordBuffer);
	glBufferData(GL_ARRAY_BUFFER, size_uv_grid_v*(size_uv_grid_u+1)*2*3 * sizeof(double), (const GLvoid *) bufT, GL_STATIC_DRAW);
}

void SphericMap::stopModule()
{
	cvReleaseImage( &uvImage );
	
	GLModule_EXEC( SphericMap, cleanGL, 0 );
}

void SphericMap::cleanGL( void* )
{
	glDeleteTextures(1, &uvTexture);
	glDeleteBuffers(1, &uvVertexBuffer);
	glDeleteBuffers(1, &uvTexCoordBuffer);
	glDeleteFramebuffersEXT(1, &uvfbo);
}

void SphericMap::setWidth( int width )
{
	this->width = width;
}

void SphericMap::setHeight( int height )
{
	this->height = height;
}

void SphericMap::input( unsigned int tex )
{
	GLModule_EXEC( SphericMap, drawUV, &tex );

    unhideWindow();
    
    GLModule_EXEC(SphericMap, drawImage, 0);
    
	emitNamedEvent("output", uvImage );
}

void SphericMap::drawUV( void* texPtr)
{
	GLuint tex = *(static_cast<GLuint*>(texPtr));
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, uvfbo);
	glDrawBuffer(GL_COLOR_ATTACHMENT0_EXT);
	glViewport(0, 0, width, height);
	glClearColor(0., 0., 0., 1.);
	
 	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
	glMatrixMode( GL_PROJECTION );
	glLoadIdentity();
	glScalef(1., -1., 1.);
	glMatrixMode( GL_MODELVIEW );
	glLoadIdentity();
	
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_TEXTURE_2D);
	
	glEnable(GL_TEXTURE_CUBE_MAP);
	glBindTexture(GL_TEXTURE_CUBE_MAP, tex);
	
	glColor3f(1., 1., 1.);
	
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);

	glBindBuffer(GL_ARRAY_BUFFER, uvTexCoordBuffer);
	glTexCoordPointer(3, GL_DOUBLE, 0, 0);
	
	glBindBuffer(GL_ARRAY_BUFFER, uvVertexBuffer);
	glVertexPointer(2, GL_DOUBLE, 0, 0);
	
	for ( int j = 0; j < size_uv_grid_v; j++)
	{		
		glDrawArrays(GL_QUAD_STRIP, j*(size_uv_grid_u+1)*2, (size_uv_grid_u+1)*2);
	}
	
	glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);	
	
 	glDisable(GL_TEXTURE_CUBE_MAP);
	
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

	glBindTexture( GL_TEXTURE_2D, uvTexture);
	glGetTexImage( GL_TEXTURE_2D, 0, GL_BGR, GL_UNSIGNED_BYTE, uvImage->imageData);
}

void SphericMap::drawImage( void* )
{
  glEnable(GL_TEXTURE_2D);
  glBindTexture( GL_TEXTURE_2D, uvTexture);
  
  glColor4f(1., 1., 1., 1.);
  
  glViewport(0, 0, 640, 480);
  glMatrixMode( GL_PROJECTION );
  glLoadIdentity();
  glMatrixMode( GL_MODELVIEW );
  glLoadIdentity();

  glBegin(GL_QUADS);

  glTexCoord2f(0., 0.);
  glVertex2f(-1., -1.);
  glTexCoord2f(1., 0.);
  glVertex2f(1., -1.);
  glTexCoord2f(1., 1.);
  glVertex2f(1., 1.);
  glTexCoord2f(0., 1.);
  glVertex2f(-1., 1.);
  
  glEnd();
  
  glDisable(GL_TEXTURE_2D);

  swap();
}
