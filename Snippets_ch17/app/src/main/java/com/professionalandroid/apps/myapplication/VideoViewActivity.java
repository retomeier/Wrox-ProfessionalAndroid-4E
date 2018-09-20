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
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

/*
 * Listing 17-3: Playing a video using Player View
 */
public class VideoViewActivity extends AppCompatActivity {

  private PlayerView playerView;
  private SimpleExoPlayer player;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playerview);
    playerView = findViewById(R.id.player_view);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Create a new Exo Player
    player = ExoPlayerFactory.newSimpleInstance(this,
      new DefaultTrackSelector());

    // Associate the ExoPlayer with the Player View
    playerView.setPlayer(player);

    // Build a DataSource.Factory capable of
    // loading http and local content
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
      this,
      Util.getUserAgent(this, getString(R.string.app_name)));

    // Specify the URI to play
    File file = new File(Environment.getExternalStorageDirectory(),
      // TODO Replace with a real file.
      "test2.mp4");
    ExtractorMediaSource mediaSource =
      new ExtractorMediaSource.Factory(dataSourceFactory)
        .createMediaSource(Uri.fromFile(file));

    // Start loading the media source
    player.prepare(mediaSource);

    // Start playback automatically when ready
    player.setPlayWhenReady(true);
  }

  @Override
  protected void onStop() {
    playerView.setPlayer(null);
    player.release();
    player = null;
    super.onStop();
  }
}