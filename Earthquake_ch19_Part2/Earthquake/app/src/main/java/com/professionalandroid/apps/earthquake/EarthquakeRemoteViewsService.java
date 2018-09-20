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
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class EarthquakeRemoteViewsService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new EarthquakeRemoteViewsFactory(this);
  }

  class EarthquakeRemoteViewsFactory implements RemoteViewsFactory {

    private Context mContext;
    private List<Earthquake> mEarthquakes;

    public EarthquakeRemoteViewsFactory(Context context) {
      mContext = context;
    }

    public void onCreate() {
    }

    public void onDataSetChanged() {
      mEarthquakes = EarthquakeDatabaseAccessor.getInstance(mContext)
                       .earthquakeDAO().loadAllEarthquakesBlocking();
    }

    public int getCount() {
      if (mEarthquakes == null) return 0;
      return mEarthquakes.size();
    }
    public long getItemId(int index) {
      if (mEarthquakes == null) return index;
      return mEarthquakes.get(index).getDate().getTime();
    }

    public RemoteViews getViewAt(int index) {
      if (mEarthquakes != null) {
        // Extract the requested Earthquake.
        Earthquake earthquake = mEarthquakes.get(index);

        // Extract the values to be displayed.
        String id = earthquake.getId();
        String magnitude = String.valueOf(earthquake.getMagnitude());
        String details = earthquake.getDetails();

        // Create a new Remote Views object and use it to populate the
        // layout used to represent each earthquake in the list.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
          R.layout.quake_widget);
        rv.setTextViewText(R.id.widget_magnitude, magnitude);
        rv.setTextViewText(R.id.widget_details, details);

        // Create a Pending Intent that will open the main Activity.
        Intent intent = new Intent(mContext, EarthquakeMainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        rv.setOnClickPendingIntent(R.id.widget_magnitude, pendingIntent);
        rv.setOnClickPendingIntent(R.id.widget_details, pendingIntent);

        return rv;
      } else {
        return null;
      }
    }

    public int getViewTypeCount() {
      return 1;
    }

    public boolean hasStableIds() {
      return true;
    }

    public RemoteViews getLoadingView() {
      return null;
    }

    public void onDestroy() {
    }
  }
}