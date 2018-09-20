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

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.BeaconFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/*
 * Listing 15-27: Connecting to the Google API Client
 * Listing 15-28: Retrieving Snapshot context signal results
 * Listing 15-29: Creating Awareness Fences
 * Listing 15-30: Combining Awareness Fences
 * Listing 15-31: Creating an Awareness Fence Update Request
 * Listing 15-32: Adding a new Awareness Fence
 * Listing 15-34: Removing an Awareness Fence
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = "CONTEXT_ACTIVITY";
  private static final int LOCATION_PERMISSION_REQUEST = 1;
  private static final String WALK_FENCE_KEY = "WALK_FENCE_KEY";

  GoogleApiClient mGoogleApiClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
                         .addApi(Awareness.API)
                         .enableAutoManage(this, // MainActivity
                           this)         // OnConnectionFailedListener
                         .build();

    int permission = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
    if (permission != PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
        new String[]{ACCESS_FINE_LOCATION},
        LOCATION_PERMISSION_REQUEST);
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.e(TAG, "Failed to connect to Google Services: " +
                 connectionResult.getErrorMessage() +
                 " (" + connectionResult.getErrorCode() + ")");
    // TODO Handled failed connection.
  }

  private void listing15_28() {
    // Listing 15-28: Retrieving Snapshot context signal results
    Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
      .setResultCallback(new ResultCallback<DetectedActivityResult>() {
        @Override
        public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
          if (!detectedActivityResult.getStatus().isSuccess()) {
            Log.e(TAG, "Current activity unknown.");
          } else {
            ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
            DetectedActivity probableActivity = ar.getMostProbableActivity();

            // TODO: Do something with the detected user activity.
          }
        }
      });
  }

  private void listing15_29_30_31_32() {
    if (ActivityCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    int flags = PendingIntent.FLAG_UPDATE_CURRENT;
    Intent intent = new Intent(this, WalkFenceReceiver.class);
    PendingIntent awarenessIntent = PendingIntent.getBroadcast(this, -1,
      intent, flags);

    // Listing 15-29: Creating Awareness Fences
    // Near one of my custom beacons.
    BeaconState.TypeFilter typeFilter
      = BeaconState.TypeFilter.with("com.professionalandroid.apps.beacon", "my_type");

    AwarenessFence beaconFence = BeaconFence.near(typeFilter);

    // While walking.
    AwarenessFence activityFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);

    // Having just plugged in my headphones.
    AwarenessFence headphoneFence = HeadphoneFence.pluggingIn();

    // Within 1km of Google for longer than a minute.
    double lat = 37.4220233;
    double lng = -122.084252;
    double radius = 1000; // meters
    long dwell = 60000; // milliseconds.
    AwarenessFence locationFence = LocationFence.in(lat, lng, radius, dwell);

    // In the morning
    AwarenessFence timeFence = TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_MORNING);

    // During holidays
    AwarenessFence holidayFence = TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_HOLIDAY);

    // Listing 15-30: Combining Awareness Fences
    // Trigger when headphones are plugged in and walking in the morning
    // either within a kilometer of Google or near one of my beacons --
    // but not on a holiday.
    AwarenessFence morningWalk = AwarenessFence
                                   .and(activityFence,
                                     headphoneFence,
                                     timeFence,
                                     AwarenessFence.or(locationFence,
                                       beaconFence),
                                     AwarenessFence.not(holidayFence));

    // Listing 15-31: Creating an Awareness Fence Update Request
    FenceUpdateRequest fenceUpdateRequest
      = new FenceUpdateRequest.Builder()
          .addFence(WalkFenceReceiver.WALK_FENCE_KEY, morningWalk, awarenessIntent)
          .build();

    // Listing 15-32: Adding a new Awareness Fence
    Awareness.FenceApi.updateFences(
      mGoogleApiClient, fenceUpdateRequest).setResultCallback(new ResultCallback<Status>() {
      @Override
      public void onResult(@NonNull Status status) {
        if (!status.isSuccess()) {
          Log.d(TAG, "Fence could not be registered: " + status);
        }
      }
    });
  }

  private void listing15_34() {
    // Listing 15-34: Removing an Awareness Fence
    FenceUpdateRequest fenceUpdateRequest = new FenceUpdateRequest.Builder()
                                              .removeFence(WalkFenceReceiver.WALK_FENCE_KEY)
                                              .build();
    Awareness.FenceApi.updateFences(
      mGoogleApiClient,
      fenceUpdateRequest)
      .setResultCallback(new ResultCallback<Status>() {
        @Override
        public void onResult(@NonNull Status status) {
          if(!status.isSuccess()) {
            Log.d(TAG, "Fence could not be removed: " + status);
          }
        }
      });
  }
}