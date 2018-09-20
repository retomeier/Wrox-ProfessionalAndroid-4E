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

import android.app.Application;
import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeSearchResultActivity extends AppCompatActivity {

  private ArrayList<Earthquake> mEarthquakes = new ArrayList< >();
  private EarthquakeRecyclerViewAdapter mEarthquakeAdapter = new EarthquakeRecyclerViewAdapter(mEarthquakes);
  private MutableLiveData<String> searchQuery;
  private LiveData<List<Earthquake>> searchResults;
  private MutableLiveData<String> selectedSearchSuggestionId;
  private LiveData<Earthquake> selectedSearchSuggestion;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_earthquake_search_result);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    RecyclerView recyclerView = findViewById(R.id.search_result_list);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(mEarthquakeAdapter);

    // Initialize the search query Live Data.
    searchQuery = new MutableLiveData<>();
    searchQuery.setValue(null);

    // Link the search query Live Data to the search results Live Data.
    // Configure Switch Map such that a change in the search query
    // updates the search results by querying the database.
    searchResults = Transformations.switchMap(searchQuery,
      query -> EarthquakeDatabaseAccessor
                 .getInstance(getApplicationContext())
                 .earthquakeDAO()
                 .searchEarthquakes("%" + query + "%"));

    // Observe changes to the search results Live Data.
    searchResults.observe(EarthquakeSearchResultActivity.this,
      searchQueryResultObserver);

    // Initialize the selected search suggestion Id Live Data.
    selectedSearchSuggestionId = new MutableLiveData<>();
    selectedSearchSuggestionId.setValue(null);

    // Link the selected search suggestion ID Live Data to the
    // selected search suggestion Live Data.
    // Configure Switch Map such that a change in the ID of the
    // selected search suggestion, updates the Live Data that
    // returns the corresponding Earthquake by querying the database.
    selectedSearchSuggestion =
      Transformations.switchMap(selectedSearchSuggestionId,
        id -> EarthquakeDatabaseAccessor
                .getInstance(getApplicationContext())
                .earthquakeDAO()
                .getEarthquake(id));

    // If the Activity was launched by a search suggestion
    if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
      selectedSearchSuggestion.observe(this,
        selectedSearchSuggestionObserver);
      setSelectedSearchSuggestion(getIntent().getData());
    }
    else {
      // If the Activity was launched from a search query,
      // extract the search query term and update the search query
      // Live Data.
      String query = getIntent().getStringExtra(SearchManager.QUERY);
      setSearchQuery(query);
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    // If the search Activity exists, and another search
    // is performed, set the launch Intent to the newly
    // received search Intent.
    setIntent(intent);

    if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
      // Update the selected search suggestion Id.
      setSelectedSearchSuggestion(getIntent().getData());
    }
    else {
      // Extract the search query and update the searchQuery Live Data.
      String query = getIntent().getStringExtra(SearchManager.QUERY);
      setSearchQuery(query);
    }
  }

  private void setSearchQuery(String query) {
    searchQuery.setValue(query);
  }

  private final Observer<List<Earthquake>> searchQueryResultObserver
    = updatedEarthquakes -> {
    // Update the UI with the updated search query results.
    mEarthquakes.clear();

    if (updatedEarthquakes != null)
      mEarthquakes.addAll(updatedEarthquakes);

    mEarthquakeAdapter.notifyDataSetChanged();
  };

  private void setSelectedSearchSuggestion(Uri dataString) {
    String id = dataString.getPathSegments().get(1);
    selectedSearchSuggestionId.setValue(id);
  }

  final Observer<Earthquake> selectedSearchSuggestionObserver
    = selectedSearchSuggestion -> {
    // Update the search query to match the selected search suggestion.
    if (selectedSearchSuggestion != null) {
      setSearchQuery(selectedSearchSuggestion.getDetails());
    }
  };
}