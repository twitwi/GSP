#include "OmniView.hpp"

#include <iostream>
#include <sstream>
#include <opencv/highgui.h>
#include <cmath>
#include <cstdio>

using namespace std;
bool OmniView::t = true;
Display* OmniView::dpy;
Window OmniView::win;
GLXContext OmniView::ctx;

const int OmniView::size_uv_grid_u = 64;
const int OmniView::size_uv_grid_v = 32;

OmniView::OmniView()
	: PanTiltEnabled( false )
	, UVEnabled( true )
{
	pan = 0.;
	tilt = 0.;
	zoom = 1.;
}

OmniView::~OmniView()
{
	gluDeleteQuadric(quadric);
}

void OmniView::initGL()
{
	if(!t) return;
	
	t=false;
	
	dpy = XOpenDisplay( NULL );
	if ( !dpy )
	{
 		fprintf( stderr, "Error: XOpenDisplay failed\n" );
 		abort();
	}
	// Choose GLX visual / pixel format
 	int visAttributes[] = {
 		GLX_RGBA,
 		GLX_DOUBLEBUFFER,
		GLX_DEPTH_SIZE, 16,
		GLX_RED_SIZE, 8,
		GLX_GREEN_SIZE, 8,
		GLX_BLUE_SIZE, 8,
		None
 	};
	
	XVisualInfo *visinfo = glXChooseVisual( dpy, DefaultScreen(dpy), visAttributes );
	if ( !visinfo )
	{
 		fprintf( stderr, "Error: couldn't get an RGB, Double-buffered "
				 "visual\n" );
 		abort();
	}
	
// 	// Create the window
 	Window root = RootWindow( dpy, 0 );
	
	XSetWindowAttributes attr;
	attr.background_pixel = 0;
	attr.border_pixel = 0;
	attr.colormap = XCreateColormap( dpy, root, visinfo->visual, AllocNone );
	
	unsigned long attrMask;
	attrMask = CWBackPixel | CWBorderPixel | CWColormap;
	
 	win = XCreateWindow( dpy, root,
 						 0, 0,
 						 512, 384,
  						 0, visinfo->depth, InputOutput,
  						 visinfo->visual, attrMask, &attr );
 	if ( !win )
 	{
  		cerr << "Error: XCreateWindow failed" << endl;
  		abort();
 	}
	
 	// Set standard and WM properties
	
// // 	XSetCommand( dpy, win, argv, argc );
// 	XStoreName( dpy, win, "GL Window" );
// 	XSetIconName( dpy, win, "GL Window" );


// // Ask for event notify
// 	long event_mask =
// 		StructureNotifyMask
// 		| KeymapStateMask
// 		| KeyPressMask
// 		| KeyReleaseMask
// 		| ButtonPressMask
// 		| ButtonReleaseMask
// 		| ExposureMask
// 		| PointerMotionMask
// 		| Button1MotionMask
// 		| Button2MotionMask
// 		| Button3MotionMask
// 		| Button4MotionMask
// 		| Button5MotionMask
// 		| ButtonMotionMask
// 		| FocusChangeMask ;
//     XSelectInput( dpy, win, event_mask );
	
// 	XMapWindow( dpy, win );
	
	// Create GLX rendering context
	ctx = glXCreateContext( dpy, visinfo, NULL, True );
	if ( !ctx )
	{
		fprintf( stderr, "Error: glXCreateContext failed\n" );
 		abort();
	}
	
	// Bind the rendering context and window
	glXMakeCurrent( dpy, win, ctx );
		
	// Init GLEW
	GLenum err = glewInit();
	
	if (GLEW_OK != err)
    {
		std::cout << "Error:" << glewGetErrorString(err) << std::endl;
		return;
    }
	cout << "OpenGL Vendor: " << (char*) glGetString(GL_VENDOR) << "\n";
	cout << "OpenGL Renderer: " << (char*) glGetString(GL_RENDERER) << "\n";
	cout << "OpenGL Version: " << (char*) glGetString(GL_VERSION) << "\n\n";
	
	glClearColor(0,0,0,1);
	
	glClear(GL_COLOR_BUFFER_BIT);
	glFlush();
	
	glPushAttrib(GL_ALL_ATTRIB_BITS);
}

