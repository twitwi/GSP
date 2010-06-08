#include "CatadioptricCubeMap.hpp"

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <opencv/highgui.h>
#include <cmath>
#include <libxml/parser.h>
#include <libxml/tree.h>

using namespace std;

CatadioptricCubeMap::CatadioptricCubeMap()
  : nbCircles(16)
  , nbRays(32)
  , texture(0)
{
}

void CatadioptricCubeMap::initModule()
{
  CubeMapModule::initModule();
  
  
  xmlDocPtr doc = xmlParseFile(param_file.c_str());
  xmlNodePtr node = doc->children;
  xmlChar *attr_width = xmlGetProp(node, (const xmlChar *) "width");
  xmlChar *attr_height = xmlGetProp(node, (const xmlChar *) "height");
  xmlChar *attr_xc = xmlGetProp(node, (const xmlChar *) "xc");
  xmlChar *attr_yc = xmlGetProp(node, (const xmlChar *) "yc");
  xmlChar *attr_c0 = xmlGetProp(node, (const xmlChar *) "c0");
  xmlChar *attr_c1 = xmlGetProp(node, (const xmlChar *) "c1");
  xmlChar *attr_c2 = xmlGetProp(node, (const xmlChar *) "c2");
  xmlChar *attr_c3 = xmlGetProp(node, (const xmlChar *) "c3");
  xmlChar *attr_c4 = xmlGetProp(node, (const xmlChar *) "c4");
  
  width = atof((const char*) attr_width);
  height = atof((const char*) attr_height);
  xc = atof((const char*) attr_xc);
  yc = atof((const char*) attr_yc);
  coeff[0] = atof((const char*) attr_c0);
  coeff[1] = atof((const char*) attr_c1);
  coeff[2] = atof((const char*) attr_c2);
  coeff[3] = atof((const char*) attr_c3);
  coeff[4] = atof((const char*) attr_c4);
  
  frame = cvCreateImageHeader(cvSize(width,height), IPL_DEPTH_8U, 3);
  
  GLModule_EXEC(CatadioptricCubeMap, init, 0);
}

void CatadioptricCubeMap::stopModule()
{
  CubeMapModule::stopModule();
  GLModule_EXEC(CatadioptricCubeMap, cleanGL, 0);
}

void CatadioptricCubeMap::init( void * )
{
  
  glGenBuffers(1, &vertexBuffer);
  glGenBuffers(1, &texCoordBuffer);
  
  float bufV[ nbRays * nbCircles * 2 * 3];
  float bufT[ nbRays * nbCircles * 2 * 2];
  
  float raumax = max(xc, max(width-xc, max(yc, height-yc)));
	
  for(int i = 0; i < nbCircles - 1; i++ )
  {
    float rau0 = (raumax * i) / nbCircles;
    float rau1 = (raumax * (i+1)) / nbCircles;
    //cout << "rau0 : " << rau0 << endl;
		
    float z0 = coeff[0]
      + coeff[1] * rau0
      + coeff[2] * rau0 * rau0
      + coeff[3] * rau0 * rau0 * rau0
      + coeff[4] * rau0 * rau0 * rau0 * rau0;
    float z1 = coeff[0]
      + coeff[1] * rau1
      + coeff[2] * rau1 * rau1
      + coeff[3] * rau1 * rau1 * rau1
      + coeff[4] * rau1 * rau1 * rau1 * rau1;
		
    for(int j=0; j<nbRays; j++){
      float theta = j*2*M_PI/(nbRays-1);
      //cout << "theta : "<< theta << endl;
			
      float x, y;
      sincosf(theta, &y, &x);
      float x0, y0, x1, y1;
      x0 = x * rau0;
      y0 = y * rau0;
      x1 = x * rau1;
      y1 = y * rau1;

      float n0 = sqrtf(x0*x0+y0*y0+z0*z0) / 2.;
      float n1 = sqrtf(x1*x1+y1*y1+z1*z1) / 2.;
      bufV[(i*nbRays+j)*6+0] = x0 / n0;
      bufV[(i*nbRays+j)*6+1] = y0 / n0;
      bufV[(i*nbRays+j)*6+2] = z0 / n0;
      bufV[(i*nbRays+j)*6+3] = x1 / n1;
      bufV[(i*nbRays+j)*6+4] = y1 / n1;
      bufV[(i*nbRays+j)*6+5] = z1 / n1;
			
      bufT[(i*nbRays+j)*4+0] = (x0+xc)/width;
      bufT[(i*nbRays+j)*4+1] = (y0+yc)/height;
      bufT[(i*nbRays+j)*4+2] = (x1+xc)/width;
      bufT[(i*nbRays+j)*4+3] = (y1+yc)/height;
			
    }
  }
  glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
  glBufferData(GL_ARRAY_BUFFER, nbCircles * nbRays * 2 * 3 * sizeof(float), (const GLvoid *) bufV, GL_STATIC_DRAW);
  glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffer);
  glBufferData(GL_ARRAY_BUFFER, nbCircles * nbRays * 2 * 2 * sizeof(float), (const GLvoid *) bufT, GL_STATIC_DRAW);

}

