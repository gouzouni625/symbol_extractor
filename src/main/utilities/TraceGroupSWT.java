package main.utilities;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

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

    public Image print(Device device, int imageWidth, int imageHeight){
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

      Image image = new Image(device, imageWidth, imageHeight);

      for(int i = 0;i < traceGroup.size();i++){
        ((TraceSWT)(traceGroup.get(i))).print(image);
      }

      return image;
    }

    private ArrayList<TraceSWT> traces_;

    private Point topLeftCorner_;
    private Point bottomRightCorner_;

}
