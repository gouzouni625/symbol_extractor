package main.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MainView{
  public MainView(){
    display_ = new Display();

    shell_ = new Shell(display_);
    shell_.setText("Main View");
    shell_.setLayout(new GridLayout(1, false));

    previousButton_ = new Button(shell_, SWT.PUSH);
    previousButton_.setText("previous");

    nextButton_ = new Button(shell_, SWT.PUSH);
    nextButton_.setText("next");

    holdButton_ = new Button(shell_, SWT.PUSH);
    holdButton_.setText("hold");

    inputText_ = new Text(shell_, SWT.BORDER);

    previousImageButton_ = new Button(shell_, SWT.PUSH);
    previousImageButton_.setText("previous image");

    nextImageButton_ = new Button(shell_, SWT.PUSH);
    nextImageButton_.setText("next image");

    saveButton_ = new Button(shell_, SWT.PUSH);
    saveButton_.setText("save");

    label_ = new Label(shell_, SWT.BORDER);
  }

  public void start(){
    shell_.pack();
    shell_.open();

    while(!shell_.isDisposed()){
      if(!display_.readAndDispatch()){
        display_.sleep();
      }
    }

    this.dispose();
  }

  public void setImage(Image image){
    label_.setImage(image);

    label_.pack();
    shell_.pack();
  }

  public void setText(String text){
    shell_.setText(text);
  }

  public void dispose(){
    inputText_.dispose();

    nextImageButton_.dispose();
    previousButton_.dispose();
    nextButton_.dispose();
    holdButton_.dispose();

    label_.dispose();
    shell_.dispose();
    display_.dispose();
  }


  public Display display_;
  public Shell shell_;
  public Label label_;

  public Button nextButton_;
  public Button previousButton_;
  public Button holdButton_;
  public Button saveButton_;
  public Button previousImageButton_;
  public Button nextImageButton_;

  public Text inputText_;

}
