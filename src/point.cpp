#include "point.h"

namespace point{

Point::Point(){
  x_ = 0;
  y_ = 0;
}

Point::Point(double x, double y){
  x_ = x;
  y_ = y;
}

void Point::print(){
  std::cout << x_ << ", " << y_ << std::endl;
}

Point::~Point(){
}

}

