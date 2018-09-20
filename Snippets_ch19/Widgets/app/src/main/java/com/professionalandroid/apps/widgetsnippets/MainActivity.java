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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

  private static final String DESTINATION_EXTRA = "DESTINATION_EXTRA";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  private void listing19_10() {
    // Listing 19-9: Sending a Broadcast Intent to an App Widget
    Intent forceWidgetUpdate = new Intent(this, SkeletonAppWidget.class);
    forceWidgetUpdate.setAction(SkeletonAppWidget.FORCE_WIDGET_UPDATE);
    sendBroadcast(forceWidgetUpdate);
  }

  @RequiresApi(api = Build.VERSION_CODES.N_MR1)
  private void listing19_28(String destination) {
    // Listing 19-28: Creating and adding dynamic App Shortcuts
    ShortcutManager shortcutManager = (ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);

    Intent navIntent = new Intent(this, MainActivity.class);
    navIntent.setAction(Intent.ACTION_VIEW);
    navIntent.putExtra(DESTINATION_EXTRA, destination);

    String id = "dynamicDest" + destination;
    ShortcutInfo shortcut =
      new ShortcutInfo.Builder(this, id)
        .setShortLabel(destination)
        .setLongLabel("Navigate to " + destination)
        .setDisabledMessage("Navigation Shortcut Disabled")
        .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
        .setIntent(navIntent)
        .build();

    shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
  }
}
