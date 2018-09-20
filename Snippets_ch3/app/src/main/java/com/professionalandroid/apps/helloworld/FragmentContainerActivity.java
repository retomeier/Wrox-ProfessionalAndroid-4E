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

package com.professionalandroid.apps.helloworld;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.professionalandroid.apps.DetailFragment;
import com.professionalandroid.apps.MyListFragment;

public class FragmentContainerActivity extends AppCompatActivity {

  /*
   * Listing 3-10: Populating Fragment layouts using container views
   */
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inflate the layout containing the Fragment containers
    setContentView(R.layout.fragment_container_layout);

    FragmentManager fragmentManager = getSupportFragmentManager();

    // Check to see if the Fragment containers have been populated
    // with Fragment instances. If not, create and populate the layout.
    DetailFragment detailsFragment =
      (DetailFragment) fragmentManager.findFragmentById(R.id.details_container);

    if (detailsFragment == null) {
      FragmentTransaction ft = fragmentManager.beginTransaction();
      ft.add(R.id.details_container, new DetailFragment());
      ft.add(R.id.list_container, new MyListFragment());
      ft.commitNow();
    }
  }
}
