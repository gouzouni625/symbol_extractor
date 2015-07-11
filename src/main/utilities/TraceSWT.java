package main.utilities;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public class TraceSWT extends main.utilities.traces.Trace{
  public Image print(Image image){
    GC gc = new GC(image);

    for(int i = 0;i < points_.size() - 1;i++){
      gc.drawLine((int)points_.get(i).x_, (int)points_.get(i).y_,
                  (int)points_.get(i + 1).x_, (int)points_.get(i + 1).y_);
    }

    return image;
  }

}
