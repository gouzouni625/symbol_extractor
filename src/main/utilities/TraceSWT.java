package main.utilities;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class TraceSWT{
  public TraceSWT(){
    points_ = new ArrayList<Point>();
  }

  // Constructor that will be called to create an identical Trace.
  public TraceSWT(TraceSWT trace){
    points_ = new ArrayList<Point>();

    for(int i = 0;i < trace.size();i++){
      this.add(trace.get(i));
    }
  }

  public void add(Point point){
    points_.add(new Point(point));
  }

  public Point get(int index){
    return points_.get(index);
  }

  public int size(){
    return points_.size();
  }

  public TraceSWT multiplyBy(double factor){
    for(int i = 0;i < points_.size();i++){
      points_.get(i).multiplyBy(factor);
    }

    return this;
  }

  public TraceSWT subtract(Point point){
    for(int i = 0;i < points_.size();i++){
      points_.get(i).subtract(point);
    }

    return this;
  }

  public void calculateCorners(){
    double minX = points_.get(0).x_;
    double maxX = points_.get(0).x_;
    double minY = points_.get(0).y_;
    double maxY = points_.get(0).y_;

    for(int i = 0;i < points_.size();i++){
      Point point = points_.get(i);

      if(point.x_ > maxX){
        maxX = point.x_;
      }

      if(point.x_ < minX){
        minX = point.x_;
      }

      if(point.y_ > maxY){
        maxY = point.y_;
      }

      if(point.y_ < minY){
        minY = point.y_;
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

  public void print(GC gc, Color color, int width, int height){
    Color oldColor = gc.getForeground();
    gc.setForeground(color);

    for(int i = 0;i < points_.size() - 1;i++){
      gc.drawLine((int)points_.get(i).x_, height - (int)points_.get(i).y_,
                  (int)points_.get(i + 1).x_, height - (int)points_.get(i + 1).y_);
    }

    gc.setForeground(oldColor);
  }

  private ArrayList<Point> points_;

  private Point topLeftCorner_;
  private Point bottomRightCorner_;

}
