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

import java.util.List;

public class EarthquakeRecyclerViewAdapter extends
  RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {

  private final List<Earthquake> mEarthquakes;

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
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    holder.earthquake = mEarthquakes.get(position);
    holder.detailsView.setText(mEarthquakes.get(position).toString());
  }

  @Override
  public int getItemCount() {
    return mEarthquakes.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View parentView;
    public final TextView detailsView;
    public Earthquake earthquake;

    public ViewHolder(View view) {
      super(view);
      parentView = view;
      detailsView = (TextView)view.findViewById(R.id.list_item_earthquake_details);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + detailsView.getText() + "'";
    }
  }
}
