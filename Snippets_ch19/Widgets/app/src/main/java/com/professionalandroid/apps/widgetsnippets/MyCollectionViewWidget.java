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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class MyCollectionViewWidget extends AppWidgetProvider {

  /*
   * Listing 19-19: Binding a Remove Views Service to a Widget
   * Listing 19-20: Adding a Click Listener to individual items within a Collection View Widget using a Pending Intent
   */
  static void updateAppWidget(Context context,
                              AppWidgetManager appWidgetManager,
                              int appWidgetId) {

    // Create a Remote View.
    RemoteViews views = new RemoteViews(context.getPackageName(),
      R.layout.widget_collection_layout);

    // Listing 19-20: Adding a Click Listener to individual items
    // within a Collection View Widget using a Pending Intent
    Intent templateIntent = new Intent(context, MainActivity.class);
    templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

    PendingIntent templatePendingIntent = PendingIntent.getActivity(
      context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    views.setPendingIntentTemplate(R.id.widget_stack_view,
      templatePendingIntent);

    // Bind this widget to a Remote Views Service.
    Intent intent = new Intent(context, MyRemoteViewsService.class);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    views.setRemoteAdapter(R.id.widget_stack_view, intent);

    // Specify a View within the Widget layout hierarchy to display
    // when the bound collection is empty.
    views.setEmptyView(R.id.widget_stack_view, R.id.widget_empty_text);

    // TODO Customize this Widgets UI based on configuration

    // settings etc.
    // Notify the App Widget Manager to update the widget using
    // the modified remote view.
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  //
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }
}
