#include "CubeMapModule.hpp"

#include <iostream>
using namespace std;

const int cube_map_resolution = 512;

void CubeMapModule::init()
{
	GLModule_EXEC(CubeMapModule, initCode, 0);
}

void CubeMapModule::stop()
{
	GLModule_EXEC(CubeMapModule, cleanGL, 0);
}

void CubeMapModule::initCode( void* )
{
	// intialize cube map texture & fbo
	glGenTextures( 1, &cubeMapTex );
	glBindTexture( GL_TEXTURE_CUBE_MAP, cubeMapTex );
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP);
  	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	for(int i=0; i<6; i++)
	{
		glTexImage2D( GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					  0, GL_RGB8, cube_map_resolution, cube_map_resolution, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
	}
	glGenFramebuffersEXT( 1, &fbo );
	glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, 0 );
	
	printErrors( __FILE__, __LINE__ );
}

void CubeMapModule::cleanGL( void* )
{
	glDeleteTextures(1, &cubeMapTex);
	glDeleteFramebuffersEXT( 1, &fbo);
}

void CubeMapModule::pre_render()
{
}
void CubeMapModule::render()
{
}

void CubeMapModule::execCode( void* )
{
	pre_render();
	
	glDisable(GL_CULL_FACE);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_STENCIL_TEST);
	glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
	glEnable( GL_TEXTURE_2D );
	
	glViewport(0, 0, cube_map_resolution, cube_map_resolution);
	
	glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, fbo );
	
	glColor4f(1., 1., 1., 1.);
	
	for(int k=0; k<6; k++)
	{
		glFramebufferTexture2DEXT( GL_FRAMEBUFFER_EXT,
								   GL_COLOR_ATTACHMENT0_EXT,
								   GL_TEXTURE_CUBE_MAP_POSITIVE_X + k,
								   cubeMapTex,
								   0);
// 		glFramebufferRenderbufferEXT( GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT,
// 									  GL_RENDERBUFFER_EXT, depthBuffer_map );
		
		glClear( GL_COLOR_BUFFER_BIT );
		
		glDrawBuffer(GL_COLOR_ATTACHMENT0_EXT);
		
		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();
		gluPerspective(90., 1., 0.1, 100.);

		glMatrixMode( GL_MODELVIEW );
		glLoadIdentity();
		
		switch(k){
		case 0:
			gluLookAt(0,0,0,    1,0,0,        0,-1,0);
			//glRotatef(90., 0., 1., 0.);
			break;
		case 1:
			gluLookAt(0,0,0,    -1,0,0,        0,-1,0);
			//glRotatef(-90., 0, 1., 0.);
 			break;
 		case 2:
			gluLookAt(0,0,0,    0,1,0,        0,0,1);
			//glRotatef(-90., 1., 0., 0.);
			//glRotatef(180., 0., 1., 0.);
			break;
		case 3:
			gluLookAt(0,0,0,    0,-1,0,        0,0,-1);
// 			glRotatef(90., 1., 0., 0.);
// 			glRotatef(180., 0., 1., 0.);
			break;
		case 4:
			gluLookAt(0,0,0,    0,0,1,        0,-1,0);
// 			glRotatef(180., 0., 1., 0.);
			break;
		case 5:
			gluLookAt(0,0,0,    0,0,-1,        0,-1,0);
			break;
		default:
			break;
		}
		
		render();
		
	}
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
}
