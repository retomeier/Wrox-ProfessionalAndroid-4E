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

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao
public interface EarthquakeDAO {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertEarthquakes(List<Earthquake> earthquakes);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertEarthquake(Earthquake earthquake);

  @Delete
  public void deleteEarthquake(Earthquake earthquake);

  @Query("SELECT * FROM earthquake ORDER BY mDate DESC")
  public LiveData<List<Earthquake>> loadAllEarthquakes();

  @Query("SELECT mId as _id, " +
           "mDetails as suggest_text_1, " +
           "mId as suggest_intent_data_id " +
           "FROM earthquake " +
           "WHERE mDetails LIKE :query " +
           "ORDER BY mdate DESC")
  public Cursor generateSearchSuggestions(String query);

  @Query("SELECT * " +
           "FROM earthquake " +
           "WHERE mDetails LIKE :query " +
           "ORDER BY mdate DESC")
  public LiveData<List<Earthquake>> searchEarthquakes(String query);

  @Query("SELECT * " +
           "FROM earthquake " +
           "WHERE mId = :id " +
           "LIMIT 1")
  public LiveData<Earthquake> getEarthquake(String id);

  @Query("SELECT * FROM earthquake ORDER BY mDate DESC")
  List<Earthquake> loadAllEarthquakesBlocking();

  @Query("SELECT * FROM earthquake ORDER BY mDate DESC LIMIT 1")
  Earthquake getLatestEarthquake();
}