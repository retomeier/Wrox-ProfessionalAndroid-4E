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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.app.Application;
import android.location.Location;

public class Earthquake {
  private String mId;
  private Date mDate;
  private String mDetails;
  private Location mLocation;
  private double mMagnitude;
  private String mLink;

  public String getId() { return mId; }
  public Date getDate() { return mDate; }
  public String getDetails() { return mDetails; }
  public Location getLocation() { return mLocation; }
  public double getMagnitude() { return mMagnitude; }
  public String getLink() { return mLink; }

  public Earthquake(String id, Date date, String details,
                    Location location,
                    double magnitude, String link) {
    mId = id;
    mDate = date;
    mDetails = details;
    mLocation = location;
    mMagnitude = magnitude;
    mLink = link;
  }

  @Override
  public String toString() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH.mm", Locale.US);
    String dateString = sdf.format(mDate);
    return dateString + ": " + mMagnitude + " " + mDetails;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Earthquake)
      return (((Earthquake)obj).getId().contentEquals(mId));
    else
      return false;
  }
}