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
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

/*
 * Listing 17-2: Initializing and assigning a Surface View to a Media Player
 */
public class SurfaceViewActivity extends Activity implements SurfaceHolder.Callback {

  static final String TAG = "VideoViewActivity";

  private MediaPlayer mediaPlayer;

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      // When the surface is created, assign it as the
      // display surface and assign and prepare a data
      // source.
      mediaPlayer.setDisplay(holder);

      // Specify the path, URL, or Content Provider URI of
      // the video resource to play.
      File file = new File(Environment.getExternalStorageDirectory(),
        // TODO Replace this with aan actual video.
        "sickbeatsvideo.mp4");
      mediaPlayer.setDataSource(file.getPath());
      mediaPlayer.prepare();
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "Illegal Argument Exception", e);
    } catch (IllegalStateException e) {
      Log.e(TAG, "Illegal State Exception", e);
    } catch (SecurityException e) {
      Log.e(TAG, "Security Exception", e);
    } catch (IOException e) {
      Log.e(TAG, "IO Exception", e);
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    mediaPlayer.release();
  }

  public void surfaceChanged(SurfaceHolder holder,
                             int format, int width, int height) { }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.surfaceviewvideoviewer);

    // Create a new Media Player.
    mediaPlayer = new MediaPlayer();

    // Get a reference to the Surface View.
    final SurfaceView surfaceView = findViewById(R.id.surfaceView);

    // Configure the Surface View.
    surfaceView.setKeepScreenOn(true);

    // Configure the Surface Holder and register the callback.
    SurfaceHolder holder = surfaceView.getHolder();
    holder.addCallback(this);
    holder.setFixedSize(400, 300);

    // Connect a play button.
    Button playButton = findViewById(R.id.buttonPlay);
    playButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mediaPlayer.start();
      }
    });

    // Connect a pause button.
    Button pauseButton = findViewById(R.id.buttonPause);
    pauseButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mediaPlayer.pause();
      }
    });

    // Add a skip button.
    Button skipButton = findViewById(R.id.buttonSkip);
    skipButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mediaPlayer.seekTo(mediaPlayer.getDuration()/2);
      }
    });
  }
}