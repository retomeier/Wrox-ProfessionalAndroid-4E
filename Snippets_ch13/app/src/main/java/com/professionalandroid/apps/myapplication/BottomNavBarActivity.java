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

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class BottomNavBarActivity extends AppCompatActivity {

  FragmentManager fm = getSupportFragmentManager();

  /*
   * Listing 13-19: Handling bottom navigation Item selection events
   */
  private static final String CURRENT_ITEM_KEY = "current_item";

  // This should be saved in onSaveInstanceState() using CURRENT_ITEM_KEY
  int mCurrentItem = R.id.nav_home;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_bar_bottom_nav);

    // Restore the ID of the current tab
    if (savedInstanceState != null) {
      mCurrentItem = savedInstanceState.getInt(CURRENT_ITEM_KEY);
    }

    BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
    bottomNav.setOnNavigationItemSelectedListener(
      new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
          FragmentManager fm = getSupportFragmentManager();

          // Create the newly selected item's Fragment
          Fragment newFragment = null;
          switch (item.getItemId()) {
            case R.id.nav_home:
              newFragment = new HomeFragment();
              getSupportActionBar().setTitle(R.string.nav_home);
              break;
            case R.id.nav_profile:
              newFragment = new ProfileFragment();
              getSupportActionBar().setTitle(R.string.nav_profile);
              break;
            case R.id.nav_notifications:
              newFragment = new NotificationsFragment();
              getSupportActionBar().setTitle(R.string.nav_notifications);
              break;
            default:
              break;
          }

          // Replace the current fragment with the newly selected item
          fm.beginTransaction()
            .replace(R.id.main_content, newFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit();

          return true;
        }
      });

    bottomNav.setOnNavigationItemReselectedListener(
      new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(MenuItem item) {
          // Scroll to the top of the current tab if it supports scrolling
          // This can be done in many ways: this code assumes all Fragments
          // implement a ScrollableFragment subclass you've created
          ScrollableFragment fragment = (ScrollableFragment) fm.findFragmentById(R.id.main_content);
          fragment.scrollToTop();
        }
      });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(CURRENT_ITEM_KEY, mCurrentItem);
  }

}
