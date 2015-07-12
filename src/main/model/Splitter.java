package main.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

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
    return (parser_.traceGroup_.print(device, null, null));
  }

  public void parseNextFile() throws FileNotFoundException{
    parser_.setXMLData(this.getNextFile());
    parser_.parse();
  }

  public String getNextFile() throws FileNotFoundException{
    currentFile_++;
    if(currentFile_ >= equationFiles_.length){
      return Splitter.NO_MORE_FILES;
    }

    Scanner scanner = new Scanner(equationFiles_[currentFile_]);
    scanner.useDelimiter("\n");

    String xmlData = new String("");
    while(scanner.hasNextLine()){
      xmlData += scanner.next();
    }
    scanner.close();

    currentFile_++;

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
      chosenTraces_.remove(highlightedTrace_);
    }
    else{
      chosenTraces_.add(highlightedTrace_);
    }
  }

  private enum Symbol{
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    PLUS("10"),
    EQUALS("11"),
    VARIABLE_X("12"),
    VARIABLE_Y("13"),
    HORIZONTAL_LINE("14");

    private Symbol(String stringValue){
      stringValue_ = stringValue;
    }

    @Override
    public String toString(){
      return stringValue_;
    }

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
