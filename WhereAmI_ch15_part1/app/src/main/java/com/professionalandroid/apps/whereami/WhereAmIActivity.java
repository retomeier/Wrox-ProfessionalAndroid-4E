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

package com.professionalandroid.apps.whereami;

import android.Manifest;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class WhereAmIActivity extends AppCompatActivity {

  private static final String ERROR_MSG = "Google Play services are unavailable.";
  private static final int LOCATION_PERMISSION_REQUEST = 1;

  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_where_am_i);

    mTextView = findViewById(R.id.myLocationText);

    GoogleApiAvailability availability = GoogleApiAvailability.getInstance();

    int result = availability.isGooglePlayServicesAvailable(this);
    if (result != ConnectionResult.SUCCESS) {
      if (!availability.isUserResolvableError(result)) {
        Toast.makeText(this, ERROR_MSG, Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Check if we have permission to access high accuracy fine location.
    int permission = ActivityCompat.checkSelfPermission(this,
      ACCESS_FINE_LOCATION);

    // If permission is granted, fetch the last location.
    if (permission == PERMISSION_GRANTED) {
      getLastLocation();
    } else {
      // If permission has not been granted, request permission.
      ActivityCompat.requestPermissions(this,
        new String[]{ACCESS_FINE_LOCATION},
        LOCATION_PERMISSION_REQUEST);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == LOCATION_PERMISSION_REQUEST) {
      if (grantResults[0] != PERMISSION_GRANTED)
        Toast.makeText(this, "Location Permission Denied",
          Toast.LENGTH_LONG).show();
      else
        getLastLocation();
    }
  }

  private void getLastLocation() {
    FusedLocationProviderClient fusedLocationClient;
    fusedLocationClient =
      LocationServices.getFusedLocationProviderClient(this);
    if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
        == PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
        == PERMISSION_GRANTED) {
      fusedLocationClient.getLastLocation()
        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            updateTextView(location);
          }
        });
    }
  }

  private void updateTextView(Location location) {
    String latLongString = "No location found";
    if (location != null) {
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      latLongString = "Lat:" + lat + "\nLong:" + lng;
    }
    mTextView.setText(latLongString);
  }
}
