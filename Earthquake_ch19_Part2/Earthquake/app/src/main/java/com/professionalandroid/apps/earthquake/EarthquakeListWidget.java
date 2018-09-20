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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class EarthquakeListWidget extends AppWidgetProvider {
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
    ComponentName earthquakeListWidget = new ComponentName(context, EarthquakeListWidget.class);

    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeListWidget);

    updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
  }

  static void updateAppWidgets(final Context context,
                               final AppWidgetManager appWidgetManager,
                               final int[] appWidgetIds,
                               final PendingResult pendingResult) {
    Thread thread = new Thread() {
      public void run() {
        for (int appWidgetId: appWidgetIds) {
          // Set up the intent that starts the Earthquake
          // Remote Views Service, which will supply the views
          // shown in the List View.
          Intent intent = new Intent(context, EarthquakeRemoteViewsService.class);

          // Add the app widget ID to the intent extras.
          intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

          // Instantiate the RemoteViews object for the App Widget layout.
          RemoteViews views = new RemoteViews(context.getPackageName(),
                                              R.layout.quake_collection_widget);

          // Set up the RemoteViews object to use a RemoteViews adapter.
          views.setRemoteAdapter(R.id.widget_list_view, intent);

          // The empty view is displayed when the collection has no items.
          views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_text);

          // Notify the App Widget Manager to update the widget using
          // the modified remote view.
          appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        if (pendingResult != null)
          pendingResult.finish();
      }
    };
    thread.start();
  }
}