package main.utilities;

public class InkMLParser{
  public InkMLParser(){
    traceGroup_ = new TraceGroupSWT();
  }

  public InkMLParser(String xmlData){
    xmlData_ = xmlData;

    traceGroup_ = new TraceGroupSWT();
  }

  public void parse(){
    String xmlData = new String(xmlData_);

    int startOfTrace = xmlData.indexOf("<trace>");
    int endOfTrace = xmlData.indexOf("</trace>");
    while(startOfTrace != -1){
      String[] traceData = xmlData.substring(startOfTrace + 7, endOfTrace).split(", "); // ("<trace>").length = 7.

      TraceSWT trace = new TraceSWT();
      for(int i = 0;i < traceData.length;i++){
        double x = Double.parseDouble(traceData[i].split(" ")[0]);
        double y = Double.parseDouble(traceData[i].split(" ")[1]);
        trace.add(new Point(x, y));
      }
      if(trace.size() > 1){
        traceGroup_.add(trace);
      }

      xmlData = xmlData.substring(endOfTrace + 8); // ("</trace>").length = 8.
      startOfTrace = xmlData.indexOf("<trace>");
      endOfTrace = xmlData.indexOf("</trace>");
    }
  }

  public void setXMLData(String xmlData){
    xmlData_ = xmlData;

    traceGroup_ = new TraceGroupSWT();
  }

  private String xmlData_;

  public TraceGroupSWT traceGroup_;

}
