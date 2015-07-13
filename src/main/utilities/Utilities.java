package main.utilities;

import org.opencv.core.Mat;

/**
 * Class that contains some methods used in many different places of the
 * project.
 *
 * @author Georgios Ouzounis
 *
 * TODO
 * Many of these methods can be templates(very low priority).
 *
 */
public class Utilities{
  public static byte[] imageToByteArray(Mat image){
    int numberOfRows = image.rows();
    int numberOfColumns = image.cols();

    byte[] array = new byte[numberOfRows * numberOfColumns];
    if(array.length == 0){
      return array;
    }

    for(int row = 0;row < numberOfRows;row++){
      for(int column = 0;column < numberOfColumns;column++){
        array[row * numberOfColumns + column] = (byte)(image.get(row, column)[0]);
      }
    }

    return array;
  }

}
