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

package com.professionalandroid.apps.myapplication;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TabActivity extends AppCompatActivity {

  /*
   * Listing 13-16: Connecting the View Pager to a Tab Layout
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_bar_tabs);

    ViewPager viewPager = findViewById(R.id.view_pager);
    PagerAdapter pagerAdapter =
      new FixedTabsPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);

    TabLayout tabLayout = findViewById(R.id.tab_layout);
    tabLayout.setupWithViewPager(viewPager);
  }

  /*
   * Listing 13-15: Creating a Fragment Pager Adapter for a Tab Layout
   */
  class FixedTabsPagerAdapter extends FragmentPagerAdapter {
    public FixedTabsPagerAdapter(FragmentManager fm) {
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
          return new HomeFragment();
        case 1:
          return new ProfileFragment();
        default:
          return null;
      }
    }

    @Override
    public CharSequence getPageTitle(int position) {
      // To support internationalization, use string
      // resources for these titles
      switch(position) {
        case 0:
          return "Home";
        case 1:
          return "Profile";
        default:
          return null;
      }
    }
  }
}
