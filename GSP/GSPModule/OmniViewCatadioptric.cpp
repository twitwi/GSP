#include "OmniViewCatadioptric.hpp"

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <opencv/highgui.h>
#include <cmath>
#include <libxml/parser.h>
#include <libxml/tree.h>

using namespace std;

const int OmniViewCatadioptric::nbCircles = 32;
const int OmniViewCatadioptric::nbRays = 128;


OmniViewCatadioptric::OmniViewCatadioptric()
	: OmniView()
{
	texture = 0;
	vertexBuffer = 0;
	texCoordBuffer = 0;
	
	coeff[0] = 0;//-102.2078348948984;
	coeff[1] = 0;
	coeff[2] = 0;//0.0029988766680;
	coeff[3] = 0;//-0.0000021339258;
	coeff[4] = 0;//0.0000000063864;
	
	xc = 0;//323.2162832674422;
	yc = 0;//251.0943246846372;
}

OmniViewCatadioptric::~OmniViewCatadioptric()
{
	if(texture){
		glDeleteTextures(1, &texture);
	}
	
	if(vertexBuffer){
		glDeleteBuffers(1, &vertexBuffer);
	}
	if(texCoordBuffer){
		glDeleteBuffers(1, &texCoordBuffer);
	}
}


void OmniViewCatadioptric::init(const char *filename)
{
	OmniView::init();
	
	xmlDocPtr doc = xmlParseFile(filename);
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

// 	xmlAttrPtr attr_xc = xmlmsg->FindAttribute("xc",node);
// 	xmlAttrPtr attr_yc = xmlmsg->FindAttribute("yc",node);
// 	xmlAttrPtr attr_c0 = xmlmsg->FindAttribute("c0",node);
// 	xmlAttrPtr attr_c1 = xmlmsg->FindAttribute("c1",node);
// 	xmlAttrPtr attr_c2 = xmlmsg->FindAttribute("c2",node);
// 	xmlAttrPtr attr_c3 = xmlmsg->FindAttribute("c3",node);
// 	xmlAttrPtr attr_c4 = xmlmsg->FindAttribute("c4",node);
	
	width = atof((const char*) attr_width);
	height = atof((const char*) attr_height);
	xc = atof((const char*) attr_xc);
	yc = atof((const char*) attr_yc);
	coeff[0] = atof((const char*) attr_c0);
	coeff[1] = atof((const char*) attr_c1);
	coeff[2] = atof((const char*) attr_c2);
	coeff[3] = atof((const char*) attr_c3);
	coeff[4] = atof((const char*) attr_c4);
		
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

			float n0 = sqrtf(x0*x0+y0*y0+z0*z0);
			float n1 = sqrtf(x1*x1+y1*y1+z1*z1);
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

void OmniViewCatadioptric::updateFrame( IplImage* image )
{
	width = image->width;
	height = image->height;
	
	if(!texture){
		glGenTextures(1, &texture);
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
					 image->width, image->height,
					 0, GL_BGR, GL_UNSIGNED_BYTE, image->imageData);
		
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
	}else{
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 
						image->width, image->height,
						GL_BGR, GL_UNSIGNED_BYTE, image->imageData);
	}
}

void OmniViewCatadioptric::renderScene()
{
	glEnable(GL_TEXTURE_2D);
		
	glColor4f(1., 1., 1., 1.);
	
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	glBindTexture( GL_TEXTURE_2D, texture);
	
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
	outputGlError("OmniViewLadybug::renderScene");
}
