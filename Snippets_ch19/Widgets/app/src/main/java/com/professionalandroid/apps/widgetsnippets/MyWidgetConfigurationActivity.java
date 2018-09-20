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

package com.professionalandroid.apps.widgetsnippets;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MyWidgetConfigurationActivity extends AppCompatActivity {

  /*
   * Listing 19-13: Skeleton App Widget configuration Activity
   */
  private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_widget_configuration);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();

    if (extras != null) {
      appWidgetId = extras.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // Set the result to canceled in case the user exits
    // the Activity without accepting the configuration
    // changes / settings. The widget will not be placed.
    setResult(RESULT_CANCELED, null);
  }

  private void completedConfiguration() {
    // Save the configuration settings for the Widget ID
    // Notify the Widget Manager that the configuration has completed.
    Intent result = new Intent();
    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

    setResult(RESULT_OK, result);
    finish();
  }
}
