#ifndef TRACE_H
#define TRACE_H

#include <iostream>
#include <vector>

#include "point.h"

namespace trace{

class Trace{
public:
  Trace();

  void add(point::Point point);

  point::Point& get(int index);

  int size();

  void print();

  ~Trace();

private:
  std::vector<point::Point> points_;

};

}

#endif // TRACE_H

