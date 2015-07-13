package main.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import main.utilities.InkMLParser;
import main.utilities.TraceGroupSWT;

public class Splitter{
  public Splitter(String inputPath, String outputPath){
    inputPath_ = inputPath;
    outputPath_ = outputPath;

    chosenTraces_ = new ArrayList<Integer>();
    highlightedTrace_ = 0;

    parser_ = new InkMLParser();

    File inputDirectory = new File(inputPath_);
    equationFiles_ = inputDirectory.listFiles();
    currentFile_ = -1;
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
    if(currentFile_ >= equationFiles_.length){
      return Splitter.NO_MORE_FILES;
    }
    else{
      return (equationFiles_[currentFile_].getName());
    }
  }

  public void parsePreviousFile() throws FileNotFoundException{
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

  public void save(int label){
  }

  public void reset(){
    highlightedTrace_ = 0;
    chosenTraces_.clear();
  }

  private enum Symbol{
    ONE(1, "1"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    PLUS(10, "+"),
    EQUALS(11, "="),
    VARIABLE_X(12, "var_x"),
    VARIABLE_Y(13, "var_y"),
    HORIZONTAL_LINE(14, "-");

    private Symbol(int intValue, String stringValue){
      intValue_ = intValue;
      stringValue_ = stringValue;
    }

    @Override
    public String toString(){
      return stringValue_;
    }

    public int toInt(){
      return intValue_;
    }

    private int intValue_;
    private String stringValue_;
  }

  private int highlightedTrace_;
  private ArrayList<Integer> chosenTraces_;

  private String inputPath_;
  private String outputPath_;

  public InkMLParser parser_;

  private File[] equationFiles_;
  private int currentFile_;

  public static final String NO_MORE_FILES = "NO_MORE_FILES";

}
