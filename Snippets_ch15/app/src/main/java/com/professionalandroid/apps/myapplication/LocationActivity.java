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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class LocationActivity extends AppCompatActivity {

  private static final int LOCATION_PERMISSION_REQUEST = 1;
  private static final int REQUEST_CHECK_SETTINGS = 2;
  private static final String TAG = "CHAPTER15_SNIPPETS";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);

    // Listing 15-2: Checking if Google Play services is available
    GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
    int result = availability.isGooglePlayServicesAvailable(this);
    if (result != ConnectionResult.SUCCESS) {
      if (!availability.isUserResolvableError(result)) {
        // TODO: Google Play services not available.
      }
    }

    int permission = ActivityCompat.checkSelfPermission(this,
      ACCESS_FINE_LOCATION);
    if (permission == PERMISSION_GRANTED) {
      // TODO Access the location-based services.

    } else {
      // Request fine location permission.
      if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, ACCESS_FINE_LOCATION)) {
        // TODO Display additional rationale for the requested permission.
      }

      ActivityCompat.requestPermissions(this,
        new String[]{ACCESS_FINE_LOCATION},
        LOCATION_PERMISSION_REQUEST);
    }
  }

  private void listing15_4() {
    // Listing 15-4: Accessing the Fused Location Provider
    FusedLocationProviderClient fusedLocationClient;
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
  }

  private void listing15_5() {
    int permission = ActivityCompat.checkSelfPermission(this,
      ACCESS_FINE_LOCATION);
    if (permission == PERMISSION_GRANTED) {

      // LISTING 15-5: Obtaining the last known device Location
      FusedLocationProviderClient fusedLocationClient;
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      fusedLocationClient.getLastLocation()
        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            // In some rare situations this can be null.
            if (location != null) {
              // TODO Do something with the returned location.
            }
          }
        });
    }
  }

  /*
   * Listing 15-6: Requesting location updates using a Location Request
   */
  LocationCallback mLocationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult locationResult) {
      for (Location location : locationResult.getLocations()) {
        // TODO React to newly received locations.
      }
    }
  };

  private void startTrackingLocation() {
    if (
      ActivityCompat
        .checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
        ActivityCompat
          .checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {

      FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
      LocationRequest request =
        new LocationRequest()
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
          .setInterval(5000); // Update every 5 seconds.

      locationClient.requestLocationUpdates(request, mLocationCallback, null);
    }
  }

  /*
   * Listing 15-7: Cancelling location updates
   */
  @Override
  protected void onStop() {
    super.onStop();

    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    fusedLocationClient.removeLocationUpdates(mLocationCallback);
  }

  private void listing15_8() {
    if (
      ActivityCompat
        .checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
        ActivityCompat
          .checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {

      // Listing 15-8: Requesting location updates using a Pending Intent
      FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

      LocationRequest request = new LocationRequest()
                                  .setInterval(60000 * 10) // Update every 10 minutes.
                                  .setPriority(LocationRequest.PRIORITY_NO_POWER);

      final int locationUpdateRC = 0;
      int flags = PendingIntent.FLAG_UPDATE_CURRENT;

      Intent intent = new Intent(this, MyLocationUpdateReceiver.class);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(this, locationUpdateRC, intent, flags);

      fusedLocationClient.requestLocationUpdates(request, pendingIntent);
    }
  }

  private void listing15_10_11_12() {
    LocationRequest request =
      new LocationRequest()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(5000); // Update every 5 seconds.

    // Listing 15-10: Check if the current Location Settings satisfy your requirements
    // Get the settings client.
    SettingsClient client = LocationServices.getSettingsClient(this);

    // Create a new Location Settings Request, adding our Location Requests
    LocationSettingsRequest.Builder builder =
      new LocationSettingsRequest.Builder().addLocationRequest(request);

    // Check if the Location Settings satisfy our requirements.
    Task<LocationSettingsResponse> task =
      client.checkLocationSettings(builder.build());

    // Listing 15-11: Create a handler for when Location Settings satisfy your requirements
    task.addOnSuccessListener(this,
      new OnSuccessListener<LocationSettingsResponse>() {
        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
          // Location settings satisfy the requirements of the Location Request
          startTrackingLocation();
        }
      });

    // Listing 15-12: Request user changes to location settings
    task.addOnFailureListener(this, new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        // Extract the status code for the failure from within the Exception.
        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode) {
          case CommonStatusCodes.RESOLUTION_REQUIRED:
            // Location settings don't satisfy the requirements of the
            // Location Request, but they could be resolved through user
            // selection within a Dialog.
            try {
              // Display a user dialog to resolve the location settings issue.
              ResolvableApiException resolvable = (ResolvableApiException) e;
              resolvable.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
              Log.e(TAG, "Location Settings resolution failed.", sendEx);
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            // Location settings don't satisfy the requirements of the
            // Location Request, however it can't be resolved with a user
            // dialog.
            // TODO Start monitoring location updates anyway, or abort.
            break;
          default: break;
        }
      }
    });
  }

  /*
   * Listing 15-13: Handling the user’s response to our request to change location settings
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data){

    final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

    if (requestCode == REQUEST_CHECK_SETTINGS) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          // TODO Changes were applied.
          break;
        case Activity.RESULT_CANCELED:
          // TODO Changes were not applied.
          // TODO Check states to confirm if we can attempt
          // TODO to request location updates anyway.
          break;
        default: break;
      }
    }
  }

  private void listing15_14_15_16_17(Location location, String id) {
    if (
      ActivityCompat
        .checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
      ActivityCompat
        .checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent geofenceIntent = PendingIntent.getBroadcast(this, -1, intent, 0);

        // Listing 15-14: Accessing the Geofencing Client
        GeofencingClient geofencingClient =
          LocationServices.getGeofencingClient(this);

        // Listing 15-15: Defining a Geofence
        Geofence newGeofence
          = new Geofence.Builder()
              .setRequestId(id) // unique name of geofence
              .setCircularRegion(location.getLatitude(),
                location.getLongitude(),
                30) // 30 meter radius.
              .setExpirationDuration(Geofence.NEVER_EXPIRE) // Or expiration time in ms
              .setLoiteringDelay(10 * 1000)                   // Dwell after 10 seconds
              .setNotificationResponsiveness(10 * 1000)       // Notify within 10 seconds
              .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
              .build();

        // Listing 15-16: Creating a Geofencing Request
        GeofencingRequest geofencingRequest
          = new GeofencingRequest.Builder()
              .addGeofence(newGeofence)
              .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
              .build();

        // Listing 15-17: Initiating a Geofencing Request
        geofencingClient.addGeofences(geofencingRequest, geofenceIntent)
          .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              // TODO Geofence added.
            }
          })
          .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Log.d(TAG, "Adding Geofence failed", e);
              // TODO Geofence failed to add.
            }
          });
      }
  }

  /*
   * Listing 15-19: Reverse-geocoding a given location
   */
  private void reverseGeocode(Location location) {
    double latitude = location.getLatitude();
    double longitude = location.getLongitude();
    List<Address> addresses = null;
    Geocoder gc = new Geocoder(this, Locale.getDefault());
    try {
      addresses = gc.getFromLocation(latitude, longitude, 10);
    } catch (IOException e) {
      Log.e(TAG, "Geocoder I/O Exception", e);
    }
  }

  private void listing15_20() {
    // Listing 15-20: Geocoding an address
    Geocoder geocoder = new Geocoder(this, Locale.US);
    String streetAddress = "160 Riverside Drive, New York, New York";
    List<Address> locations = null;
    try {
      locations = geocoder.getFromLocationName(streetAddress, 5);
    } catch (IOException e) {
      Log.e(TAG, "Geocoder I/O Exception", e);
    }
  }
}
