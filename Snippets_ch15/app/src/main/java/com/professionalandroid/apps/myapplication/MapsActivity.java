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

/*
 * Listing 15-23: Accessing a Google Map within your Activity
 * Listing 15-24: Moving the Google Maps camera
 * Listing 15-25: Animating a Google Maps camera update
 * Listing 15-26: Adding a marker to a Google Map
 */

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    // Obtain the SupportMapFragment and request the Google Map object.
    SupportMapFragment mapFragment =
      (SupportMapFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  /**
   * This callback is triggered when the map is ready to be used.
   * If Google Play services is not installed on the device, the user
   * will be prompted to install it inside the SupportMapFragment.
   * This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    // TODO Manipulate the map.

    // Listing 15-24: Moving the Google Maps camera
    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
      @Override
      public void onMapLoaded() {
        Double firstLat = 20.288193;
        Double firstLng = -155.881057;
        LatLng firstLatLng = new LatLng(firstLat, firstLng);
        Double secondLat = 18.871097;
        Double secondLng = -154.747620;
        LatLng secondLatLng = new LatLng(secondLat, secondLng);
        LatLngBounds llBounds = LatLngBounds.builder()
                                  .include(firstLatLng)
                                  .include(secondLatLng)
                                  .build();
        CameraUpdate bUpdate = CameraUpdateFactory.newLatLngBounds(llBounds, 0);
        mMap.animateCamera(bUpdate);

        bUpdate = CameraUpdateFactory.newLatLngBounds(llBounds, 32);
        // Listing 15-25: Animating a Google Maps camera update
        int duration = 2000; // 2 seconds.
        mMap.animateCamera(bUpdate, duration, new GoogleMap.CancelableCallback() {
          @Override
          public void onFinish() {
            // TODO The camera update animation completed successfully.
          }

          @Override
          public void onCancel() {
            // TODO The camera update animation was cancelled.
          }
        });

        // Listing 15-26: Adding a marker to a Google Map
        Marker newMarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title("Honeymoon Location")
                                            .snippet("This is where I had my honeymoon!"));

      }
    });
  }

  LatLngBounds llBounds = LatLngBounds.builder()
                            .include(new LatLng(54.614028, -3.957354))
                            .include(new LatLng(66.992803, -26.369462))
                            .include(new LatLng(50.321568, -6.066729))
                            .include(new LatLng(49.757089, -5.231768))
                            .include(new LatLng(50.934844, 1.425947) )
                            .include(new LatLng(52.873063, 2.107099))
                            .include(new LatLng(56.124692, -1.738115))
                            .include(new LatLng(67.569820, -13.625322))
                            .build();

  LatLng latLng = new LatLng(3.619945, 72.722683);
}