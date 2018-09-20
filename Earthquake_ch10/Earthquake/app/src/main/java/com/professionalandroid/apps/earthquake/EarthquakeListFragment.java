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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeListFragment extends Fragment {

  public interface OnListFragmentInteractionListener {
    void onListFragmentRefreshRequested();
  }
  private OnListFragmentInteractionListener mListener;

  private ArrayList<Earthquake> mEarthquakes =
    new ArrayList<Earthquake>();

  private EarthquakeRecyclerViewAdapter mEarthquakeAdapter =
    new EarthquakeRecyclerViewAdapter(mEarthquakes);

  private RecyclerView mRecyclerView;
  private SwipeRefreshLayout mSwipeToRefreshView;

  protected EarthquakeViewModel earthquakeViewModel;

  private int mMinimumMagnitude = 0;

  public EarthquakeListFragment() {
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_earthquake_list,
      container, false);
    mRecyclerView = view.findViewById(R.id.list);
    mSwipeToRefreshView = view.findViewById(R.id.swiperefresh);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Set the Recycler View adapter
    Context context = view.getContext();
    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    mRecyclerView.setAdapter(mEarthquakeAdapter);

    // Setup the Swipe to Refresh view
    mSwipeToRefreshView.setOnRefreshListener(
      new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          updateEarthquakes();
        }
      });
  }

  protected void updateEarthquakes() {
    if (mListener != null)
      mListener.onListFragmentRefreshRequested();
  }

  public void setEarthquakes(List<Earthquake> earthquakes) {
    updateFromPreferences();

    mEarthquakes.clear();
    mEarthquakeAdapter.notifyDataSetChanged();

    for (Earthquake earthquake: earthquakes) {
      if (earthquake.getMagnitude() >= mMinimumMagnitude) {
        if (!mEarthquakes.contains(earthquake)) {
          mEarthquakes.add(earthquake);
          mEarthquakeAdapter
            .notifyItemInserted(mEarthquakes.indexOf(earthquake));
        }
      }
    }

    if (mEarthquakes != null && mEarthquakes.size() > 0)
      for (int i = mEarthquakes.size() - 1; i >= 0; i--) {
        if (mEarthquakes.get(i).getMagnitude() < mMinimumMagnitude) {
          mEarthquakes.remove(i);
          mEarthquakeAdapter.notifyItemRemoved(i);
        }
      }

    mSwipeToRefreshView.setRefreshing(false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Retrieve the Earthquake View Model for the parent Activity.
    earthquakeViewModel = ViewModelProviders.of(getActivity())
                            .get(EarthquakeViewModel.class);

    // Get the data from the View Model, and observe any changes.
    earthquakeViewModel.getEarthquakes()
      .observe(this, new Observer<List<Earthquake>>() {
        @Override
        public void onChanged(@Nullable List<Earthquake> earthquakes) {
          // When the View Model changes, update the List
          if (earthquakes != null)
            setEarthquakes(earthquakes);
        }
      });

    // Register an OnSharedPreferenceChangeListener
    SharedPreferences prefs =
      PreferenceManager.getDefaultSharedPreferences(getContext());
    prefs.registerOnSharedPreferenceChangeListener(mPrefListener);
  }

  private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener
    = new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences
                                              sharedPreferences,
                                            String key) {
        if (PreferencesActivity.PREF_MIN_MAG.equals(key)) {
          List<Earthquake> earthquakes
            = earthquakeViewModel.getEarthquakes().getValue();
          if (earthquakes != null)
            setEarthquakes(earthquakes);
        }
      }
  };

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mListener = (OnListFragmentInteractionListener) context;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  private void updateFromPreferences() {
    SharedPreferences prefs =
      PreferenceManager.getDefaultSharedPreferences(getContext());
    mMinimumMagnitude = Integer.parseInt(
      prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
  }
}