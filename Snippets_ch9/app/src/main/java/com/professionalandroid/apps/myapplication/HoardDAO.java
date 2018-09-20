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
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Update;

import java.util.List;

/*
 * Listing 9-3: Defining a Room Data Access Object
 */
@Dao
public interface HoardDAO {
  // Listing 9-5: Defining a Room database insert method within a DOA
  // Insert a list of hoards, replacing stored hoards using the same name.
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public void insertHoards(List<Hoard> hoards);

  // Insert one new hoard.
  @Insert
  public void insertHoard(Hoard hoard);

  // Listing 9-6: Defining a Room database update method within a DOA
  @Update
  public void updateHoards(Hoard... hoard);

  @Update
  public void updateHoard(Hoard hoard);

  // Listing 9-7: Defining a Room database delete method within a DOA
  @Delete
  public void deleteHoard(Hoard hoard);

  @Delete
  public void deleteTwoHoards(Hoard hoard1, Hoard hoard2);

  // Listing 9-8: Querying a Room database
  // Return all hoards
  @Query("SELECT * FROM hoard")
  public List<Hoard> loadAllHoards();

  // Return a named hoard
  @Query("SELECT * FROM hoard WHERE hoardName = :hoardName")
  public Hoard loadHoardByName(String hoardName);

  // Listing 9-9:  Using a List parameter when querying a Room database
  @Query("SELECT * FROM Hoard WHERE hoardName IN(:hoardNames)")
  public List<Hoard> findByNames(String[] hoardNames);

  // Listing 9-10: Returning a subset of columns from a Room database query
  @Query("SELECT goldHoarded, hoardAccessible FROM hoard")
  public List<AnonymousHoard> getAnonymousAmounts();

  @Query("SELECT AVG(goldHoarded) FROM hoard")
  public int averageGoldHoarded();

  @Query("SELECT SUM(goldHoarded) FROM hoard")
  public int totalGoldHoarded();

  // Listing 9-13: Creating an observable query using Live Data
  @Query("SELECT * FROM hoard")
  public LiveData<List<Hoard>> monitorAllHoards();
}