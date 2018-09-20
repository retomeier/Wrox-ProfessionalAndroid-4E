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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HoardDB {
  /*
   * Listing 9-15: Skeleton code for contract class constants
   */
  public static class HoardContract {
    // The index (key) column name for use in where clauses.
    public static final String KEY_ID = "_id";

    // The name and column index of each column in your database.
    // These should be descriptive.
    public static final String KEY_GOLD_HOARD_NAME_COLUMN =
      "GOLD_HOARD_NAME_COLUMN";
    public static final String KEY_GOLD_HOARD_ACCESSIBLE_COLUMN =
      "OLD_HOARD_ACCESSIBLE_COLUMN";
    public static final String KEY_GOLD_HOARDED_COLUMN =
      "GOLD_HOARDED_COLUMN";
  }

  /*
   * Listing 9-16: Implementing an SQLite Open Helper
   */
  public static class HoardDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myDatabase.db";
    public static final String DATABASE_TABLE = "GoldHoards";
    public static final int DATABASE_VERSION = 1;

    // SQL Statement to create a new database.
    private static final String DATABASE_CREATE =
      "create table " + DATABASE_TABLE + " (" + HoardContract.KEY_ID +
        " integer primary key autoincrement, " +
        HoardContract.KEY_GOLD_HOARD_NAME_COLUMN + " text not null, " +
        HoardContract.KEY_GOLD_HOARDED_COLUMN + " float, " +
        HoardContract.KEY_GOLD_HOARD_ACCESSIBLE_COLUMN + " integer);";

    public HoardDBOpenHelper(Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that
    // the version of the database on disk needs to be upgraded to
    // the current version.
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " +
                               oldVersion + " to " +
                               newVersion +
                               ", which will destroy all old data");

      // Upgrade the existing database to conform to the new
      // version. Multiple previous versions can be handled by
      // comparing oldVersion and newVersion values.
      // The simplest case is to drop the old table and create a new one.
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

      // Create a new one.
      onCreate(db);
    }
  }
}
