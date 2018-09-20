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
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/*
 * Listing 19-16: Creating a Remote Views Factory
 * Listing 19-21: Filling in a Pending Intent template for each item displayed in your Collection View Widget
 */
class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private ArrayList<String> myWidgetText = new ArrayList<String>();
  private Context context;
  private Intent intent;
  private int widgetId;

  public MyRemoteViewsFactory(Context context, Intent intent) {
    // Optional constructor implementation.
    // Useful for getting references to the
    // Context of the calling widget
    this.context = context;
    this.intent = intent;

    widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
      AppWidgetManager.INVALID_APPWIDGET_ID);
  }

  // Set up any connections / cursors to your data source.
  // Heavy lifting, like downloading data should be
  // deferred to onDataSetChanged()or getViewAt().
  // Taking more than 20 seconds in this call will result
  // in an ANR.
  public void onCreate() {
    myWidgetText.add("The");
    myWidgetText.add("quick");
    myWidgetText.add("brown");
    myWidgetText.add("fox");
    myWidgetText.add("jumps");
    myWidgetText.add("over");
    myWidgetText.add("the");
    myWidgetText.add("lazy");
    myWidgetText.add("droid");
  }

  // Called when the underlying data collection being displayed is
  // modified. You can use the AppWidgetManager's
  // notifyAppWidgetViewDataChanged method to trigger this handler.
  public void onDataSetChanged() {
    // TODO Processing when underlying data has changed.
  }

  // Return the number of items in the collection being displayed.
  public int getCount() {
    return myWidgetText.size();
  }

  // Return true if the unique IDs provided by each item are stable --
  // that is, they don't change at run time.
  public boolean hasStableIds() {
    return false;
  }

  // Return the unique ID associated with the item at a given index.
  public long getItemId(int index) {
    return index;
  }

  // The number of different view definitions. Usually 1.
  public int getViewTypeCount() {
    return 1;
  }

  // Optionally specify a "loading" view to display before onDataSetChanged
  // has been called and returned. Return null to use the default.
  public RemoteViews getLoadingView() {
    return null;
  }

  // Create and populate the View to display at the given index.
  public RemoteViews getViewAt(int index) {
    // Create a view to display at the required index.
    RemoteViews rv = new RemoteViews(context.getPackageName(),
      R.layout.widget_collection_item_layout);

    // Populate the view from the underlying data.
    rv.setTextViewText(R.id.widget_title_text,
      myWidgetText.get(index));
    rv.setTextViewText(R.id.widget_text, "View Number: " +
                                           String.valueOf(index));

    // Listing 19-21: Filling in a Pending Intent template for each item displayed in your Collection View Widget
    // Create the item-specific fill-in Intent that will populate
    // the Pending Intent template created in the App Widget Provider.
    Intent fillInIntent = new Intent();
    fillInIntent.putExtra(Intent.EXTRA_TEXT, myWidgetText.get(index));
    rv.setOnClickFillInIntent(R.id.widget_title_text, fillInIntent);

    return rv;
  }

  // Close connections, cursors, or any other persistent state you
  // created in onCreate.
  public void onDestroy() {
    myWidgetText.clear();
  }
}