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

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class AppBarSideNavActivity extends AppCompatActivity {

  private FragmentManager fm;
  /*
   * Listing 13-22 & 13-23: Connecting the App Bar and Navigation Drawer
   */
  private ActionBarDrawerToggle mDrawerToggle;
  private int mSelectedItem = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_bar_side_nav);

    // Ensure the navigation button is visible
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

    mDrawerToggle = new ActionBarDrawerToggle(this,
      drawerLayout,
      R.string.drawer_open_content_description,
      R.string.drawer_closed_content_description) {
      //  Listing 13-23
      @Override
      public void onDrawerClosed(View view) {
        // Create the newly selected item's Fragment
        Fragment newFragment;
        switch(mSelectedItem) {
          case R.id.nav_home:
            newFragment = new HomeFragment();
            getSupportActionBar().setTitle(R.string.nav_home);
            break;
          case R.id.nav_account:
            newFragment = new AccountFragment();
            getSupportActionBar().setTitle(R.string.nav_account);
            break;
          case R.id.nav_settings:
            newFragment = new SettingsFragment();
            getSupportActionBar().setTitle(R.string.nav_settings);
            break;
          case R.id.nav_about:
            newFragment = new AboutFragment();
            getSupportActionBar().setTitle(R.string.nav_about);
            break;
          default:
            return;
        }

        // Replace the current fragment with the newly selected item
        fm.beginTransaction()
          .replace(R.id.main_content, newFragment)
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
          .commit();

        // Reset the selected item
        mSelectedItem = 0;
      }
    };

    final NavigationView navigationView = findViewById(R.id.nav_view);

    navigationView.setNavigationItemSelectedListener(
      new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          mSelectedItem = item.getItemId();
          item.setChecked(true);
          drawerLayout.closeDrawer(navigationView);
          return true;
        }
      });
  }

  @Override
  public void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.syncState();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    // Follow with your own Menu Item selection logic
    return super.onOptionsItemSelected(item);
  }
}
