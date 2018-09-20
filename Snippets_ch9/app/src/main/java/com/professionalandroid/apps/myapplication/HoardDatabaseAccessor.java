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

import android.arch.persistence.room.Room;
import android.content.Context;

/*
 * Listing 9-11: Creating a Room Database access singleton
 */
public class HoardDatabaseAccessor {
  private static HoardDatabase HoardDatabaseInstance;
  private static final String HOARD_DB_NAME = "hoard_db";

  private HoardDatabaseAccessor() { }

  public static HoardDatabase getInstance(Context context) {
    if (HoardDatabaseInstance == null) {
      // Create or open a new SQLite database, and return it as
      // a Room Database instance.
      HoardDatabaseInstance = Room.databaseBuilder(context,
        HoardDatabase.class, HOARD_DB_NAME).build();
    }
    return HoardDatabaseInstance;
  }
}