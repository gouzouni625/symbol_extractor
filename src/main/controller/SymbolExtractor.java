package main.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

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
        try{
          draw();
        }
        catch(FileNotFoundException exception){
          exception.printStackTrace();
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.previousButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        splitter_.previousTrace();
        try{
          draw();
        }
        catch(FileNotFoundException exception){
          exception.printStackTrace();
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.holdButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e) {
        splitter_.holdTrace();
        try{
          draw();
        }
        catch(FileNotFoundException exception){
          exception.printStackTrace();
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.previousImageButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        view_.display_.asyncExec(new Runnable(){

          @Override
          public void run(){
            try {
              showPreviousImage();
            }
            catch(FileNotFoundException e){
              e.printStackTrace();
            }
          }
        });
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.nextImageButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        view_.display_.asyncExec(new Runnable(){

          @Override
          public void run(){
            try {
              showNextImage();
            }
            catch(FileNotFoundException e){
              e.printStackTrace();
            }
          }
        });
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.saveButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        view_.display_.asyncExec(new Runnable(){

          @Override
          public void run(){
            String label = view_.inputText_.getText();

            splitter_.save(label);

            try {
              draw();
            }
            catch(FileNotFoundException exception){
              exception.printStackTrace();
            }
          }
        });
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    view_.closeButton_.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent exception){
        try {
          splitter_.exit();

          view_.shell_.dispose();

          System.exit(0);
        }
        catch(IOException e){
          e.printStackTrace();
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });
  }

  public void start() throws FileNotFoundException{
    showNextImage();

    view_.start();
  }

  public void showNextImage() throws FileNotFoundException{
    splitter_.parseNextFile();
    draw();
    view_.setText(splitter_.getCurrentFileName() + " " + splitter_.parser_.getEquation());
  }

  public void showPreviousImage() throws FileNotFoundException{
    splitter_.parsePreviousFile();
    draw();
    view_.setText(splitter_.getCurrentFileName() + " " + splitter_.parser_.getEquation());
  }

  public void draw() throws FileNotFoundException{
    view_.setImage(splitter_.getImage(view_.display_));
  }

  private Splitter splitter_;

  private MainView view_;

}
