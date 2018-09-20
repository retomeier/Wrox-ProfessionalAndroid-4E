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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.security.Provider;

/*
 * Listing 19-3: App Widget implementation
 * Listing 19-5: Creating Remote Views
 * Listing 19-6: Applying a Remote View within the App Widget Provider’s update handler
 * Listing 19-7: Adding a Click Listener to an App Widget
 * Listing 19-10: Updating App Widgets based on broadcast Intents
 */
public class SkeletonAppWidget extends AppWidgetProvider {

  static void updateAppWidget(Context context,
                              AppWidgetManager appWidgetManager,
                              int appWidgetId) {
    // Listing 19-5
    // Listing 19-6
    RemoteViews views = new RemoteViews(context.getPackageName(),
      R.layout.my_widget_layout);

    // TODO Update the UI.

    // Listing 19-7
    // Create an Intent to launch an Activity
    Intent intent = new Intent(context, MainActivity.class);

    // Wrap it in a Pending Intent so another application
    // can fire it on your behalf.
    PendingIntent pendingIntent =
      PendingIntent.getActivity(context, 0, intent, 0);

    // Assign the Pending Intent to be triggered when
    // the assigned View is clicked.
    views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);

    // Notify the App Widget Manager to update the widget using
    // the modified remote view.
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  /*
  @Override
  public void onUpdate(Context context,
                       AppWidgetManager appWidgetManager,
                       int[] appWidgetIds) {
    // Iterate through each widget, creating a RemoteViews object and
    // applying the modified RemoteViews to each widget.
    for (int appWidgetId : appWidgetIds)
      updateAppWidget(context, appWidgetManager, appWidgetId);
  }
  */

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    // TODO Handle deletion of the widget.
  }

  @Override
  public void onDisabled(Context context) {
    super.onDisabled(context);
    // TODO Widget has been disabled.
  }

  /*
  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);

    // Call goAsync() to move updates onto a background thread.
    PendingResult pendingResult = null;

    // Listing 19-6
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    ComponentName skeletonAppWidget = new ComponentName(context, SkeletonAppWidget.class);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(skeletonAppWidget);

    updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
  }
  */

  /*
   * Listing 19-10: Updating App Widgets based on broadcast Intents
   * Listing 19-11: Updating an App Widget with asynchronously loaded data
   */
  public static String FORCE_WIDGET_UPDATE = "com.paad.mywidget.FORCE_WIDGET_UPDATE";

  @Override
  public void onReceive(final Context context, final Intent intent) {
    super.onReceive(context, intent);

    // Listing 19-11
    // Indicate an asynchronous operation will take place.
    final PendingResult pendingResult = goAsync();

    if (FORCE_WIDGET_UPDATE.equals(intent.getAction())) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      ComponentName skeletonAppWidget = new ComponentName(context, SkeletonAppWidget.class);
      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(skeletonAppWidget);

      updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }
  }

  static void updateAppWidgets(final Context context,
                               final AppWidgetManager appWidgetManager,
                               final int[] appWidgetIds,
                               final PendingResult pendingResult) {

    // Create a thread to asynchronously load data to show in the widgets.
    Thread thread = new Thread() {
      public void run() {
        // TODO Load data from a database.

        RemoteViews views = new RemoteViews(context.getPackageName(),
                                            R.layout.my_widget_layout);
        // TODO Update the UI.

        // Update all the added widgets
        for (int appWidgetId : appWidgetIds)
          appWidgetManager.updateAppWidget(appWidgetId, views);

        if (pendingResult != null)
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
    ComponentName skeletonAppWidget = new ComponentName(context, SkeletonAppWidget.class);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(skeletonAppWidget);

    updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
  }


}