void OmniView::swap()
{
	glXSwapBuffers(dpy,win);
 	glClearColor(0., 0., 0., 1.);
 	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

void OmniView::outputGlError( const char* pszLabel )
{
   GLenum errorno = glGetError();
   
   if ( errorno != GL_NO_ERROR )
   {
      printf( 
         "%s had error: #(%d) %s\r\n", 
         pszLabel, 
         errorno, 
         gluErrorString( errorno ) );
   }
}

void OmniView::init()
{
	pantiltImage = cvCreateImage(cvSize(512, 384),
								 IPL_DEPTH_8U, 3);
	
//	uvImage = cvCreateImage(cvSize(2048, 1152),
// 							IPL_DEPTH_8U, 3);
	uvImage = cvCreateImage(cvSize(1024, 576),
							IPL_DEPTH_8U, 3);
	
	initGL();
	
	glGenTextures(1, &pantiltTexture);
	glBindTexture(GL_TEXTURE_2D, pantiltTexture);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
				 pantiltImage->width, pantiltImage->height,
				 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
  	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
  	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
	glBindTexture(GL_TEXTURE_2D, 0);
	
	glGenFramebuffersEXT(1, &pantiltfbo);
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, pantiltfbo);
	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,
							  GL_COLOR_ATTACHMENT0_EXT,
							  GL_TEXTURE_2D,
							  pantiltTexture,
							  0);
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	
	glDisable(GL_CULL_FACE);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_STENCIL_TEST);
	glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
	
	glEnable( GL_TEXTURE_2D );
	
	int w=pantiltImage->width, h=pantiltImage->height;

	// intialize cube map texture & fbo
	glGenTextures( 1, &cubeMapTex );
	glBindTexture( GL_TEXTURE_CUBE_MAP, cubeMapTex );
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  	glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
  	glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
	
	for(int i=0; i<6; i++)
	{
		glTexImage2D( GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					  0, GL_RGB8, 1024, 1024, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
	}
	glGenFramebuffersEXT( 1, &fbo_map );	
	glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, 0 );
	

	glGenTextures(1, &uvTexture);
	glBindTexture(GL_TEXTURE_2D, uvTexture);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
				 uvImage->width, uvImage->height,
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

	quadric = gluNewQuadric();
	gluQuadricTexture(quadric, GL_FALSE);

	glMatrixMode( GL_TEXTURE );
	glLoadIdentity();

	outputGlError("init");
}

void OmniView::initUVBuffers()
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
		sincos((j+1)* M_PI_2 / 16., &z1, &c1);
		double x, y;
		for(int i=-size_uv_grid_u/2; i<=size_uv_grid_u/2; i++)
		{
			sincos(i*M_PI/16., &y, &x);
			bufV[2*idx] = i/16.;
			bufV[2*idx+1] = j/16.;
			bufT[3*idx] = c2*x;
			bufT[3*idx+1] = c2*y;
			bufT[3*idx+2] = z2;
			idx++;
			
			bufV[2*idx] = i/16.;
			bufV[2*idx+1] = (j+1)/16.;
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

void OmniView::renderCubeMap()
{
	glViewport(0, 0, 1024, 1024);
	
	glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, fbo_map );
	
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
		
		renderScene();
		
	}
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	outputGlError("renderCubeMap");
}

void OmniView::drawPanTilt()
{
	int w=pantiltImage->width, h=pantiltImage->height;
	
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, pantiltfbo);
	glDrawBuffer(GL_COLOR_ATTACHMENT0_EXT);
	glViewport(0, 0, w, h);
	glClearColor(0., 0., 0., 1.);
	
 	glClear(GL_COLOR_BUFFER_BIT);
	
	glMatrixMode( GL_PROJECTION );
	glLoadIdentity();
	gluPerspective( 90.0/zoom, (GLfloat)w/(GLfloat)h, 0.1, 100.0 );
	glScalef(1., -1., 1.);
	
	gluLookAt(0., 0., 0.,   1., 0., 0.,   0., 0., 1.);
	glRotatef( tilt, 0.0, 1.0, 0.0); // tilt the view	
	glRotatef( -pan, 0.0, 0.0, 1.0); // pan the view
	
	glMatrixMode( GL_MODELVIEW );
	glLoadIdentity();
	
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_TEXTURE_2D);
	
	glEnable(GL_TEXTURE_CUBE_MAP);
	glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapTex);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

	glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_NORMAL_MAP_EXT);
	glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_NORMAL_MAP_EXT);
	glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_NORMAL_MAP_EXT);
	
	glEnable(GL_TEXTURE_GEN_S);
	glEnable(GL_TEXTURE_GEN_T);
	glEnable(GL_TEXTURE_GEN_R);
	
	glColor3f(1., 1., 1.);
	gluSphere(quadric, 1., 16, 16);
	
 	glDisable(GL_TEXTURE_CUBE_MAP);
	glDisable(GL_TEXTURE_GEN_S);
	glDisable(GL_TEXTURE_GEN_T);
	glDisable(GL_TEXTURE_GEN_R);
	
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

	outputGlError("drawPanTilt");
	
	glBindTexture( GL_TEXTURE_2D, pantiltTexture);
	glGetTexImage( GL_TEXTURE_2D, 0, GL_BGR, GL_UNSIGNED_BYTE, pantiltImage->imageData);	
}

void OmniView::drawUV()
{
	int w=uvImage->width, h=uvImage->height;
	
	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, uvfbo);
	glDrawBuffer(GL_COLOR_ATTACHMENT0_EXT);
	glViewport(0, 0, w, h);
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
	glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapTex);
	
	glColor3f(1., 1., 1.);
// 	gluSphere(quadric, 1., 16, 16);
	
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
	
	outputGlError("drawUV");
	
	glBindTexture( GL_TEXTURE_2D, uvTexture);
	glGetTexImage( GL_TEXTURE_2D, 0, GL_BGR, GL_UNSIGNED_BYTE, uvImage->imageData);	
	
}

void OmniView::draw()
{
	renderCubeMap();
	if(PanTiltEnabled)
		drawPanTilt();
	if(UVEnabled)
		drawUV();
	glFinish();
}
