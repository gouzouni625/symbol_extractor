package main.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
    filePartition_ = new ArrayList<ArrayList<Integer>>();
    labels_ = new ArrayList<String>();
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

    if(currentFile_ >= 0){
      this.saveFilePartition();
    }
    filePartition_ = new ArrayList<ArrayList<Integer>>();
    labels_ = new ArrayList<String>();

    parser_.setXMLData(this.getNextFile());
    parser_.parse();

    this.reset();
  }

  private void saveFilePartition() throws FileNotFoundException{
    FileOutputStream fileOutputStream = new FileOutputStream(outputPath_ + "/partitions/" + getCurrentFileName());
    PrintWriter printWriter = new PrintWriter(fileOutputStream);

    printWriter.println(parser_.traceGroup_.size());
    for(int i = 0;i < filePartition_.size();i++){
      for(int j = 0;j < filePartition_.get(i).size();j++){
        printWriter.print(filePartition_.get(i).get(j) + ", ");
      }

      printWriter.println(", " + labels_.get(i));
    }

    printWriter.close();
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
    ArrayList<Integer> symbol = new ArrayList<Integer>();

    for(int i = 0;i < chosenTraces_.size();i++){
      symbol.add(chosenTraces_.get(i));
    }
    filePartition_.add(symbol);
    labels_.add(label);

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

    chosenTraces_.clear();
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
    LOWER_X(12, "x", (byte)0x0C),
    LOWER_Y(13, "y", (byte)0x0D),
    HORIZONTAL_LINE(14, "_", (byte)0x0E),
    CAPITAL_A(15, "A", (byte)0x0F),
    CAPITAL_B(16, "B", (byte)0x10),
    CAPITAL_C(17, "C", (byte)0x11),
    CAPITAL_D(18, "D", (byte)0x12),
    CAPITAL_E(19, "E", (byte)0x13),
    CAPITAL_F(20, "F", (byte)0x14),
    CAPITAL_G(21, "G", (byte)0x15),
    CAPITAL_H(22, "H", (byte)0x16),
    CAPITAL_I(23, "I", (byte)0x17),
    CAPITAL_J(24, "J", (byte)0x18),
    CAPITAL_K(25, "K", (byte)0x19),
    CAPITAL_L(26, "L", (byte)0x1A),
    CAPITAL_M(27, "M", (byte)0x1B),
    CAPITAL_N(28, "N", (byte)0x1C),
    CAPITAL_O(29, "O", (byte)0x1D),
    CAPITAL_P(30, "P", (byte)0x1E),
    CAPITAL_Q(31, "Q", (byte)0x1F),
    CAPITAL_R(32, "R", (byte)0x20),
    CAPITAL_S(33, "S", (byte)0x21),
    CAPITAL_T(34, "T", (byte)0x22),
    CAPITAL_U(35, "U", (byte)0x23),
    CAPITAL_V(36, "V", (byte)0x24),
    CAPITAL_W(37, "W", (byte)0x25),
    CAPITAL_X(38, "X", (byte)0x26),
    CAPITAL_Y(39, "Y", (byte)0x27),
    CAPITAL_Z(40, "Z", (byte)0x28),
    LOWER_A(41, "a", (byte)0x29),
    LOWER_C(42, "c", (byte)0x2A),
    LOWER_E(43, "e", (byte)0x2B),
    LOWER_I(44, "i", (byte)0x2C),
    LOWER_L(45, "l", (byte)0x2D),
    LOWER_N(46, "n", (byte)0x2E),
    LOWER_O(47, "o", (byte)0x2F),
    LOWER_P(48, "p", (byte)0x30),
    LOWER_S(49, "s", (byte)0x31),
    LOWER_T(50, "t", (byte)0x32),
    // LOWER_X(12, "x", (byte)0x0C),
    // LOWER_Y(13, "y", (byte)0x0D),
    COMMA(51, ",", (byte)0x33),
    LEFT_PARENTHESIS(52, "left_par", (byte)0x34),
    RIGHT_PARENTHESIS(53, "right_par", (byte)0x35),
    SQRT(54, "sqrt", (byte)0x36);

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

  private ArrayList<ArrayList<Integer>> filePartition_;
  private ArrayList<String> labels_;

  private String inputPath_;
  private String outputPath_;

  public InkMLParser parser_;

  private File[] equationFiles_;
  private int currentFile_;

  private ArrayList<String> processedFiles_;

  private DataSet dataSet_;

}
