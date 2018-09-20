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

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
  private static final int MENU_PREFERENCES = Menu.FIRST+1;
  private static final int SHOW_PREFERENCES = 1;


  EarthquakeListFragment mEarthquakeListFragment;
  EarthquakeViewModel earthquakeViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_earthquake_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ViewPager viewPager = findViewById(R.id.view_pager);
    if (viewPager != null) {
      PagerAdapter pagerAdapter =
        new EarthquakeTabsPagerAdapter(getSupportFragmentManager());
      viewPager.setAdapter(pagerAdapter);
      TabLayout tabLayout = findViewById(R.id.tab_layout);
      tabLayout.setupWithViewPager(viewPager);
    }

    // Retrieve the Earthquake View Model for this Activity.
    earthquakeViewModel = ViewModelProviders.of(this)
                            .get(EarthquakeViewModel.class);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    // Inflate the options menu from XML
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);

    // Use the Search Manager to find the SearchableInfo related
    // to the Search Result Activity.
    SearchManager searchManager =
      (SearchManager) getSystemService(Context.SEARCH_SERVICE);

    SearchableInfo searchableInfo = searchManager.getSearchableInfo(
      new ComponentName(getApplicationContext(),
        EarthquakeSearchResultActivity.class));

    SearchView searchView =
      (SearchView)menu.findItem(R.id.search_view).getActionView();
    searchView.setSearchableInfo(searchableInfo);
    searchView.setIconifiedByDefault(false);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {
      case R.id.settings_menu_item:
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, SHOW_PREFERENCES);
        return true;
    }

    return false;
  }

  class EarthquakeTabsPagerAdapter extends FragmentPagerAdapter {
    EarthquakeTabsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return 2;
    }

    @Override
    public Fragment getItem(int position) {
      switch(position) {
        case 0:
          return new EarthquakeListFragment();
        case 1:
          return new EarthquakeMapFragment();
        default:
          return null;
      }
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch(position) {
        case 0:
          return getString(R.string.tab_list);
        case 1:
          return getString(R.string.tab_map);
        default:
          return null;
      }
    }
  }
}
