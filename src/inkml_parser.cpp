#include "inkml_parser.h"

namespace inkml_parser{

InkMLParser::InkMLParser(std::string xmlData){
  xmlData_ = xmlData;
}

void InkMLParser::parse(){
  // Create a copy of the original xml data.
  std::string xmlData = xmlData_;

  int startOfTrace = xmlData.find("<trace>");
  int endOfTrace = xmlData.find("</trace>");

  while(startOfTrace != std::string::npos){
    std::vector<std::string> traceData = this->split(xmlData.substr(startOfTrace + std::string("<trace>").size(), endOfTrace - startOfTrace - std::string("<trace>").size()), ", ");

    trace::Trace trace;
    for(int i = 0;i < traceData.size();i++){
      std::vector<std::string> coordinates = this->split(traceData[i], " ");
      double x = std::stod(coordinates[0]);
      double y = std::stod(coordinates[1]);
      point::Point point(x, y);
      trace.add(point);
    }
    if(trace.size() > 1){
      traces_.push_back(trace);
    }

    xmlData = xmlData.substr(endOfTrace + std::string("</trace>").size());
    startOfTrace = xmlData.find("<trace>");
    endOfTrace = xmlData.find("</trace>");
  }
}

trace::Trace& InkMLParser::get(int index){
  return (traces_[index]);
}

std::vector<trace::Trace>& InkMLParser::getAll(){
  return traces_;
}

int InkMLParser::size(){
  return traces_.size();
}

std::vector<std::string> InkMLParser::split(std::string string, std::string delimiter){
  std::vector<std::string> result;

  int startOfDelimiter = string.find(delimiter);
  while(startOfDelimiter != std::string::npos){
    result.push_back(string.substr(0, startOfDelimiter));
    string = string.substr(startOfDelimiter + delimiter.size());
    startOfDelimiter = string.find(delimiter);
  }
  if(string.size() > 0){
    result.push_back(string);
  }

  return result;
}

InkMLParser::~InkMLParser(){
}

}
