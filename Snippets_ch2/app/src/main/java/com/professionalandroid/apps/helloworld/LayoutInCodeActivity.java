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

package com.professionalandroid.apps.helloworld;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LayoutInCodeActivity extends AppCompatActivity {

  /*
   * Listing 2-3: Creating layouts in code
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    RelativeLayout.LayoutParams lp;
    lp =
      new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                      LinearLayout.LayoutParams.MATCH_PARENT);

    RelativeLayout.LayoutParams textViewLP;
    textViewLP = new RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.WRAP_CONTENT,
      RelativeLayout.LayoutParams.WRAP_CONTENT);

    Resources res = getResources();
    int hpad = res.getDimensionPixelSize(R.dimen.activity_horizontal_margin);
    int vpad = res.getDimensionPixelSize(R.dimen.activity_vertical_margin);

    RelativeLayout rl = new RelativeLayout(this);
    rl.setPadding(hpad, vpad, hpad, vpad);

    TextView myTextView = new TextView(this);
    myTextView.setText("Hello World!");

    rl.addView(myTextView, textViewLP);

    addContentView(rl, lp);
  }
}
