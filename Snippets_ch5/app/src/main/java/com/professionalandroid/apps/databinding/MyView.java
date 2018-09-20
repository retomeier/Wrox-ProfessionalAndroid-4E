/*
 * Professional Android, 4th Edition
 * Reto Meier and Ian Lake
 * Copyright 2018 John Wiley Wiley & Sons, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.professionalandroid.apps.databinding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/*
 * Listing 5-12: Creating a new View
 */

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

  /*
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
  */

  /*
   * Listing 5-13: Drawing a custom View
   */

  @Override
  protected void onDraw(Canvas canvas) {
    // Get the size of the control based on the last call to onMeasure.
    int height = getMeasuredHeight();
    int width = getMeasuredWidth();

    // Find the center
    int px = width/2;
    int py = height/2;

    // Create the new paint brushes.
    // NOTE: For efficiency this should be done in
    // the views's constructor
    Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setColor(Color.WHITE);

    // Define the string.
    String displayText = "Hello View!";

    // Measure the width of the text string.
    float textWidth = mTextPaint.measureText(displayText);

    // Draw the text string in the center of the control.
    canvas.drawText(displayText, px-textWidth/2, py, mTextPaint);
  }

  /*
   * Listing 5-14: A typical View measurement implementation
   */
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int measuredHeight = measureHeight(heightMeasureSpec);
    int measuredWidth = measureWidth(widthMeasureSpec);
    setMeasuredDimension(measuredHeight, measuredWidth);
  }

  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    // Default size in pixels if no limits are specified.
    int result = 500;

    if (specMode == MeasureSpec.AT_MOST) {
      // Calculate the ideal size of your
      // control within this maximum size.
      // If your control fills the available
      // space return the outer bound.
      result = specSize;
    } else if (specMode == MeasureSpec.EXACTLY) {
      // If your control can fit within these bounds return that value.
      result = specSize;
    }

    return result;
  }

  private int measureWidth(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    // Default size in pixels if no limits are specified.
    int result = 500;

    if (specMode == MeasureSpec.AT_MOST) {
      // Calculate the ideal size of your control
      // within this maximum size.
      // If your control fills the available space
      // return the outer bound.
      result = specSize;
    } else if (specMode == MeasureSpec.EXACTLY) {
      // If your control can fit within these bounds return that value.
      result = specSize;
    }

    return result;
  }

  /*
   * Listing 5-15: Input event handling for Views
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
    // Return true if the event was handled.
    return true;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
    // Return true if the event was handled.
    return true;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // Get the type of action this event represents
    int actionPerformed = event.getAction();

    // Return true if the event was handled.
    return true;
  }

  Season mSeason;

  /*
   * Listing 5-16: Broadcasting Accessibility Events
   */
  public void setSeason(Season season) {
    mSeason = season;
    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
  }

  /*
   * Listing 5-17: Customizing Accessibility Event properties
   */
  @Override
  public boolean dispatchPopulateAccessibilityEvent(
    final AccessibilityEvent event) {
    super.dispatchPopulateAccessibilityEvent(event);
    if (isShown()) {
      String seasonStr = Season.valueOf(mSeason);
      if (seasonStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH)
        seasonStr =
          seasonStr.substring(0, AccessibilityEvent.MAX_TEXT_LENGTH-1);
      event.getText().add(seasonStr);
      return true;
    }
    else
      return false;
  }
}