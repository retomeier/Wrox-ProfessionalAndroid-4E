package com.professionalandroid.apps.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {

  public MyView(Context context) {
    this(context, null);
  }

  public MyView (Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
    int measuredHeight = measureHeight(hMeasureSpec);
    int measuredWidth = measureWidth(wMeasureSpec);

    // MUST make this call to setMeasuredDimension
    // or you will cause a runtime exception when
    // the control is laid out.
    setMeasuredDimension(measuredHeight, measuredWidth);
  }

  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    // TODO [ ... Calculate the view height ... ]

    return specSize;
  }

  private int measureWidth(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    // TODO [ ... Calculate the view width ... ]

    return specSize;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    // TODO [ ... Draw your visual interface ... ]
  }

  /*
   * Listing 14-6: Handling touch screen movement events
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    switch (action) {
      case (MotionEvent.ACTION_MOVE):
      {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
          float x = event.getHistoricalX(i);
          float y = event.getHistoricalY(i);
          processMovement(x, y);
        }
        float x = event.getX();
        float y = event.getY();
        processMovement(x, y);
        return true;
      }
    }
    return super.onTouchEvent(event);
  }

  private void processMovement(float x, float y) {
    // TODO Do something on movement.
  }
}
