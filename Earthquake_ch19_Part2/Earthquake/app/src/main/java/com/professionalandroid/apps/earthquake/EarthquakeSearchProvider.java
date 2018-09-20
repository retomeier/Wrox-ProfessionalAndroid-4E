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
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class EarthquakeSearchProvider extends ContentProvider {

  private static final int SEARCH_SUGGESTIONS = 1;

  // Allocate the UriMatcher object, recognize search requests.
  private static final UriMatcher uriMatcher;
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI("com.professionalandroid.provider.earthquake",
      SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGESTIONS);
    uriMatcher.addURI("com.professionalandroid.provider.earthquake",
      SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGESTIONS);
    uriMatcher.addURI("com.professionalandroid.provider.earthquake",
      SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH_SUGGESTIONS);
    uriMatcher.addURI("com.professionalandroid.provider.earthquake",
      SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH_SUGGESTIONS);
  }

  @Override
  public boolean onCreate() {
    EarthquakeDatabaseAccessor
      .getInstance(getContext().getApplicationContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs,
                      @Nullable String sortOrder) {
    if (uriMatcher.match(uri) == SEARCH_SUGGESTIONS) {
      String searchQuery = "%" + uri.getLastPathSegment() + "%";
      EarthquakeDAO earthquakeDAO
        = EarthquakeDatabaseAccessor
            .getInstance(getContext().getApplicationContext())
            .earthquakeDAO();
      Cursor c = earthquakeDAO.generateSearchSuggestions(searchQuery);

      // Return a cursor of search suggestions.
      return c;
    }
    return null;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    switch (uriMatcher.match(uri)) {
      case SEARCH_SUGGESTIONS:
        return SearchManager.SUGGEST_MIME_TYPE;
      default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection,
                    @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {
    return 0;
  }
}