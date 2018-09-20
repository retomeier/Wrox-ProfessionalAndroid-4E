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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/*
 * Listing 9-1: Defining a Room entity
 */

@Entity
public class Hoard {

  @NonNull
  @PrimaryKey
  private String hoardName;
  private int goldHoarded;
  private boolean hoardAccessible;

  public String getHoardName() {
    return hoardName;
  }

  public void setHoardName(String hoardName) {
    this.hoardName = hoardName;
  }

  public int getGoldHoarded() {
    return goldHoarded;
  }

  public void setGoldHoarded(int goldHoarded) {
    this.goldHoarded = goldHoarded;
  }

  public boolean getHoardAccessible() {
    return hoardAccessible;
  }

  public void setHoardAccessible(boolean hoardAccessible) {
    this.hoardAccessible = hoardAccessible;
  }

  public Hoard(String hoardName, int goldHoarded, boolean hoardAccessible) {
    this.hoardName = hoardName;
    this.goldHoarded = goldHoarded;
    this.hoardAccessible = hoardAccessible;
  }
}