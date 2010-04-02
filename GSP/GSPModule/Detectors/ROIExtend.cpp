#include "ROIExtend.hpp"
#include <iostream>

/**
* Constructor en specifiant les points de la ROI
* @param x1,y1 : coord du 1er point
* @param x2,y2 : coord du 2ieme point
* @param x3,y3 : coord du 3ieme point
* @param x4,y4 : coord du 4ieme point
*/
ROIExtend::ROIExtend(int x1, int y1, int x2, int y2,int x3, int y3, int x4, int y4) {
  roi_[0][0] = x1;
  roi_[0][1] = y1;
  roi_[1][0] = x2;
  roi_[1][1] = y2;
  roi_[2][0] = x3;
  roi_[2][1] = y3;
  roi_[3][0] = x4;
  roi_[3][1] = y4;
}

/**
 * Copy Constructor
 */
ROIExtend::ROIExtend( const ROIExtend& roi)
{
  roi_[0][0] = roi.roi_[0][0];
  roi_[0][1] = roi.roi_[0][1];
  roi_[1][0] = roi.roi_[1][0];
  roi_[1][1] = roi.roi_[1][1];
  roi_[2][0] = roi.roi_[2][0];
  roi_[2][1] = roi.roi_[2][1];
  roi_[3][0] = roi.roi_[3][0];
  roi_[3][1] = roi.roi_[3][1];
}

/// Destructor
ROIExtend::~ROIExtend() {
}

/**
* Fonction pour mettre à jour la ROI en fonction d'une ROI passée en paramètre
* @param roi : ROI source
*/
void
ROIExtend::setROIExtend(ROIExtend& roi) {
  roi_[0][0] = roi.getCoord(0,0);
  roi_[0][1] = roi.getCoord(0,1);
  roi_[1][0] = roi.getCoord(1,0);
  roi_[1][1] = roi.getCoord(1,1);
  roi_[2][0] = roi.getCoord(2,0);
  roi_[2][1] = roi.getCoord(2,1);
  roi_[3][0] = roi.getCoord(3,0);
  roi_[3][1] = roi.getCoord(3,1);
}

/**
* Fonction retournant la valeur du point(x,y)
* @param x : numero du point (1,2,3,4)
* @param y : valeur abscisse ou ordonnée (0,1)
*/
int
ROIExtend::getCoord(int x, int y) {
  return roi_[x][y];
}

/**
* Fonction retournant la valeur la plus haute en ordonnée
*/
int
ROIExtend::getTop() {
  int max = 1000000;
  for (int i=0;i<4;i++) {
    if (max > roi_[i][1])
      max = roi_[i][1];
  }
  return max;
}

/**
* Fonction retournant la valeur la plus basse en ordonnée
*/
int
ROIExtend::getBottom() {
  int max = 0;
  for (int i=0;i<4;i++) {
    if (max < roi_[i][1])
      max = roi_[i][1];
  }
  return max;
}

/**
* Fonction retournant la valeur la plus a gauche
*/
int
ROIExtend::getLeft() {
  int max = 100000;
  for (int i=0;i<4;i++) {
    if (max > roi_[i][0])
      max = roi_[i][0];
  }
  return max;
}

/**
* Fonction retournant la valeur la plus a droite
*/
int
ROIExtend::getRight() {
  int max = 0;
  for (int i=0;i<4;i++) {
    if (max < roi_[i][0])
      max = roi_[i][0];
  }
  return max;
}

/**
* Fonction retournant un boolean  indiquant si le point (x,y) se trouve dans la ROI
* @param x : coordonnée en abscisse
* @param y : coordonnée en ordonnée
*/
bool
ROIExtend::isInside(int x, int y) {
  bool ret = false;
  int i = 0;
  long res1 = (roi_[1][0] - roi_[0][0])*(y - roi_[0][1]) - (roi_[1][1] - roi_[0][1])*(x - roi_[0][0]);
  long res2 = (roi_[3][0] - roi_[1][0])*(y - roi_[1][1]) - (roi_[3][1] - roi_[1][1])*(x - roi_[1][0]);
  long res3 = (roi_[2][0] - roi_[3][0])*(y - roi_[3][1]) - (roi_[2][1] - roi_[3][1])*(x - roi_[3][0]);
  long res4 = (roi_[0][0] - roi_[2][0])*(y - roi_[2][1]) - (roi_[0][1] - roi_[2][1])*(x - roi_[2][0]);

  if ((res1 >= 0 && res2 >= 0 && res3 >= 0 && res4 >= 0)
      ||(res1 < 0 && res2 < 0 && res3 < 0 && res4 < 0))
    return true;
  
  return ret;
}

/**
* Fonction retournant la coordonnée en abscisse du centre de la ROI
*/
int
ROIExtend::getCenterX() {
  return ((roi_[0][0]+roi_[1][0]+roi_[2][0]+roi_[3][0])/4);
}

/**
* Fonction retournant la coordonnée en ordonnée du centre de la ROI
*/
int
ROIExtend::getCenterY() {
  return ((roi_[0][1]+roi_[1][1]+roi_[2][1]+roi_[3][1])/4);
}