void CatadioptricCubeMap::cleanGL(void *)
{
  glDeleteTextures(1, &texture);
  glDeleteBuffers(1, &vertexBuffer);
  glDeleteBuffers(1, &texCoordBuffer);
}

void CatadioptricCubeMap::setParameters(char *filename)
{
  param_file = filename;
}

void CatadioptricCubeMap::inputRaw( void *data, int w, int h, int widthStep, int type)
{
  frame->nChannels = type/8;
  frame->width = w;
  frame->height = h;
  frame->widthStep = widthStep;
  frame->imageSize = h * widthStep;
  frame->imageData = (char*) data;
  GLModule_EXEC(CubeMapModule, execCode, 0);
	
  emitNamedEvent("output", cubeMapTex );
}

void CatadioptricCubeMap::input( IplImage *img )
{
  IplImage * tmp = frame;
  frame = img;

//   frame = cvCloneImage(img);
  
  GLModule_EXEC(CubeMapModule, execCode, 0);
  
  emitNamedEvent("output", cubeMapTex );

//  cvReleaseImage(&frame);

  frame = tmp;
}

void CatadioptricCubeMap::pre_render()
{
  width = frame->width;
  height = frame->height;
	
  if(!texture){
    glGenTextures(1, &texture);
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                 frame->width, frame->height,
                 0, GL_BGR, GL_UNSIGNED_BYTE, frame->imageData);
		
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
  }else{
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 
                    frame->width, frame->height,
                    GL_BGR, GL_UNSIGNED_BYTE, frame->imageData);
  }

  glDisable( GL_CULL_FACE );
  glDisable( GL_DEPTH_TEST );
  glDisable( GL_STENCIL_TEST );
  glDisable( GL_BLEND );
  glDisable( GL_ALPHA_TEST );
  glDisable( GL_DITHER );
  glDisable( GL_TEXTURE_GEN_S );
  glDisable( GL_TEXTURE_GEN_R );
  glDisable( GL_TEXTURE_GEN_T );
  
  printErrors( __FILE__, __LINE__ );
}


void CatadioptricCubeMap::render()
{
  glEnable(GL_TEXTURE_2D);
  glBindTexture( GL_TEXTURE_2D, texture);

  glMatrixMode( GL_TEXTURE );
  glLoadIdentity();
  
  glColor4f(1., 1., 1., 1.);

//   glBindTexture( GL_TEXTURE_2D, texture);
  
//   glBegin(GL_QUADS);

//   glTexCoord2f(0., 0.);
//   glVertex3f(-1., -1., 1.);
//   glTexCoord2f(1., 0.);
//   glVertex3f(1., -1., 1.);
//   glTexCoord2f(1., 1.);
//   glVertex3f(1., 1., 1.);
//   glTexCoord2f(0., 1.);
//   glVertex3f(-1., 1., 1.);
  
//   glTexCoord2f(0., 0.);
//   glVertex3f(-1., -1., -1.);
//   glTexCoord2f(1., 0.);
//   glVertex3f(1., -1., -1.);
//   glTexCoord2f(1., 1.);
//   glVertex3f(1., 1., -1.);
//   glTexCoord2f(0., 1.);
//   glVertex3f(-1., 1., -1.);

//   glTexCoord2f(0., 0.);
//   glVertex3f(1, -1., -1.);
//   glTexCoord2f(1., 0.);
//   glVertex3f(1., 1., -1.);
//   glTexCoord2f(1., 1.);
//   glVertex3f(1., 1., 1.);
//   glTexCoord2f(0., 1.);
//   glVertex3f(1., -1., 1.);

//   glEnd();
  
  glEnableClientState(GL_VERTEX_ARRAY);
  glEnableClientState(GL_TEXTURE_COORD_ARRAY);
  
  for ( int i = 0; i < nbCircles - 1; i++ ) // for each circle
  {
    glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffer);
    glTexCoordPointer(2, GL_FLOAT, 0, 0);
		
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glVertexPointer(3, GL_FLOAT, 0, 0);
		
    glDrawArrays(GL_QUAD_STRIP, i*nbRays*2, nbRays*2);
  }
	
  glDisableClientState(GL_VERTEX_ARRAY);
  glDisableClientState(GL_TEXTURE_COORD_ARRAY);	
  
  glDisable(GL_TEXTURE_2D);

  printErrors( __FILE__, __LINE__ );
}

void CatadioptricCubeMap::drawImage( void* )
{
  glEnable(GL_TEXTURE_2D);
  glBindTexture( GL_TEXTURE_2D, texture);
  
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
