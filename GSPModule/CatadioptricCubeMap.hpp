#ifndef __CATADIOPTRIC_CUBE_MAP_HPP
#define __CATADIOPTRIC_CUBE_MAP_HPP

#include <framework.h>
#include <opencv/cxcore.h>
#include <CubeMapModule.hpp>
#include <string>

class CatadioptricCubeMap : public CubeMapModule
{
private:
	GLuint vertexBuffer;
	GLuint texCoordBuffer;
	GLuint texture;
	
	IplImage *frame;
	
	float coeff[5];
	float xc, yc;
	
	float width, height;
	
	int nbCircles, nbRays;
	
	std::string param_file;

	void init( void* );
	void cleanGL( void* );

public:
	Framework _framework;
	CatadioptricCubeMap();
	
	void initModule();
	void stopModule();
	void setParameters(char *filename);
	void inputRaw(void *data, int w, int h, int widthStep, int type);
    void input(IplImage *img);
  
protected:
	virtual void pre_render();
	virtual void render();

  void drawImage( void *);
};
CLASS_AS_MODULE(CatadioptricCubeMap);

#endif // __CATADIOPTRIC_CUBE_MAP_HPP
