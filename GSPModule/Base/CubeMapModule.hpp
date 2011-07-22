#ifndef __CUBE_MAP_MODULE_HPP
#define __CUBE_MAP_MODULE_HPP

#include <GL/glew.h>
#include <GLModule.hpp>

class CubeMapModule : public GLModule
{
public:
  void init();
  void stop();
  void execCode( void* );

protected:
  unsigned int cubeMapTex, fbo;
  
  virtual void render();
  virtual void pre_render();
  
private:
  void initCode( void* );	
  void cleanGL( void* );
};

#endif // __CUBE_MAP_MODULE_HPP
