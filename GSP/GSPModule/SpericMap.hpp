#ifndef __SPHERIC_MAP_HPP
#define __SPHERIC_MAP_HPP

#include <GLModule.hpp>
#include <framework.h>

class SphericMap : public GLModule
{
private:
	int width;
	int height;
	
public:
	SphericMap();
	void initModule();
	void stopModule();
	
	void setWidth( int width );
	void setHeight( int height );
	
	Framework _framework;
	
};
CLASS_AS_MODULE(SphericMap);

#endif //__SPHERIC_MAP_HPP
