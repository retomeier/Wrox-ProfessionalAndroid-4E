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

import android.Manifest;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final String TAG = "CH10_SNIPPETS";
  private static final int CONTACTS_PERMISSION_REQUEST = 1;
  private static final int CALENDER_PERMISSION_REQUEST = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  private void listing10_10() {
    // Listing 10-10: Querying a Content Provider with the Content Resolver

    // Get the Content Resolver.
    ContentResolver cr = getContentResolver();

    // Specify the result column projection. Return the minimum set
    // of columns required to satisfy your requirements.
    String[] result_columns = new String[]{
      HoardDB.HoardContract.KEY_ID,
      HoardDB.HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN,
      HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN};

    // Specify the where clause that will limit your results.
    String where = HoardDB.HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN
                     + "=?";
    String[] whereArgs = {"1"};

    // Replace with valid SQL ordering statement as necessary.
    String order = null;

    // Return the specified rows.
    Cursor resultCursor = cr.query(MyHoardContentProvider.CONTENT_URI,
      result_columns, where, whereArgs, order);

    // Listing 10-12: Extracting values from a Content Provider result Cursor
    float largestHoard = 0f;
    String largestHoardName = "No Hoards";

    // Find the index to the column(s) being used.
    int GOLD_HOARDED_COLUMN_INDEX = resultCursor.getColumnIndexOrThrow(
      HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN);
    int HOARD_NAME_COLUMN_INDEX = resultCursor.getColumnIndexOrThrow(
      HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN);

    // Iterate over the cursors rows.
    // The Cursor is initialized at before first, so we can
    // check only if there is a "next" row available. If the
    // result Cursor is empty, this will return false.
    while (resultCursor.moveToNext()) {
      float hoard = resultCursor.getFloat(GOLD_HOARDED_COLUMN_INDEX);
      if (hoard > largestHoard) {
        largestHoard = hoard;
        largestHoardName = resultCursor.getString(HOARD_NAME_COLUMN_INDEX);
      }
    }

    // Close the Cursor when you've finished with it.
    resultCursor.close();
  }

  /*
   * Listing 10-11: Querying a Content Provider for a particular row
   */
  private Cursor queryRow(int rowId) {
    // Get the Content Resolver.
    ContentResolver cr = getContentResolver();

    // Specify the result column projection. Return the minimum set
    // of columns required to satisfy your requirements.
    String[] result_columns = new String[]{
      HoardDB.HoardContract.KEY_ID,
      HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN,
      HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN};

    // Append a row ID to the URI to address a specific row.
    Uri rowAddress =
      ContentUris.withAppendedId(MyHoardContentProvider.CONTENT_URI,
        rowId);

    // These are null as we are requesting a single row.
    String where = null;
    String[] whereArgs = null;
    String order = null;

    // Return the specified row.
    return cr.query(rowAddress, result_columns, where, whereArgs, order);
  }

  /*
   * Listing 10-13: Implementing Loader Callbacks
   */
  @NonNull
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // Construct the new query in the form of a Cursor Loader. Use the id
    // parameter to construct and return different loaders.
    String[] projection = null;
    String where = null;
    String[] whereArgs = null;
    String sortOrder = null;

    // Query URI
    Uri queryUri = MyHoardContentProvider.CONTENT_URI;

    // Create the new Cursor loader.
    return new CursorLoader(this, queryUri, projection,
      where, whereArgs, sortOrder);
  }

  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
    // You are now on the UI thread, update your UI with the loaded data.
    // Returns cached data automatically if initLoader is called after
    // a configuration change.
  }

  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    // Handle any cleanup necessary when Loader (or its parent)
    // is completely destroyed, for example the application being
    // terminated. Note that the Cursor Loader will close the
    // underlying result Cursor so you don't have to.
  }

  private void listing10_14(String newHoardName, int newHoardValue, boolean newHoardAccessible) {
    // Listing 10-14: Inserting new rows into a Content Provider

    // Create a new row of values to insert.
    ContentValues newValues = new ContentValues();

    // Assign values for each row.
    newValues.put(HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN,
      newHoardName);
    newValues.put(HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN,
      newHoardValue);
    newValues.put(HoardDB.HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN,
      newHoardAccessible);

    // Get the Content Resolver
    ContentResolver cr = getContentResolver();

    // Insert the row into your table
    Uri newRowUri = cr.insert(MyHoardContentProvider.CONTENT_URI, newValues);
  }

  private void listing10_15() {
    // Listing 10-15: Deleting rows from a Content Provider

    // Specify a where clause that determines which row(s) to delete.
    // Specify where arguments as necessary.
    String where = HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN + "=?";
    String[] whereArgs = {"0"};

    // Get the Content Resolver.
    ContentResolver cr = getContentResolver();

    // Delete the matching rows
    int deletedRowCount = cr.delete(MyHoardContentProvider.CONTENT_URI, where, whereArgs);
  }

  private void listing10_16(int hoardId, float newHoardValue) {
    // Listing 10-16: Updating a record in a Content Provider

    // Create a URI addressing a specific row.
    Uri rowURI =
      ContentUris.withAppendedId(MyHoardContentProvider.CONTENT_URI,
        hoardId);

    // Create the updated row content, assigning values for each row.
    ContentValues updatedValues = new ContentValues();
    updatedValues.put(HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN,
      newHoardValue);

    // [ ... Repeat for each column to update ... ]

    // If we specify a specific row, no selection clause is required.
    String where = null;
    String[] whereArgs = null;

    // Get the Content Resolver.
    ContentResolver cr = getContentResolver();

    // Update the specified row.
    int updatedRowCount =
      cr.update(rowURI, updatedValues, where, whereArgs);
  }

  /*
   * Listing 10-17: Reading and writing files from and to a Content Provider
   */
  public void addNewHoardWithImage(int rowId, Bitmap hoardImage) {
    // Create a URI addressing a specific row.
    Uri rowURI =
      ContentUris.withAppendedId(MyHoardContentProvider.CONTENT_URI, rowId);

    // Get the Content Resolver
    ContentResolver cr = getContentResolver();
    try {
      // Open an output stream using the row's URI.
      OutputStream outStream = cr.openOutputStream(rowURI);

      // Compress your bitmap and save it into your provider.
      hoardImage.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
    } catch (FileNotFoundException e) {
      Log.d(TAG, "No file found for this record.");
    }
  }

  public Bitmap getHoardImage(long rowId) {
    Uri myRowUri =
      ContentUris.withAppendedId(MyHoardContentProvider.CONTENT_URI, rowId);
    try {
      // Open an input stream using the new row's URI.
      InputStream inStream =
        getContentResolver().openInputStream(myRowUri);

      // Make a copy of the Bitmap.
      Bitmap bitmap = BitmapFactory.decodeStream(inStream);
      return bitmap;
    } catch (FileNotFoundException e) {
      Log.d(TAG, "No file found for this record.");
    }
    return null;
  }

  private void listing10_18() {
    // Call Log reading requires a runtime permission.
    if (ActivityCompat.checkSelfPermission(this,
      Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.READ_CONTACTS},
        CONTACTS_PERMISSION_REQUEST);
      return;
    }

    // Listing 10-18: Accessing the Call Log Content Provider
    // Create a projection that limits the result Cursor
    // to the required columns.
    String[] projection = {
      CallLog.Calls.DURATION,
      CallLog.Calls.NUMBER,
      CallLog.Calls.CACHED_NAME,
      CallLog.Calls.TYPE
    };

    // Return only outgoing calls.
    String where = CallLog.Calls.TYPE + "=?";
    String[] whereArgs = {String.valueOf(CallLog.Calls.OUTGOING_TYPE)};

    // Get a Cursor over the Call Log Calls Provider.
    Cursor cursor =
      getContentResolver().query(CallLog.Calls.CONTENT_URI,
        projection, where, whereArgs, null);

    // Get the index of the columns.
    int durIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION);
    int numberIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER);
    int nameIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME);

    // Initialize the result set.
    String[] result = new String[cursor.getCount()];

    // Iterate over the result Cursor.
    while (cursor.moveToNext()) {
      String durStr = cursor.getString(durIdx);
      String numberStr = cursor.getString(numberIdx);
      String nameStr = cursor.getString(nameIdx);
      result[cursor.getPosition()] = numberStr + " for " + durStr + "sec" +
                                       ((null == nameStr) ?
                                          "" : " (" + nameStr + ")");
      Log.d(TAG, result[cursor.getPosition()]);
    }

    // Close the Cursor.
    cursor.close();
  }

  private void listing10_19() {
    // Listing 10-19: Accessing the Media Store Content Provider

    // Get a Cursor over every piece of audio on the external volume,
    // extracting the song title and album name.
    String[] projection = new String[]{
      MediaStore.Audio.AudioColumns.ALBUM,
      MediaStore.Audio.AudioColumns.TITLE
    };

    Uri contentUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;

    Cursor cursor =
      getContentResolver().query(contentUri, projection,
        null, null, null);

    // Get the index of the columns we need.
    int albumIdx =
      cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM);
    int titleIdx =
      cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);

    // Create an array to store the result set.
    String[] result = new String[cursor.getCount()];

    // Iterate over the Cursor, extracting each album name and song title.
    while (cursor.moveToNext()) {

      // Extract the song title.
      String title = cursor.getString(titleIdx);

      // Extract the album name.
      String album = cursor.getString(albumIdx);
      result[cursor.getPosition()] = title + " (" + album + ")";
    }

    // Close the Cursor.
    cursor.close();
  }

  private void listing10_20() {
    // Listing 10-20: Accessing the Contacts Contract Contact Content Provider

    // Create a projection that limits the result Cursor
    // to the required columns.
    String[] projection = {
      ContactsContract.Contacts._ID,
      ContactsContract.Contacts.DISPLAY_NAME
    };

    // Get a Cursor over the Contacts Provider.
    Cursor cursor =
      getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
        projection, null, null, null);

    // Get the index of the columns.
    int nameIdx =
      cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
    int idIdx =
      cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);

    // Initialize the result set.
    String[] result = new String[cursor.getCount()];

    // Iterate over the result Cursor.
    while (cursor.moveToNext()) {

      // Extract the name.
      String name = cursor.getString(nameIdx);

      // Extract the unique ID.
      String id = cursor.getString(idIdx);
      result[cursor.getPosition()] = name + " (" + id + ")";
    }

    // Close the Cursor.
    cursor.close();
  }

  private void listing10_21() {
    // Listing 10-21: Finding contact details for a contact name
    ContentResolver cr = getContentResolver();
    String[] result = null;

    // Find a contact using a partial name match
    String searchName = "john";
    Uri lookupUri =
      Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, searchName);

    // Create a projection of the required column names.
    String[] projection = new String[]{
      ContactsContract.Contacts._ID
    };

    // Get a Cursor that will return the ID(s) of the matched name.
    Cursor idCursor = cr.query(lookupUri,
      projection, null, null, null);

    // Extract the first matching ID if it exists.
    String id = null;
    if (idCursor.moveToFirst()) {
      int idIdx = idCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
      id = idCursor.getString(idIdx);
    }

    // Close that Cursor.
    idCursor.close();

    // Create a new Cursor searching for the data associated
    // with the returned Contact ID.
    if (id != null) {
      // Return all the PHONE data for the contact.
      String where = ContactsContract.Data.CONTACT_ID +
                       " = " + id + " AND " +
                       ContactsContract.Data.MIMETYPE + " = '" +
                       ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +
                       "'";

      projection = new String[]{
        ContactsContract.Data.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
      };

      Cursor dataCursor =
        getContentResolver().query(ContactsContract.Data.CONTENT_URI,
          projection, where, null, null);

      // Get the indexes of the required columns.
      int nameIdx = dataCursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME);
      int phoneIdx = dataCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

      result = new String[dataCursor.getCount()];

      while (dataCursor.moveToNext()) {
        // Extract the name.
        String name = dataCursor.getString(nameIdx);

        // Extract the phone number.
        String number = dataCursor.getString(phoneIdx);
        result[dataCursor.getPosition()] = name + " (" + number + ")";
      }

      dataCursor.close();
    }
  }

  private void listing10_22() {
    // Listing 10-22: Performing a caller-ID lookup
    String incomingNumber = "(555) 123-4567";
    String result = "Not Found";

    Uri lookupUri =
      Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        incomingNumber);

    String[] projection = new String[]{
      ContactsContract.Contacts.DISPLAY_NAME
    };

    Cursor cursor = getContentResolver().query(lookupUri,
      projection, null, null, null);

    if (cursor.moveToFirst()) {
      int nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
      result = cursor.getString(nameIdx);
    }

    cursor.close();
  }

  /*
   * Listing 10-23: Picking a contact
   */
  private static int PICK_CONTACT = 0;

  private void pickContact() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
    startActivityForResult(intent, PICK_CONTACT);
  }

  private void listing10_24() {
    // Listing 10-24: Inserting a new contact using an Intent
    Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
      ContactsContract.Contacts.CONTENT_URI);

    intent.setData(Uri.parse("tel:(650)253-0000"));

    intent.putExtra(ContactsContract.Intents.Insert.COMPANY, "Google");
    intent.putExtra(ContactsContract.Intents.Insert.POSTAL,
      "1600 Amphitheatre Parkway, Mountain View, California");

    startActivity(intent);
  }

  private void listing10_25() {
    // Calendar reading requires a runtime permission.
    if (ActivityCompat.checkSelfPermission(this,
      Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.READ_CALENDAR},
        CALENDER_PERMISSION_REQUEST);
      return;
    }

    // Listing 10-25: Querying the Events table
    // Create a projection that limits the result Cursor
    // to the required columns.
    String[] projection = {
      CalendarContract.Events._ID,
      CalendarContract.Events.TITLE
    };

    // Get a Cursor over the Events Provider.
    Cursor cursor =
      getContentResolver().query(CalendarContract.Events.CONTENT_URI,
        projection, null, null, null);

    // Get the index of the columns.
    int nameIdx =
      cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE);
    int idIdx = cursor. getColumnIndexOrThrow(CalendarContract.Events._ID);

    // Initialize the result set.
    String[] result = new String[cursor.getCount()];

    // Iterate over the result Cursor.
    while(cursor.moveToNext()) {
      // Extract the name.
      String name = cursor.getString(nameIdx);

      // Extract the unique ID.
      String id = cursor.getString(idIdx);
      result[cursor.getPosition()] = name + " (" + id + ")";
    }

    // Close the Cursor.
    cursor.close();
  }

  private void listing10_26() {
    // Listing 10-26: Inserting a new calendar event using an Intent

    // Create a new insertion Intent.
    Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);

    // Add the calendar event details
    intent.putExtra(CalendarContract.Events.TITLE, "Book Launch!");
    intent.putExtra(CalendarContract.Events.DESCRIPTION, "Professional Android Release!");
    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Wrox.com");

    Calendar startTime = Calendar.getInstance();
    startTime.set(2018, 6, 19, 0, 30);
    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());

    // Use the Calendar app to add the new event.
    startActivity(intent);
  }

  private void listing10_27() {
    // Listing 10-27: Viewing a calendar event using an Intent

    // Create a URI addressing a specific event by its row ID.
    // Use it to create a new edit Intent.
    long rowID = 760;
    Uri uri = ContentUris.withAppendedId(
      CalendarContract.Events.CONTENT_URI, rowID);

    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

    // Use the Calendar app to view the calendar entry.
    startActivity(intent);
  }

  private void listing10_28() {
    // Listing 10-28: Displaying a time on the calendar using an Intent

    // Create a URI that specifies a particular time to view.
    Calendar startTime = Calendar.getInstance();
    startTime.set(2012, 2, 13, 0, 30);
    Uri uri = Uri.parse("content://com.android.calendar/time/" +
                          String.valueOf(startTime.getTimeInMillis()));
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

    // Use the Calendar app to view the time.
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the options menu from XML
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    // Use the Search Manager to find the SearchableInfo related
    // to this Activity.
    SearchManager searchManager =
      (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchableInfo searchableInfo =
      searchManager.getSearchableInfo(getComponentName());

    SearchView searchView =
      (SearchView) menu.findItem(R.id.search_view).getActionView();
    searchView.setSearchableInfo(searchableInfo);
    searchView.setIconifiedByDefault(false);
    return true;
  }
}
