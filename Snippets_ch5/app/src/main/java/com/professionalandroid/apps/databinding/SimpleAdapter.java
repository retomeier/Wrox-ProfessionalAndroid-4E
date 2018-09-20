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

package com.professionalandroid.apps.databinding;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * LISTING 5-4: Creating a Recycler View Adapter
 */

public class SimpleAdapter
       extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

  // Underlying data to be displayed.
  private String[] mData;

  // Set the initial data in the constructor
  public SimpleAdapter(String[] data) {
    mData = data;
  }

  // Tell the Layout Manager how many items exist in the data
  @Override
  public int getItemCount() {
    return mData == null ? 0 : mData.length;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public ViewHolder(View v) {
      super(v);
      // Only do findViewById once
      textView = v.findViewById(R.id.text);
    }
  }

  @NonNull
  @Override
  public SimpleAdapter.ViewHolder onCreateViewHolder(
    @NonNull ViewGroup parent, int viewType) {

    // Create the new View
    View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.simple_text, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
  }

  /*
   * Listing 5-5: Calculating the transitions between datasets
   */
  public void setData(final String[] newData) {
    // Store a copy of the previous data
    final String[] previousData = mData;

    // apply the new data
    mData = newData;

    // Calculate the differences between the old and new data
    DiffUtil.calculateDiff(new DiffUtil.Callback() {
      @Override
      public int getOldListSize() {
        return previousData != null ? previousData.length : 0;
      }

      @Override
      public int getNewListSize() {
        return newData != null ? previousData.length : 0;
      }

      @Override
      public boolean areItemsTheSame(int oldItemPosition,
                                     int newItemPosition) {
        // This method should compare the item's unique identifiers
        // if available. Returning true means the two items should be
        // crossfaded. In this example, we don't have an identifier,
        // so we'll compare the string values.
        return TextUtils.equals(previousData[oldItemPosition],
          newData[newItemPosition]);
      }

      @Override
      public boolean areContentsTheSame(int oldItemPosition,
                                        int newItemPosition) {
        // This method should do a deep inspection of the items to determine
        // if their visible contents are the same.
        // If they are the same, no animation is required.
        // In this example, if the items are the same,
        // the contents are the same
        return true;
      }
    }).dispatchUpdatesTo(this);
  }
}