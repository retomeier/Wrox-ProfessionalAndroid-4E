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

package com.professionalandroid.apps.starsignpicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StarSignPickerTester extends AppCompatActivity {

  public static final int PICK_STARSIGN = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_star_sign_picker_tester);

    Button button = findViewById(R.id.pick_starsign_button);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View _view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                                   Uri.parse("starsigns://"));
        startActivityForResult(intent, PICK_STARSIGN);
      }
    });
  }

  @Override
  public void onActivityResult(int reqCode, int resCode, Intent data) {
    super.onActivityResult(reqCode, resCode, data);

    switch(reqCode) {
      case (PICK_STARSIGN) : {
        if (resCode == Activity.RESULT_OK) {
          String selectedSign =
            data.getStringExtra(StarSignPicker.EXTRA_SIGN_NAME);
          TextView tv = findViewById(R.id.selected_starsign_textview);
          tv.setText(selectedSign);
        }
        break;
      }
      default: break;
    }
  }
}