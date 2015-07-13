package main.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;

import main.utilities.DataSet;
import main.utilities.InkMLParser;
import main.utilities.TraceGroupSWT;
import main.utilities.Utilities;
import main.utilities.DataSample;

public class Splitter{
  public Splitter(String inputPath, String outputPath) throws FileNotFoundException{
    inputPath_ = inputPath;
    outputPath_ = outputPath;

    chosenTraces_ = new ArrayList<Integer>();
    highlightedTrace_ = 0;

    parser_ = new InkMLParser();

    File inputDirectory = new File(inputPath_);
    equationFiles_ = inputDirectory.listFiles();
    currentFile_ = -1;

    dataSet_ = new DataSet();

    processedFiles_ = new ArrayList<String>();
  }

  public Image getImage(Device device) throws FileNotFoundException{
    Color[] colors = new Color[parser_.traceGroup_.size()];
    int[] indices = new int[parser_.traceGroup_.size()];
    for(int i = 0;i < indices.length;i++){
      indices[i] = i;
      if(i == highlightedTrace_){
        colors[i] = new Color(device, new RGB(0, 255, 0));
      }
      else if(chosenTraces_.contains(i)){
        colors[i] = new Color(device, new RGB(255, 165, 0));
      }
      else{
        colors[i] = new Color(device, new RGB(0, 0, 0));
      }
    }

    return (parser_.traceGroup_.print(device, colors, indices));
  }

  public void parseNextFile() throws FileNotFoundException{
    processedFiles_.add(this.getCurrentFileName());

    parser_.setXMLData(this.getNextFile());
    parser_.parse();

    this.reset();
  }

  public String getNextFile() throws FileNotFoundException{
    currentFile_++;
    if(currentFile_ >= equationFiles_.length){
      currentFile_ = 0;
    }

    Scanner scanner = new Scanner(equationFiles_[currentFile_]);
    scanner.useDelimiter("\n");

    String xmlData = new String("");
    while(scanner.hasNextLine()){
      xmlData += scanner.next();
    }
    scanner.close();

    return xmlData;
  }

  public String getCurrentFileName(){
    if(currentFile_ < 0 || currentFile_ >= equationFiles_.length){
      return "";
    }

    return (equationFiles_[currentFile_].getName());
  }

  public void parsePreviousFile() throws FileNotFoundException{
    processedFiles_.add(this.getCurrentFileName());

    parser_.setXMLData(this.getPreviousFile());
    parser_.parse();

    this.reset();
  }

  public String getPreviousFile() throws FileNotFoundException{
    currentFile_--;
    if(currentFile_ < 0){
      currentFile_ = equationFiles_.length - 1;
    }

    Scanner scanner = new Scanner(equationFiles_[currentFile_]);
    scanner.useDelimiter("\n");

    String xmlData = new String("");
    while(scanner.hasNextLine()){
      xmlData += scanner.next();
    }
    scanner.close();

    return xmlData;
  }

  public void nextTrace(){
    highlightedTrace_++;

    if(highlightedTrace_ == parser_.traceGroup_.size()){
      highlightedTrace_ = 0;
    }
  }

  public void previousTrace(){
    highlightedTrace_--;

    if(highlightedTrace_ < 0){
      highlightedTrace_ = parser_.traceGroup_.size() - 1;
    }
  }

  public void holdTrace(){
    if(chosenTraces_.contains(highlightedTrace_)){
      chosenTraces_.remove(new Integer(highlightedTrace_));
    }
    else{
      chosenTraces_.add(highlightedTrace_);
    }
  }

  public void save(String label){
    String filename = "";

    filename = this.getCurrentFileName().replaceAll(".xml", "") + "_" + label + ".tiff";

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    TraceGroupSWT savable = new TraceGroupSWT();

    for(int i = 0;i < chosenTraces_.size();i++){
      savable.add(parser_.traceGroup_.get(chosenTraces_.get(i)));
    }

    Mat image = savable.printOpenCV(new Size(28, 28));

    Highgui.imwrite(outputPath_ + "/" + label + "/" + filename, image);

    dataSet_.add(new DataSample(Utilities.imageToByteArray(image), Symbol.stringToByte(label)));

    this.reset();
  }

  public void reset(){
    highlightedTrace_ = 0;
    chosenTraces_.clear();
  }

  public void exit() throws IOException{
    dataSet_.saveIDXFormat(outputPath_ + "data", outputPath_ + "labels");

    for(int i = 0;i < processedFiles_.size();i++){
      System.out.println(processedFiles_.get(i));
    }
  }

  private enum Symbol{
    ZERO(0, "0", (byte)0x00),
    ONE(1, "1", (byte)0x01),
    TWO(2, "2", (byte)0x02),
    THREE(3, "3", (byte)0x03),
    FOUR(4, "4", (byte)0x04),
    FIVE(5, "5", (byte)0x05),
    SIX(6, "6", (byte)0x06),
    SEVEN(7, "7", (byte)0x07),
    EIGHT(8, "8", (byte)0x08),
    NINE(9, "9", (byte)0x09),
    PLUS(10, "+", (byte)0x0A),
    EQUALS(11, "=", (byte)0x0B),
    VARIABLE_X(12, "var_x", (byte)0x0C),
    VARIABLE_Y(13, "var_y", (byte)0x0D),
    HORIZONTAL_LINE(14, "-", (byte)0x0E),
    EXP(15, "e", (byte)0x0F);

    public static byte stringToByte(String stringValue){
      for(Symbol symbol : Symbol.values()){
        if(symbol.toString().equals(stringValue)){
          return symbol.toByte();
        }
      }

      return (byte)0x00;
    }

    private Symbol(int intValue, String stringValue, byte byteValue){
      intValue_ = intValue;
      stringValue_ = stringValue;
      byteValue_ = byteValue;
    }

    @Override
    public String toString(){
      return stringValue_;
    }

    public int toInt(){
      return intValue_;
    }

    public byte toByte(){
      return byteValue_;
    }

    private int intValue_;
    private String stringValue_;
    private byte byteValue_;
  }

  private int highlightedTrace_;
  private ArrayList<Integer> chosenTraces_;

  private String inputPath_;
  private String outputPath_;

  public InkMLParser parser_;

  private File[] equationFiles_;
  private int currentFile_;

  private ArrayList<String> processedFiles_;

  private DataSet dataSet_;

}
