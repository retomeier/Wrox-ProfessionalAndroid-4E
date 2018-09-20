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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/*
 * LISTING 5-10: Constructing a compound View
 */

public class ClearableEditText extends LinearLayout {

  EditText editText;
  Button clearButton;

  public ClearableEditText(Context context) {
    this(context, null);
  }

  public ClearableEditText(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ClearableEditText(Context context, AttributeSet attrs,
                           int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    // Inflate the view from the layout resource.
    String infService = Context.LAYOUT_INFLATER_SERVICE;
    LayoutInflater li;
    li = (LayoutInflater)getContext().getSystemService(infService);
    li.inflate(R.layout.clearable_edit_text, this, true);

    // Get references to the child controls.
    editText = (EditText)findViewById(R.id.editText);
    clearButton = (Button)findViewById(R.id.clearButton);

    // Hook up the functionality
    hookupButton();
  }

  /*
   * Listing 5-10: Implementing the "Clear" Button
   */
  private void hookupButton() {
    clearButton.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View v) {
        editText.setText("");
      }
    });
  }
}