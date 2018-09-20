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
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EarthquakeMapFragment extends Fragment implements OnMapReadyCallback {

  private int mMinimumMagnitude = 0;
  Map<String, Marker> mMarkers = new HashMap<>();
  List<Earthquake> mEarthquakes;
  private GoogleMap mMap;
  EarthquakeViewModel earthquakeViewModel;

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    // Retrieve the Earthquake View Model for this Fragment.
    earthquakeViewModel =
      ViewModelProviders.of(getActivity()).get(EarthquakeViewModel.class);

    // Get the data from the View Model, and observe any changes.
    earthquakeViewModel.getEarthquakes()
      .observe(this, new Observer<List<Earthquake>>() {
        @Override
        public void onChanged(@Nullable List<Earthquake> earthquakes) {
          // Update the UI with the updated database results.
          if (earthquakes != null)
            setEarthquakeMarkers(earthquakes);
        }
      });
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_earthquake_map,
      container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Obtain the SupportMapFragment and request the Google Map object.
    SupportMapFragment mapFragment
      = (SupportMapFragment)getChildFragmentManager()
                              .findFragmentById(R.id.map);

    mapFragment.getMapAsync(this);
  }

  private void updateFromPreferences() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

    mMinimumMagnitude = Integer.parseInt(
      prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
  }

  public void setEarthquakeMarkers(List<Earthquake> earthquakes) {
    updateFromPreferences();

    mEarthquakes = earthquakes;

    if (mMap == null || earthquakes == null) return;

    Map<String, Earthquake> newEarthquakes = new HashMap<>();

    // Add Markers for each earthquake above the user threshold.
    for (Earthquake earthquake : earthquakes) {
      if (earthquake.getMagnitude() >= mMinimumMagnitude) {
        newEarthquakes.put(earthquake.getId(), earthquake);
        if (!mMarkers.containsKey(earthquake.getId())) {
          Location location = earthquake.getLocation();
          Marker marker = mMap.addMarker(
            new MarkerOptions()
              .position(new LatLng(location.getLatitude(),
                location.getLongitude()))
              .title("M:" + earthquake.getMagnitude()));
          mMarkers.put(earthquake.getId(), marker);
        }
      }
    }

    // Remove any Markers representing earthquakes that should no longer
    // be displayed.
    for (Iterator<String> iterator = mMarkers.keySet().iterator();
         iterator.hasNext();) {
      String earthquakeID = iterator.next();
      if (!newEarthquakes.containsKey(earthquakeID)) {
        mMarkers.get(earthquakeID).remove();
        iterator.remove();
      }
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Register an OnSharedPreferenceChangeListener
    SharedPreferences prefs =
      PreferenceManager.getDefaultSharedPreferences(getContext());
    prefs.registerOnSharedPreferenceChangeListener(mPListener);
  }

  private SharedPreferences.OnSharedPreferenceChangeListener mPListener
    = new SharedPreferences.OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
      if (PreferencesActivity.PREF_MIN_MAG.equals(key)) {
        // Repopulate the Markers.
        List<Earthquake> earthquakes = earthquakeViewModel.getEarthquakes().getValue();
        if (earthquakes != null)
          setEarthquakeMarkers(earthquakes);
      }
    }
  };
}