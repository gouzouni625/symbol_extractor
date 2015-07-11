package main.utilities;

import main.utilities.traces.Point;
import main.utilities.traces.TraceGroup;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class TraceGroupSWT extends main.utilities.traces.TraceGroup{
  public Image print(Device device, int imageWidth, int imageHeight){
    // Work on a copy of this trace group.
    TraceGroup traceGroup = new TraceGroup(this);

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
      traceGroup.get(i).print(image);
    }

    return image;
  }

}
