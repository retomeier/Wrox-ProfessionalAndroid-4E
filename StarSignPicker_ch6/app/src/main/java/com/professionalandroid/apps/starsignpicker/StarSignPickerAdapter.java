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

package com.professionalandroid.apps.starsignpicker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StarSignPickerAdapter extends RecyclerView.Adapter<StarSignPickerAdapter.ViewHolder> {
  private String[] mStarSigns = {"Aries", "Taurus", "Gemini", "Cancer",
    "Leo", "Virgo", "Libra", "Scorpio",
    "Sagittarius", "Capricorn", "Aquarius",
    "Pisces"};

  public StarSignPickerAdapter() {
  }

  @Override
  public int getItemCount() {
    return mStarSigns == null ? 0 : mStarSigns.length;
  }

  @NonNull
  @Override
  public StarSignPickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // Create the new View
    View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.list_item_layout, parent, false);

    return new ViewHolder(v, null);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
    holder.textView.setText(mStarSigns[position]);

    holder.mListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mAdapterItemClickListener != null)
          mAdapterItemClickListener.onItemClicked(mStarSigns[position]);
      }
    };
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textView;
    public View.OnClickListener mListener;

    public ViewHolder(View v, View.OnClickListener listener) {
      super(v);
      mListener = listener;
      textView = v.findViewById(R.id.itemTextView);
      v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (mListener != null)
        mListener.onClick(v);
    }
  }

  public interface IAdapterItemClick {
    void onItemClicked(String selectedItem);
  }

  IAdapterItemClick mAdapterItemClickListener;

  public void setOnAdapterItemClick(
    IAdapterItemClick adapterItemClickHandler) {
    mAdapterItemClickListener = adapterItemClickHandler;
  }
}
