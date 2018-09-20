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

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EarthquakeMainActivity extends AppCompatActivity
                                    implements EarthquakeListFragment.OnListFragmentInteractionListener {

  @Override
  public void onListFragmentRefreshRequested() {
    updateEarthquakes();
  }

  private void updateEarthquakes() {
    // Request the View Model update the earthquakes from the USGS feed.
    earthquakeViewModel.loadEarthquakes();
  }

  private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

  EarthquakeListFragment mEarthquakeListFragment;
  EarthquakeViewModel earthquakeViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_earthquake_main);

    FragmentManager fm = getSupportFragmentManager();

    // Android will automatically re-add any Fragments that
    // have previously been added after a configuration change,
    // so only add it if this isn't an automatic restart.
    if (savedInstanceState == null) {
      FragmentTransaction ft = fm.beginTransaction();

      mEarthquakeListFragment = new EarthquakeListFragment();
      ft.add(R.id.main_activity_frame,
        mEarthquakeListFragment, TAG_LIST_FRAGMENT);

      ft.commitNow();
    } else {
      mEarthquakeListFragment =
        (EarthquakeListFragment) fm.findFragmentByTag(TAG_LIST_FRAGMENT);
    }

    // Retrieve the Earthquake View Model for this Activity.
    earthquakeViewModel = ViewModelProviders.of(this)
                            .get(EarthquakeViewModel.class);
  }
}
