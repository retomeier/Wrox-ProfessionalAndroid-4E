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
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.security.Provider;
import java.util.HashMap;

import static com.professionalandroid.apps.hoarder.HoardDB.HoardContract.KEY_ID;

/*
 * Listing 10-1: Creating a new Content Provider
 */
public class MyHoardContentProvider extends ContentProvider {

  /*
  // Listing 10-1 Skeleton methods are implemented in
  // Listings 10-6, 10-7, 10-8 below

  @Override
  public boolean onCreate() {
    return false;
  }

  @Override
  public Cursor query(@NonNull Uri uri,
                      @Nullable String[] projection,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs,
                      @Nullable String sortOrder) {
    // TODO: Perform a query and return Cursor.
    return null;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    // TODO: Return the mime-type of a query.
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    // TODO: Insert the Content Values and return a URI to the record.
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {
      // TODO: Delete the matching records and return the number of records deleted.
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri,
                    @Nullable ContentValues values,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {
      // TODO: Update the matching records with the provided
      // Content Values, returning the number of records updated.
    return 0;
  }
  /*

  /*
   * Listing 10-2: Creating the Content Provider’s database
   */
  private HoardDB.HoardDBOpenHelper mHoardDBOpenHelper;

  @Override
  public boolean onCreate() {
    // Construct the underlying database.
    // Defer opening the database until you need to perform
    // a query or transaction.
    mHoardDBOpenHelper =
      new HoardDB.HoardDBOpenHelper(getContext(),
        HoardDB.HoardDBOpenHelper.DATABASE_NAME,
        null,
        HoardDB.HoardDBOpenHelper.DATABASE_VERSION);
    return true;
  }

  // Listing 10-4: Publishing your Content Provider authority
  public static final Uri CONTENT_URI =
    Uri.parse("content://com.professionalandroid.provider.hoarder/lairs");

  /*
   * Listing 10-5: Defining a Uri Matcher
   * Listing 10-36: Detecting search suggestion requests in Content Providers
   */

  // Create the constants used to differentiate between the different URI
  // requests.
  private static final int ALLROWS = 1;
  private static final int SINGLE_ROW = 2;
  // Listing 10-36
  private static final int SEARCH = 3;

  private static final UriMatcher uriMatcher;

  // Populate the UriMatcher object, where a URI ending in
  // 'elements' will correspond to a request for all items,
  // and 'elements/[rowID]' represents a single row.
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI("com.professionalandroid.provider.hoarder",
      "lairs", ALLROWS);
    uriMatcher.addURI("com.professionalandroid.provider.hoarder",
      "lairs/#", SINGLE_ROW);

