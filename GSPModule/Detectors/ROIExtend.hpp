#ifndef ROIEXTEND_HPP
#define ROIEXTEND_HPP

/**
 * Classe pour la gestion de ROI non rectangulaire (tout quadrilatere)
 * @author Matthieu Langet
 */
class ROIExtend {
private:
  /// les points determinants la ROI
  int roi_[4][2];

public:
  /// Constructor
  ROIExtend() {};
  /**
   * Constructor en specifiant les points de la ROI
   * @param x1,y1 : coord du 1er point
   * @param x2,y2 : coord du 2ieme point
   * @param x3,y3 : coord du 3ieme point
   * @param x4,y4 : coord du 4ieme point
   */
  ROIExtend(int x1, int y1, int x2, int y2,int x3, int y3, int x4, int y4);

  /**
   * Copy Constructor
   */
  ROIExtend( const ROIExtend& roi);
  
  /// Destructor
  virtual ~ROIExtend();
  
  /**
   * Fonction retournant la valeur du point(x,y)
   * @param x : numero du point (1,2,3,4)
   * @param y : valeur abscisse ou ordonnée (0,1)
   */
  int getCoord(int x, int y);
  /**
   * Fonction retournant la valeur la plus haute en ordonnée
   */
  int getTop();
  /**
   * Fonction retournant la valeur la plus basse en ordonnée
   */
  int getBottom();
  /**
   * Fonction retournant la valeur la plus a gauche
   */
  int getLeft();
  /**
   * Fonction retournant la valeur la plus a droite
   */
  int getRight();
  /**
   * Fonction retournant un boolean  indiquant si le point (x,y) se trouve dans la ROI
   * @param x : coordonnée en abscisse
   * @param y : coordonnée en ordonnée
   */
  bool isInside(int x, int y);
  /**
   * Fonction pour mettre à jour la ROI en fonction d'une ROI passée en paramètre
   * @param roi : ROI source
   */
  void setROIExtend(ROIExtend& roi);
  /**
   * Fonction retournant la coordonnée en abscisse du centre de la ROI
   */
  int getCenterX();
  /**
   * Fonction retournant la coordonnée en ordonnée du centre de la ROI
   */
  int getCenterY();

  /**
   * For omiscid serialization
   */
  template <class C>
  void Save( C & msg )
    {
      msg.Put("x1", roi_[0][0]);
      msg.Put("y1", roi_[0][1]);
      msg.Put("x2", roi_[1][0]);
      msg.Put("y2", roi_[1][1]);
      msg.Put("x3", roi_[2][0]);
      msg.Put("y3", roi_[2][1]);
      msg.Put("x4", roi_[3][0]);
      msg.Put("y4", roi_[3][1]);
    }

  template <class C>
  void Load( C & msg )
    {
      msg.Get("x1", roi_[0][0]);
      msg.Get("y1", roi_[0][1]);
      msg.Get("x2", roi_[1][0]);
      msg.Get("y2", roi_[1][1]);
      msg.Get("x3", roi_[2][0]);
      msg.Get("y3", roi_[2][1]);
      msg.Get("x4", roi_[3][0]);
      msg.Get("y4", roi_[3][1]);
    }
};

#endif // ROIEXTEND_HPP
