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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.RemoteException;
import android.provider.Browser;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MEDIABROWSER";
  /*
   * Listing 17-8: Connecting to your Media Browser Service from your Activity
   * Listing 17-9: Keeping your UI in sync with playback state and metadata changes
   */
  private MediaBrowserCompat mMediaBrowser;
  private MediaControllerCompat mMediaController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Create the MediaBrowserCompat
    mMediaBrowser = new MediaBrowserCompat(
      this,
      new ComponentName(this, MediaPlaybackService.class),
      new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
          try {
            // We can construct a media controller from the session's token
            MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();
            mMediaController = new MediaControllerCompat(MainActivity.this, token);

            // Listing 17-9: Keeping your UI in sync with playback state and metadata changes
            mMediaController.registerCallback(new MediaControllerCompat.Callback() {
              @Override
              public void onPlaybackStateChanged(PlaybackStateCompat state) {
                // Update the UI based on playback state change.
              }

              @Override
              public void onMetadataChanged(MediaMetadataCompat metadata) {
                // Update the UI based on Media Metadata change.
              }
            });
          } catch (RemoteException e) {
            Log.e(TAG, "Error creating controller", e);
          }
        }

        @Override
        public void onConnectionSuspended() {
          // We were connected, but no longer are.
        }

        @Override
        public void onConnectionFailed() {
          // The attempt to connect failed completely.
          // Check the ComponentName!
        }
      },
      null);
    mMediaBrowser.connect();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mMediaBrowser.disconnect();
  }
}