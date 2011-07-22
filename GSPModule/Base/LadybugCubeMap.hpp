#ifndef __LADYBUG_CUBE_MAP_HPP
#define __LADYBUG_CUBE_MAP_HPP

#include <framework.h>
#include <opencv/cxcore.h>
#include <CubeMapModule.hpp>

#include <string>

class LadybugCubeMap : public CubeMapModule
{
private:	
  GLuint vertexBuffers[6];
  GLuint texCoordBuffers[6];
  GLuint textures[6];
	
  IplImage* alphaMasks[6];
  IplImage* rgbaFrames[6];
  IplImage* frame;
	
  int gCols, gRows;

  std::string meshfile;
	
  void initMesh( void* );
  void cleanGL( void* );
	
public:
  LadybugCubeMap();
	
  Framework _framework;

  void initModule();
  void stopModule();
	
  void inputRaw(void *data, int w, int h, int widthStep, int type);
  void input( IplImage *img );
  
  void setMesh(char * filename);
  void setAlphaMasks(char *basename);
	
protected:
  virtual void pre_render();
  virtual void render();
};

CLASS_AS_MODULE(LadybugCubeMap);

#endif // __LADYBUG_CUBE_MAP_HPP
