package main.utilities;

public class DataSample{

  public DataSample(){
    label_ = -1;
  }

  public DataSample(byte[] data){
    data_ = data.clone();
    label_ = -1;
  }

  public DataSample(byte[] data, byte label){
    data_ = data.clone();
    label_ = label;
  }

  public DataSample(int size){
    data_ = new byte[size];
    label_ = -1;
  }

  public DataSample(DataSample dataSample){
    data_ = dataSample.data_.clone();
    label_ = dataSample.label_;
  }

  public byte[] data_;
  public byte label_;

}