    // Listing 10-36
    uriMatcher.addURI("com.professionalandroid.provider.hoarder",
      SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
    uriMatcher.addURI("com.professionalandroid.provider.hoarder",
      SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
    uriMatcher.addURI("com.professionalandroid.provider.hoarder",
      SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
    uriMatcher.addURI("com.professionalandroid.provider.hoarder",
      SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);
  }

  /*
   * Listing 10-6: Implementing queries within a Content Provider
   * Listing 10-39: Returning search suggestions for a query
   */
  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri,
                      @Nullable String[] projection,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs,
                      @Nullable String sortOrder) {
    return query(uri, projection, selection, selectionArgs, sortOrder, null);
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri,
                      @Nullable String[] projection,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs,
                      @Nullable String sortOrder,
                      @Nullable CancellationSignal cancellationSignal) {
    // Open the database.
    SQLiteDatabase db;
    try {
      db = mHoardDBOpenHelper.getWritableDatabase();
    } catch (SQLiteException ex) {
      db = mHoardDBOpenHelper.getReadableDatabase();
    }

    // Replace these with valid SQL statements if necessary.
    String groupBy = null;
    String having = null;

    // Use an SQLite Query Builder to simplify constructing the
    // database query.
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    // If this is a row query, limit the result set to the passed in row.
    switch (uriMatcher.match(uri)) {
      case SINGLE_ROW :
        String rowID = uri.getLastPathSegment();
        queryBuilder.appendWhere(KEY_ID + "=" + rowID);
        break;

      // Listing 10-39
      case SEARCH :
        String query = uri.getLastPathSegment();
        queryBuilder.appendWhere(
          HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN +
            " LIKE \"%" + query + "%\"");
        queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
        break;
      default: break;
    }

    // Specify the table on which to perform the query. This can
    // be a specific table or a join as required.
    queryBuilder.setTables(HoardDB.HoardDBOpenHelper.DATABASE_TABLE);

    // Specify a limit to the number of returned results, if any.
    String limit = null;

    // Execute the query.
    Cursor cursor = queryBuilder.query(db, projection, selection,
      selectionArgs, groupBy, having, sortOrder, limit, cancellationSignal);

    // Return the result Cursor.
    return cursor;
  }

  /*
   * Listing 10-7: Returning a Content Provider MIME type
   * Listing 10-37: Returning the correct MIME type for search results
   */
  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    // Return a string that identifies the MIME type
    // for a Content Provider URI
    switch (uriMatcher.match(uri)) {
      case ALLROWS:
        return "vnd.android.cursor.dir/vnd.professionalandroid.lairs";
      case SINGLE_ROW:
        return "vnd.android.cursor.item/vnd.professionalandroid.lairs";
      // Listing 10-37
      case SEARCH :
        return SearchManager.SUGGEST_MIME_TYPE;
      default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
  }

  /*
   * Listing 10-8: Content Provider insert, update, and delete implementations
   */
  @Override
  public int delete(@NonNull Uri uri,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {

    // Open a read / write database to support the transaction.
    SQLiteDatabase db = mHoardDBOpenHelper.getWritableDatabase();

    // If this is a row URI, limit the deletion to the specified row.
    switch (uriMatcher.match(uri)) {
      case SINGLE_ROW :
        String rowID = uri.getLastPathSegment();
        selection = KEY_ID + "=" + rowID
                      + (!TextUtils.isEmpty(selection) ?
                           " AND (" + selection + ')' : "");
      default: break;
    }

    // To return the number of deleted items you must specify a where
    // clause. To delete all rows and return a value pass in "1".
    if (selection == null)
      selection = "1";

    // Perform the deletion.
    int deleteCount = db.delete(HoardDB.HoardDBOpenHelper.DATABASE_TABLE,
      selection, selectionArgs);

    // Notify any observers of the change in the data set.
    getContext().getContentResolver().notifyChange(uri, null);

    // Return the number of deleted items.
    return deleteCount;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

    // Open a read / write database to support the transaction.
    SQLiteDatabase db = mHoardDBOpenHelper.getWritableDatabase();

    // To add empty rows to your database by passing in an empty
    // Content Values object you must use the null column hack
    // parameter to specify the name of a column that can be
    // explicitly set to null.
    String nullColumnHack = null;

    // Insert the values into the table
    long id = db.insert(HoardDB.HoardDBOpenHelper.DATABASE_TABLE,
      nullColumnHack, values);

    // Construct and return the URI of the newly inserted row.
    if (id > -1) {
      // Construct and return the URI of the newly inserted row.
      Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);

      // Notify any observers of the change in the data set.
      getContext().getContentResolver().notifyChange(insertedId, null);
      return insertedId;
    }
    else
      return null;
  }

  @Override
  public int update(@NonNull Uri uri,
                    @Nullable ContentValues values,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {

    // Open a read / write database to support the transaction.
    SQLiteDatabase db = mHoardDBOpenHelper.getWritableDatabase();

    // If this is a row URI, limit the deletion to the specified row.
    switch (uriMatcher.match(uri)) {
      case SINGLE_ROW :
        String rowID = uri.getLastPathSegment();
        selection = KEY_ID + "=" + rowID
                      + (!TextUtils.isEmpty(selection) ?
                           " AND (" + selection + ')' : "");
      default: break;
    }

    // Perform the update.
    int updateCount = db.update(HoardDB.HoardDBOpenHelper.DATABASE_TABLE,
      values, selection, selectionArgs);

    // Notify any observers of the change in the data set.
    getContext().getContentResolver().notifyChange(uri, null);
    return updateCount;
  }

  /*
   * Listing 10-9: Returning files from a Content Provider
   */
  @Nullable
  @Override
  public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
    return openFileHelper(uri, mode);
  }

  /*
   * Listing 10-38: Creating a projection for returning search suggestions
   * Listing 10-41: Updating a search suggestion projection to include Intent data
   */
  private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;

  static {
    SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();

    // Map our ID column to "_id"
    SEARCH_SUGGEST_PROJECTION_MAP.put("_id",
      HoardDB.HoardContract.KEY_ID + " AS " + "_id");

    // Map our search field to the suggestions's first text field
    SEARCH_SUGGEST_PROJECTION_MAP.put(
      SearchManager.SUGGEST_COLUMN_TEXT_1,
      HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN +
        " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);

    // Listing 10-41
    // Map the ID column to the suggestion's data ID. This will be
    // combined with the base URI specified in our Searchable definition
    // to supply the data value for the selection Intent.
    SEARCH_SUGGEST_PROJECTION_MAP.put(
      SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
      KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
  }
}