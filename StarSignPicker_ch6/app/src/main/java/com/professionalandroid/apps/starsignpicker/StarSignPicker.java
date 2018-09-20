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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class StarSignPicker extends AppCompatActivity {

  public static final String EXTRA_SIGN_NAME = "SIGN_NAME";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_star_sign_picker);

    StarSignPickerAdapter adapter = new StarSignPickerAdapter();
    adapter.setOnAdapterItemClick(
      new StarSignPickerAdapter.IAdapterItemClick() {
        @Override
        public void onItemClicked(String selectedItem) {
          // Construct the result URI.
          Intent outData = new Intent();
          outData.putExtra(EXTRA_SIGN_NAME, selectedItem);
          setResult(Activity.RESULT_OK, outData);
          finish();
        }
      });

    RecyclerView rv = findViewById(R.id.recycler_view);
    rv.setAdapter(adapter);
  }
}
