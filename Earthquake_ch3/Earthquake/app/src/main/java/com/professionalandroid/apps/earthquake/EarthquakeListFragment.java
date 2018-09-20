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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeListFragment extends Fragment {

  private ArrayList<Earthquake> mEarthquakes =
    new ArrayList<Earthquake>();

  private EarthquakeRecyclerViewAdapter mEarthquakeAdapter =
    new EarthquakeRecyclerViewAdapter(mEarthquakes);

  private RecyclerView mRecyclerView;

  public EarthquakeListFragment() {
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_earthquake_list,
      container, false);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Set the Recycler View adapter
    Context context = view.getContext();
    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    mRecyclerView.setAdapter(mEarthquakeAdapter);
  }

  public void setEarthquakes(List<Earthquake> earthquakes) {
    for (Earthquake earthquake: earthquakes) {
      if (!mEarthquakes.contains(earthquake)) {
        mEarthquakes.add(earthquake);
        mEarthquakeAdapter
          .notifyItemInserted(mEarthquakes.indexOf(earthquake));
      }
    }
  }
}