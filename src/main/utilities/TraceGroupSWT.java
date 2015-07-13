package main.utilities;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class TraceGroupSWT{
    public TraceGroupSWT(){
      traces_ = new ArrayList<TraceSWT>();
    }

    public TraceGroupSWT(TraceGroupSWT traceGroup){
      traces_ = new ArrayList<TraceSWT>();

      for(int i = 0;i < traceGroup.size();i++){
        this.add(traceGroup.get(i));
      }
    }

    public void add(TraceSWT trace){
      traces_.add(new TraceSWT(trace));
    }

    public void add(TraceGroupSWT traceGroup){
      for(int i = 0;i < traceGroup.size();i++){
        this.add(traceGroup.get(i));
      }
    }

    public TraceSWT get(int index){
      return traces_.get(index);
    }

    public int size(){
      return traces_.size();
    }

    public TraceGroupSWT subTraceGroup(int[] tracesIndeces){
      TraceGroupSWT traceGroup = new TraceGroupSWT();

      for(int i = 0;i < tracesIndeces.length;i++){
        traceGroup.add(traces_.get(tracesIndeces[i]));
      }

      return traceGroup;
    }

    public TraceGroupSWT multiplyBy(double factor){
      for(int i = 0;i < traces_.size();i++){
        traces_.get(i).multiplyBy(factor);
      }

      return this;
    }

    public TraceGroupSWT subtract(Point point){
      for(int i = 0;i < traces_.size();i++){
        traces_.get(i).subtract(point);
      }

      return this;
    }

    public void calculateCorners(){
      if(traces_ == null || traces_.size() == 0){
        return;
      }

      traces_.get(0).calculateCorners();

      double minX = traces_.get(0).getTopLeftCorner().x_;
      double maxX = traces_.get(0).getBottomRightCorner().x_;
      double minY = traces_.get(0).getBottomRightCorner().y_;
      double maxY = traces_.get(0).getTopLeftCorner().y_;

      for(int i = 0;i < traces_.size();i++){
        traces_.get(i).calculateCorners();

        Point topLeftCorner = traces_.get(i).getTopLeftCorner();
        Point bottomRightCorner = traces_.get(i).getBottomRightCorner();

        if(topLeftCorner.x_ < minX){
          minX = topLeftCorner.x_;
        }

        if(bottomRightCorner.x_ > maxX){
          maxX = bottomRightCorner.x_;
        }

        if(bottomRightCorner.y_ < minY){
          minY = bottomRightCorner.y_;
        }

        if(topLeftCorner.y_ > maxY){
          maxY = topLeftCorner.y_;
        }
      }

      topLeftCorner_ = new Point(minX, maxY);
      bottomRightCorner_ = new Point(maxX, minY);
    }

    public Point getTopLeftCorner(){
      return (new Point(topLeftCorner_));
    }

    public Point getBottomRightCorner(){
      return (new Point(bottomRightCorner_));
    }

    public double getWidth(){
      return (bottomRightCorner_.x_ - topLeftCorner_.x_);
    }

    public double getHeight(){
      return (topLeftCorner_.y_ - bottomRightCorner_.y_);
    }

    public Point getCentroid(){
      this.calculateCorners();

      double centroidX = topLeftCorner_.x_ + this.getWidth() / 2;
      double centroidY = bottomRightCorner_.y_ + this.getHeight() / 2;

      return (new Point(centroidX, centroidY));
    }

    public Image print(Device device, Color[] colors, int[] indices){
      // Work on a copy of this trace group.
      TraceGroupSWT traceGroup = new TraceGroupSWT(this);

      // Data provided by GeoGebra will have 2 decimal digits, that is why the
      // multiplication is done with 100.
      traceGroup.multiplyBy(100);

      traceGroup.calculateCorners();

      traceGroup.subtract(new Point(traceGroup.getTopLeftCorner().x_, traceGroup.getBottomRightCorner().y_));

      double width = traceGroup.getWidth();
      if(width < 100){
        width = 100;
      }
      double height = traceGroup.getHeight();
      if(height < 100){
        height = 100;
      }

      Image image = new Image(device, (int)(1.10 * width), (int)(1.10 * height));
      GC gc = new GC(image);
      gc.setLineWidth(5);

      if(indices == null){
        if(colors == null){
          for(int i = 0;i < traceGroup.size();i++){
            traceGroup.get(i).print(gc, gc.getForeground(), (int)width, (int)height);
          }
        }
        else if(colors.length == 1){
          for(int i = 0;i < traceGroup.size();i++){
            traceGroup.get(i).print(gc, colors[0], (int)width, (int)height);
          }
        }
        else{
          for(int i = 0;i < traceGroup.size();i++){
            traceGroup.get(i).print(gc, colors[i], (int)width, (int)height);
          }
        }
      }
      else if(indices.length == 1){
        if(colors == null){
          traceGroup.get(indices[0]).print(gc, gc.getForeground(), (int)width, (int)height);
        }
        else if(colors.length == 1){
          traceGroup.get(indices[0]).print(gc, colors[0], (int)width, (int)height);
        }
        else{
          traceGroup.get(indices[0]).print(gc, colors[0], (int)width, (int)height);
        }
      }
      else{
        if(colors == null){
          for(int i = 0;i < indices.length;i++){
            traceGroup.get(indices[i]).print(gc, gc.getForeground(), (int)width, (int)height);
          }
        }
        else if(colors.length == 1){
          for(int i = 0;i < indices.length;i++){
            traceGroup.get(indices[i]).print(gc, colors[0], (int)width, (int)height);
          }
        }
        else{
          for(int i = 0;i < indices.length;i++){
            traceGroup.get(indices[i]).print(gc, colors[i], (int)width, (int)height);
          }
        }
      }

      return image;
    }

    public Mat printOpenCV(Size size){
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

      // Work on a copy of this trace group.
      TraceGroupSWT traceGroup = new TraceGroupSWT(this);

      // Data provided by GeoGebra will have 2 decimal digits, that is why the
      // multiplication is done with 100.
      traceGroup.multiplyBy(100);

      traceGroup.calculateCorners();

      traceGroup.subtract(new Point(traceGroup.getTopLeftCorner().x_, traceGroup.getBottomRightCorner().y_));

      double width = traceGroup.getWidth();
      if(width < 100){
        width = 100;
      }
      double height = traceGroup.getHeight();
      if(height < 100){
        height = 100;
      }

      Mat image = Mat.zeros(new Size(width, height), CvType.CV_32F);

      int thickness = (int)((height + width) / 2 * 30 / 1000);
      if(thickness <= 0){
        thickness = 1;
      }
      else if(thickness > 255){
        thickness = 255;
      }

      for(int i = 0;i < traceGroup.size();i++){
        traceGroup.get(i).printOpenCV(image, thickness);
      }

      Imgproc.resize(image, image, new Size(1000, 1000));

      Imgproc.copyMakeBorder(image, image, 500, 500, 500, 500, Imgproc.BORDER_CONSTANT, new Scalar(0, 0, 0));

      Imgproc.blur(image, image, new Size(200, 200));

      Imgproc.resize(image, image, size);

      double meanValue = 0;
      for(int i = 0;i < size.height;i++){
        for(int j = 0;j < size.width;j++){
          meanValue += image.get(i, j)[0];
        }
      }
      meanValue /= (size.height * size.width);

      double value;
      for(int i = 0;i < size.height;i++){
        for(int j = 0;j < size.width;j++){
          value = image.get(i, j)[0];
          if(value > meanValue){
            image.put(i, j, 255);
          }
          else{
            image.put(i, j, 0);
          }
        }
      }

      return image;
    }

    private ArrayList<TraceSWT> traces_;

    private Point topLeftCorner_;
    private Point bottomRightCorner_;

}
