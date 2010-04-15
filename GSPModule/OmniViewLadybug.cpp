#include "OmniViewLadybug.hpp"

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <opencv/highgui.h>
#include <cmath>

using namespace std;

OmniViewLadybug::OmniViewLadybug()
{
	for(int i=0; i<6; i++)
	{
		textures[i] = 0;
	}
}

OmniViewLadybug::~OmniViewLadybug()
{
	if(textures[0]){
		glDeleteTextures(6, textures);
	}
	glDeleteBuffers(6, vertexBuffers);
	glDeleteBuffers(6, texCoordBuffers);
}

void OmniViewLadybug::init()
{
	OmniView::init();
}

void OmniViewLadybug::updateFrame( IplImage* image )
{
    int from_to[] = { 0, 2, 1, 1, 2, 0, 3, 3 };

	for(int i=0; i<6; i++){
//		cvSetImageROI(image,cvRect(0,i*image->height/6,image->width,image->height/6));
		CvMat mat;
		cvGetSubRect(image,&mat,cvRect(0,i*image->height/6,image->width,image->height/6));
		const CvArr* in[] = { &mat, alphaMasks[i] };
		cvMixChannels( in, 2, (CvArr**)&rgbaFrames[i], 1, from_to, 4 );
		
		if(textures[i] == 0){
			glGenTextures(1, &textures[i]);
			glBindTexture(GL_TEXTURE_2D, textures[i]);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
						 rgbaFrames[i]->width, rgbaFrames[i]->height,
						 0, GL_RGBA, GL_UNSIGNED_BYTE, rgbaFrames[i]->imageData);
			
			glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
			glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
			glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
		}else{
			glBindTexture(GL_TEXTURE_2D, textures[i]);
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 
							rgbaFrames[i]->width, rgbaFrames[i]->height,
							GL_RGBA, GL_UNSIGNED_BYTE, rgbaFrames[i]->imageData);
		}
//		cvResetImageROI(image);
	}

}

bool OmniViewLadybug::loadAlphaMasks( const char *basename )
{
	for(int c=0; c<6; c++){
		stringstream ss;
		ss << basename << c << ".pgm";
		alphaMasks[c] = cvLoadImage(ss.str().c_str(), CV_LOAD_IMAGE_GRAYSCALE);
		if(alphaMasks[c] == 0){
			return false;
		}
		rgbaFrames[c] = cvCreateImage(cvGetSize(alphaMasks[c]),
									  IPL_DEPTH_8U, 4);
	}
	return true;
}

bool OmniViewLadybug::loadMesh( const char *filename )
{
	
	FILE *fp = fopen( filename, "r");
	if ( fp == NULL){
		printf( "Can't read 3D mesh file: %s\n", filename);
		return false;
	}
	
	if ( fscanf( fp, "cols %d rows %d\n", &gCols, &gRows) != 2){
		printf( "Can't read cols/rows in 3d mesh file.\n");
		return false;
	}

	glGenBuffers(6, vertexBuffers);
	glGenBuffers(6, texCoordBuffers);

	float bufV[ gCols * gRows * 3];

	float bufT[ (gRows-1)*gCols*2*2 ];
	float bufV2[ (gRows-1)*gCols*2*3 ];
	
		
	for ( int c = 0; c < 6; c++)
	{		
		for ( int iRow = 0; iRow < gRows; iRow++ )
		{
			for ( int iCol = 0; iCol < gCols; iCol++ )
			{
				float x, y, z;
				if ( fscanf( fp, "%f, %f, %f", &x, &y, &z) != 3){
					printf( "Can't read grid data in 3d mesh file.\n");
					return false;
				}
				bufV[ ( iRow * gCols + iCol) * 3 + 0] = x;
				bufV[ ( iRow * gCols + iCol) * 3 + 1] = y;
				bufV[ ( iRow * gCols + iCol) * 3 + 2] = z;
				
// 				bufT[ (iRow * gCols + iCol) * 3 + 0] = (float) iCol / (float) (gCols-1);
// 				bufT[ (iRow * gCols + iCol) * 3 + 1] = (float) iRow / (float) (gRows-1);
			}
		}
		
		for ( int iRow = 0; iRow < gRows - 1; iRow++ ) // for each row
		{
			for ( int iCol = 0; iCol < gCols; iCol++ ) // for each column
			{
				float p1 = (float)iCol / ( gCols - 1.);
				float q1 = (float)iRow / ( gRows - 1.);
				float q2 = (float)( iRow + 1.0) / ( gRows - 1.);
				
				int ptr1 = iRow * gCols + iCol;
				float x1 = bufV[ ptr1 * 3 + 0];
				float y1 = bufV[ ptr1 * 3 + 1];
				float z1 = bufV[ ptr1 * 3 + 2];
				
				int ptr2 = ( iRow + 1) * gCols + iCol;
				float x2 = bufV[ ptr2 * 3 + 0];
				float y2 = bufV[ ptr2 * 3 + 1];
				float z2 = bufV[ ptr2 * 3 + 2];
				
				bufV2[ (iRow * gCols + iCol) * 6 + 0] = x1;
				bufV2[ (iRow * gCols + iCol) * 6 + 1] = y1;
				bufV2[ (iRow * gCols + iCol) * 6 + 2] = z1;
				bufV2[ (iRow * gCols + iCol) * 6 + 3] = x2;
				bufV2[ (iRow * gCols + iCol) * 6 + 4] = y2;
				bufV2[ (iRow * gCols + iCol) * 6 + 5] = z2;
				
				bufT[ (iRow * gCols + iCol) * 4 + 0] = p1;
				bufT[ (iRow * gCols + iCol) * 4 + 1] = q1;
				bufT[ (iRow * gCols + iCol) * 4 + 2] = p1;
				bufT[ (iRow * gCols + iCol) * 4 + 3] = q2;
			}
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffers[c]);
		glBufferData(GL_ARRAY_BUFFER, (gRows-1) * gCols * 2 * 3 * sizeof(float), (const GLvoid *) bufV2, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffers[c]);
		glBufferData(GL_ARRAY_BUFFER, (gRows-1) * gCols * 2 * 2 * sizeof(float), (const GLvoid *) bufT, GL_STATIC_DRAW);
	}
	
	fclose( fp);
	
	return true;
}

void OmniViewLadybug::renderScene()
{
	glEnable(GL_TEXTURE_2D);
	glEnable( GL_BLEND );
		
	glColor4f(1., 1., 1., 1.);
	
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	for ( int c = 0; c < 6; c++) // for each camera
	{
		for ( int iRow = 0; iRow < gRows - 1; iRow++ ) // for each row
		{
			glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffers[c]);
			glTexCoordPointer(2, GL_FLOAT, 0, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, vertexBuffers[c]);
			glVertexPointer(3, GL_FLOAT, 0, 0);
			
			glBindTexture( GL_TEXTURE_2D, textures[c]);
			glDrawArrays(GL_TRIANGLE_STRIP, iRow*gCols*2, gCols*2);
		}
	}	
	glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);	
	
	glDisable(GL_TEXTURE_2D);
	glDisable( GL_BLEND );
	outputGlError("OmniViewLadybug::renderScene");
}
