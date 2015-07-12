package main.controler;

import java.io.FileNotFoundException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import main.model.Splitter;
import main.view.MainView;

public class SymbolExtractor{
  public SymbolExtractor(MainView view, Splitter splitter){
    view_ = view;
    splitter_ = splitter;

    view_.nextButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        splitter_.nextTrace();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.previousButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        splitter_.previousTrace();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.holdButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e) {
        splitter_.holdTrace();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.submitButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        try {
          splitter_.parseNextFile();

          view_.setImage(splitter_.getImage(view_.display_));
        }
        catch(FileNotFoundException exception){
          exception.printStackTrace();
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });
  }

  public void start() throws FileNotFoundException{
    splitter_.parseNextFile();

    view_.setImage(splitter_.getImage(view_.display_));

    view_.start();
  }

  private Splitter splitter_;

  private MainView view_;

}
