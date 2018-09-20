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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  /*
   * Listing 9-14: Observing a Room query Live Data result
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // The observer, which will be triggered when the Live Data changes
    final Observer<List<Hoard>> hoardObserver = new Observer<List<Hoard>>() {
      @Override
      public void onChanged(@Nullable final List<Hoard> updatedHoard) {
        // Update the UI with the updated database results.
      }
    };

    // Observe the LiveData
    LiveData<List<Hoard>> hoardLiveData =
      HoardDatabaseAccessor.getInstance(getApplicationContext())
        .hoardDAO().monitorAllHoards();

    hoardLiveData.observe(this, hoardObserver);
  }

  // Must be executed on a background thread.
  private void listing9_12() {
    // Listing 9-12: Performing database interactions with Room

    // Access the Hoard Database instance.
    HoardDatabase hoardDb =
      HoardDatabaseAccessor.getInstance(getApplicationContext());

    // Add new hoards to the database.
    hoardDb.hoardDAO().insertHoard(new Hoard("Smegol", 1, true));
    hoardDb.hoardDAO().insertHoard(new Hoard("Smaug", 200000, false));

    // Query the database.
    int totalGold = hoardDb.hoardDAO().totalGoldHoarded();
    List<Hoard> allHoards = hoardDb.hoardDAO().loadAllHoards();
  }

  private void listing9_17() {
    // Listing 9-17: Opening a database using the SQLite Open Helper
    HoardDBOpenHelper hoardDBOpenHelper = new HoardDBOpenHelper(this,
      HoardDBOpenHelper.DATABASE_NAME, null,
      HoardDBOpenHelper.DATABASE_VERSION);

    SQLiteDatabase db;
    try {
      db = hoardDBOpenHelper.getWritableDatabase();
    } catch (SQLiteException ex) {
      db = hoardDBOpenHelper.getReadableDatabase();
    }
  }

  private void listing9_18(String newHoardName, int newHoardValue, boolean newHoardAccessible) {
    HoardDBOpenHelper hoardDBOpenHelper = new HoardDBOpenHelper(this,
      HoardDBOpenHelper.DATABASE_NAME, null,
      HoardDBOpenHelper.DATABASE_VERSION);

    // Listing 9-18: Inserting a new row into an SQLite database
    // Create a new row of values to insert.
    ContentValues newValues = new ContentValues();

    // Assign values for each row.
    newValues.put(HoardContract.KEY_GOLD_HOARD_NAME_COLUMN, newHoardName);
    newValues.put(HoardContract.KEY_GOLD_HOARDED_COLUMN, newHoardValue);
    newValues.put(HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN, newHoardAccessible);

    // [ ... Repeat for each column / value pair ... ]

    // Insert the row into your table
    SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
    db.insert(HoardDBOpenHelper.DATABASE_TABLE, null, newValues);
  }

  private void listing9_19(int newHoardValue, String hoardId) {
    HoardDBOpenHelper hoardDBOpenHelper = new HoardDBOpenHelper(this,
      HoardDBOpenHelper.DATABASE_NAME, null,
      HoardDBOpenHelper.DATABASE_VERSION);

    // Listing 9-19: Updating a database row
    // Create the updated row Content Values.
    ContentValues updatedValues = new ContentValues();

    // Assign values for each row.
    updatedValues.put(HoardContract.KEY_GOLD_HOARDED_COLUMN, newHoardValue);

    // [ ... Repeat for each column to update ... ]

    // Specify a where clause that defines which rows should be
    // updated. Specify where arguments as necessary.
    String where = HoardContract.KEY_ID + "=?";
    String whereArgs[] = {hoardId};

    // Update the row with the specified index with the new values.
    SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
    db.update(HoardDBOpenHelper.DATABASE_TABLE, updatedValues,
      where, whereArgs);
  }

  private void listing9_20(String hoardId) {
    HoardDBOpenHelper hoardDBOpenHelper = new HoardDBOpenHelper(this,
      HoardDBOpenHelper.DATABASE_NAME, null,
      HoardDBOpenHelper.DATABASE_VERSION);

    // Listing 9-20: Deleting a database row
    // Specify a where clause that determines which row(s) to delete.
    // Specify where arguments as necessary.
    String where = HoardContract.KEY_ID + "=?";
    String whereArgs[] = {hoardId};

    // Delete the rows that match the where clause.
    SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
    db.delete(HoardDBOpenHelper.DATABASE_TABLE, where, whereArgs);
  }

  private void listing9_21() {
    // Listing 9-21: Querying a database
    HoardDBOpenHelper hoardDBOpenHelper =
      new HoardDBOpenHelper(this,
        HoardDBOpenHelper.DATABASE_NAME, null,
        HoardDBOpenHelper.DATABASE_VERSION);

    // Specify the result column projection. Return the minimum set
    // of columns required to satisfy your requirements.
    String[] result_columns = new String[] {
      HoardContract.KEY_ID,
      HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN,
      HoardContract.KEY_GOLD_HOARDED_COLUMN };

    // Specify the where clause that will limit our results.
    String where = HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN + "=?";
    String whereArgs[] = {"1"};

    // Replace these with valid SQL statements as necessary.
    String groupBy = null;
    String having = null;

    // Return in ascending order of gold hoarded.
    String order = HoardContract.KEY_GOLD_HOARDED_COLUMN + " ASC";
    SQLiteDatabase db = hoardDBOpenHelper.getWritableDatabase();
    Cursor cursor = db.query(HoardDBOpenHelper.DATABASE_TABLE,
      result_columns, where, whereArgs, groupBy, having, order);

    // Listing 9-22: Extracting values from a Cursor
    float totalHoard = 0f;
    float averageHoard = 0f;

    // Find the index to the column(s) being used.
    int GOLD_HOARDED_COLUMN_INDEX =
      cursor.getColumnIndexOrThrow(HoardContract.KEY_GOLD_HOARDED_COLUMN);

    // Find the total number of rows.
    int cursorCount = cursor.getCount();

    // Iterate over the cursors rows.
    // The Cursor is initialized at before first, so we can
    // check only if there is a "next" row available. If the
    // result Cursor is empty this will return false.
    while (cursor.moveToNext())
      totalHoard += cursor.getFloat(GOLD_HOARDED_COLUMN_INDEX);

    // Calculate an average -- checking for divide by zero errors.
    averageHoard = cursor.getCount() > 0 ?
                     (totalHoard / cursorCount) : Float.NaN;

    // Close the Cursor when you've finished with it.
    cursor.close();
  }
}
