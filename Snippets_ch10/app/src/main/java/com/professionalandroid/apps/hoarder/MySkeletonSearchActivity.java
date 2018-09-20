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

package com.professionalandroid.apps.hoarder;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MySkeletonSearchActivity extends AppCompatActivity {

/*
 * Listing 10-31: Extracting the search query
 */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_search);

    // Parse the launch Intent to perform the search
    // and display the results.
    parseIntent();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    // If the search Activity exists, and another search
    // is performed, set the launch Intent to the newly
    // received search Intent and perform a new search.
    setIntent(intent);
    parseIntent();
  }

  private void parseIntent() {
    Intent searchIntent = getIntent();

    // If the Activity was started to service a Search request,
    // extract the search query.
    if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
      String searchQuery = searchIntent.getStringExtra(SearchManager.QUERY);

      // Perform the search
      performSearch(searchQuery);
    }
  }
  private void performSearch(String searchQuery) {
    // TODO: Perform the search and update the UI to display the results.
  }
}
