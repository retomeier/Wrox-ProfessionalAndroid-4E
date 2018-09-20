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

package com.professionalandroid.apps.earthquake;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class EarthquakeWidget extends AppWidgetProvider {

  public static final String NEW_QUAKE_BROADCAST = "com.paad.earthquake.NEW_QUAKE_BROADCAST";

  @Override
  public void onReceive(Context context, Intent intent){
    super.onReceive(context, intent);

    if (NEW_QUAKE_BROADCAST.equals(intent.getAction())) {
      PendingResult pendingResult = goAsync();

      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      ComponentName earthquakeWidget = new ComponentName(context, EarthquakeWidget.class);

      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeWidget);

      updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }
  }

  static void updateAppWidgets(final Context context,
                               final AppWidgetManager appWidgetManager,
                               final int[] appWidgetIds,
                               final PendingResult pendingResult) {
    Thread thread = new Thread() {
      public void run() {
        Earthquake lastEarthquake
          = EarthquakeDatabaseAccessor.getInstance(context)
              .earthquakeDAO().getLatestEarthquake();

        boolean lastEarthquakeExists = lastEarthquake != null;
        String lastMag = lastEarthquakeExists ?
                           String.valueOf(lastEarthquake.getMagnitude()) :
                           context.getString(R.string.widget_blank_magnitude);

        String details = lastEarthquakeExists ?
                           lastEarthquake.getDetails() :
                           context.getString(R.string.widget_blank_details);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quake_widget);
        views.setTextViewText(R.id.widget_magnitude, lastMag);
        views.setTextViewText(R.id.widget_details, details);

        // Create a Pending Intent that will open the main Activity.
        Intent intent = new Intent(context, EarthquakeMainActivity.class);
        PendingIntent pendingIntent =
          PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_magnitude,
          pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_details,
          pendingIntent);

        // Update all the added widgets
        for (int appWidgetId : appWidgetIds)
          appWidgetManager.updateAppWidget(appWidgetId, views);

        pendingResult.finish();
      }
    };
    thread.start();
  }

  @Override
  public void onUpdate(Context context,
                       AppWidgetManager appWidgetManager,
                       int[] appWidgetIds) {
    PendingResult pendingResult = goAsync();

    updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
  }

  @Override
  public void onEnabled(Context context) {
    final PendingResult pendingResult = goAsync();

    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    ComponentName earthquakeWidget = new ComponentName(context, EarthquakeWidget.class);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeWidget);

    updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
  }

}