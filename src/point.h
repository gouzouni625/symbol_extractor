#ifndef POINT_H
#define POINT_H

#include <iostream>

namespace point{

class Point{
public:
  Point();

  Point(double x, double y);

  void print();

  ~Point();

  double x_;
  double y_;

};

}

#endif // POINT_H
