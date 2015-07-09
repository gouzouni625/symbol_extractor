#include "trace.h"

namespace trace{

Trace::Trace(){
}

void Trace::add(point::Point point){
  points_.push_back(point);
}

point::Point& Trace::get(int index){
  return (points_[index]);
}

int Trace::size(){
  return (points_.size());
}

void Trace::print(){
  for(int i = 0;i < points_.size();i++){
    points_[i].print();
  }
}

Trace::~Trace(){
}

}

