package main.model;

import java.io.File;
import java.io.FileNotFoundException;
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

import com.sun.org.apache.xpath.internal.axes.OneStepIterator;

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
    return (equationFiles_[currentFile_].getName());
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

  public void save(String label){
    String filename = "";

    filename = this.getCurrentFileName().replaceAll(".xml", "") + "_" + label + ".tiff";

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    TraceGroupSWT savable = new TraceGroupSWT();

    for(int i = 0;i < chosenTraces_.size();i++){
      savable.add(parser_.traceGroup_.get(chosenTraces_.get(i)));
    }

    Mat image = savable.printOpenCV(new Size(28, 28));

    Highgui.imwrite(outputPath_ + "/" + filename, image);

    this.reset();
  }

  public void reset(){
    highlightedTrace_ = 0;
    chosenTraces_.clear();
  }

  private int highlightedTrace_;
  private ArrayList<Integer> chosenTraces_;

  private String inputPath_;
  private String outputPath_;

  public InkMLParser parser_;

  private File[] equationFiles_;
  private int currentFile_;

}
