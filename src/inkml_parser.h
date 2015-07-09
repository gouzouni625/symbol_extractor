#ifndef INKML_PARSER_H
#define INKML_PARSER_H

#include <iostream>
#include <vector>
#include <string>

#include "trace.h"

namespace inkml_parser{

class InkMLParser{
public:
  InkMLParser(std::string xmlData);

  void parse();

  trace::Trace& get(int index);

  std::vector<trace::Trace>& getAll();

  int size();

  std::vector<std::string> split(std::string string, std::string delimiter = " ");

  ~InkMLParser();

private:
  std::string xmlData_;

  std::vector<trace::Trace> traces_;

};

}

#endif // INKML_PARSER_H

