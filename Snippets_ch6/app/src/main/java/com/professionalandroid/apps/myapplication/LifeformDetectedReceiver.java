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

package com.professionalandroid.apps.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/*
 * Listing 6-16: Implementing a Broadcast Receiver
 */
public class LifeformDetectedReceiver extends BroadcastReceiver {
  public static final String NEW_LIFEFORM_ACTION
    = "com.professionalandroid.alien.action.NEW_LIFEFORM_ACTION";
  public static final String EXTRA_LIFEFORM_NAME
    = "EXTRA_LIFEFORM_NAME";

  public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
  public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";
  public static final String FACE_HUGGER = "facehugger";

  private static final int NOTIFICATION_ID = 1;

  @Override
  public void onReceive(Context context, Intent intent) {
    // Get the lifeform details from the intent.
    String type = intent.getStringExtra(EXTRA_LIFEFORM_NAME);
    double lat = intent.getDoubleExtra(EXTRA_LATITUDE, Double.NaN);
    double lng = intent.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN);

    if (type.equals(FACE_HUGGER)) {
      NotificationManagerCompat notificationManager =
        NotificationManagerCompat.from(context);

      NotificationCompat.Builder builder =
        new NotificationCompat.Builder(context);

      builder.setSmallIcon(R.drawable.ic_alien)
        .setContentTitle("Face Hugger Detected")
        .setContentText(Double.isNaN(lat) || Double.isNaN(lng) ?
                          "Location Unknown" :
                          "Located at " + lat + "," + lng);

      notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
  }
}