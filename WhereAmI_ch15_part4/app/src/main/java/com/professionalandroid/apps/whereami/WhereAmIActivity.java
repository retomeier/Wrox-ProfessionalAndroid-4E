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
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class WhereAmIActivity extends AppCompatActivity implements OnMapReadyCallback {

  public static final String TAG = "WhereAmIActivity";
  private static final String ERROR_MSG = "Google Play services are unavailable.";
  private static final int LOCATION_PERMISSION_REQUEST = 1;
  private static final int REQUEST_CHECK_SETTINGS = 2;

  private TextView mTextView;
  private LocationRequest mLocationRequest;
  private GoogleMap mMap;

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
  }

  LocationCallback mLocationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult locationResult) {
      Location location = locationResult.getLastLocation();
      if (location != null) {
        updateTextView(location);
      }

      if (location != null) {
        updateTextView(location);
        if (mMap != null) {
          LatLng latLng = new LatLng(location.getLatitude(),
            location.getLongitude());
          mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_where_am_i);

    mTextView = findViewById(R.id.myLocationText);

    // Obtain the SupportMapFragment and request the Google Map object.
    SupportMapFragment mapFragment =
      (SupportMapFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    GoogleApiAvailability availability = GoogleApiAvailability.getInstance();

    int result = availability.isGooglePlayServicesAvailable(this);
    if (result != ConnectionResult.SUCCESS) {
      if (!availability.isUserResolvableError(result)) {
        Toast.makeText(this, ERROR_MSG, Toast.LENGTH_LONG).show();
      }
    }

    mLocationRequest = new LocationRequest()
                         .setInterval(5000)
                         .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

    // Check of the location settings are compatible with our Location Request.
    LocationSettingsRequest.Builder builder =
      new LocationSettingsRequest.Builder()
        .addLocationRequest(mLocationRequest);

    SettingsClient client = LocationServices.getSettingsClient(this);

    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    task.addOnSuccessListener(this,
      new OnSuccessListener<LocationSettingsResponse>() {
        @Override
        public void onSuccess(LocationSettingsResponse
                                locationSettingsResponse) {
          // Location settings satisfy the requirements of the Location Request.
          // Request location updates.
          requestLocationUpdates();
        }
      });

    task.addOnFailureListener(this, new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        // Extract the status code for the failure from within the Exception.
        int statusCode = ((ApiException) e).getStatusCode();

        switch (statusCode) {
          case CommonStatusCodes.RESOLUTION_REQUIRED:
            try {
              // Display a user dialog to resolve the location settings
              // issue.
              ResolvableApiException resolvable = (ResolvableApiException) e;
              resolvable.startResolutionForResult(WhereAmIActivity.this,
                REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
              Log.e(TAG, "Location Settings resolution failed.", sendEx);
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            // Location settings issues can't be resolved by user.
            // Request location updates anyway.
            Log.d(TAG, "Location Settings can't be resolved.");
            requestLocationUpdates();
            break;
        }
      }
    });
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

    String address = geocodeLocation(location);

    String outputText = "Your Current Position is:\n" + latLongString;
    if (!address.isEmpty())
      outputText += "\n\n" + address;

    mTextView.setText(outputText);
  }

  private void requestLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
          == PERMISSION_GRANTED ||
          ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
          == PERMISSION_GRANTED) {

      FusedLocationProviderClient fusedLocationClient
        = LocationServices.getFusedLocationProviderClient(this);

      fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode,Intent data){
    final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

    if (requestCode == REQUEST_CHECK_SETTINGS) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          // Requested changes made, request location updates.
          requestLocationUpdates();
          break;
        case Activity.RESULT_CANCELED:
          // Requested changes were NOT made.
          Log.d(TAG, "Requested settings changes declined by user.");
          // Check if any location services are available, and if so
          // request location updates.
          if (states.isLocationUsable())
            requestLocationUpdates();
          else
            Log.d(TAG, "No location services available.");
          break;
        default: break;
      }
    }
  }

  private String geocodeLocation(Location location) {
    String returnString = "";

    if (location == null) {
      Log.d(TAG, "No Location to Geocode");
      return returnString;
    }

    if (!Geocoder.isPresent()) {
      Log.e(TAG, "No Geocoder Available");
      return returnString;
    } else {
      Geocoder gc = new Geocoder(this, Locale.getDefault());
      try {
        List<Address> addresses
          = gc.getFromLocation(location.getLatitude(),
          location.getLongitude(),
          1); // One Result

        StringBuilder sb = new StringBuilder();

        if (addresses.size() > 0) {
          Address address = addresses.get(0);

          for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
            sb.append(address.getAddressLine(i)).append("\n");

          sb.append(address.getLocality()).append("\n");
          sb.append(address.getPostalCode()).append("\n");
          sb.append(address.getCountryName());
        }
        returnString = sb.toString();
      } catch (IOException e) {
        Log.e(TAG, "I/O Error Geocoding.", e);
      }
      return returnString;
    }
  }
}
