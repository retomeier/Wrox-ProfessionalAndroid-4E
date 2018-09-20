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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRecyclerViewAdapter extends
  RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {

  private final List<Earthquake> mEarthquakes;

  private static final SimpleDateFormat TIME_FORMAT =
    new SimpleDateFormat("HH:mm", Locale.US);
  private static final NumberFormat MAGNITUDE_FORMAT =
    new DecimalFormat("0.0");

  public EarthquakeRecyclerViewAdapter(List<Earthquake> earthquakes ) {
    mEarthquakes = earthquakes;
  }

  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.list_item_earthquake,
                           parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Earthquake earthquake = mEarthquakes.get(position);
    holder.date.setText(TIME_FORMAT.format(earthquake.getDate()));
    holder.details.setText(earthquake.getDetails());
    holder.magnitude.setText(
      MAGNITUDE_FORMAT.format(earthquake.getMagnitude()));
  }

  @Override
  public int getItemCount() {
    return mEarthquakes.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public final TextView date;
    public final TextView details;
    public final TextView magnitude;
    public ViewHolder(View view) {
      super(view);
      date = (TextView) view.findViewById(R.id.date);
      details = (TextView) view.findViewById(R.id.details);
      magnitude = (TextView) view.findViewById(R.id.magnitude);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + details.getText() + "'";
    }
  }
}
