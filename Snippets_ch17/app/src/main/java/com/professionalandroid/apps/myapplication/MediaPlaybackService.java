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

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Listing 17-6: A skeleton Media Browser Service implementation
 * Listing 17-10: Starting playback on a Media Browser Service
 * Listing 17-11: Using a foreground Service for media playback
 * Listing 17-12: Building a Media Style Notification
 */
public class MediaPlaybackService extends MediaBrowserServiceCompat {

  private static final String LOG_TAG = "MediaPlaybackService";
  private int NOTIFICATION_ID = 1;

  private MediaSessionCompat mMediaSession;

  @Override
  public void onCreate() {
    super.onCreate();
    initMediaPlayer();

    mMediaSession = new MediaSessionCompat(this, LOG_TAG);

    // Other initialization such as setFlags, setCallback, etc.
    setSessionToken(mMediaSession.getSessionToken());

    // Listing 17-10: Starting playback on a Media Browser Service
    mMediaSession.setCallback(new MediaSessionCompat.Callback() {
      @Override
      public void onPlay() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = am.requestAudioFocus(focusChangeListener,
          AudioManager.STREAM_MUSIC,
          AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
          registerNoisyReceiver();
          mMediaSession.setActive(true);
          updateMetadata();
          updatePlaybackState();
          mediaPlayer.start();

          // Call startService to keep your Service alive during playback.
          startService(new Intent(MediaPlaybackService.this,
            MediaPlaybackService.class));

          // Listing 17-11
          // Construct a Media Style Notification and start the foreground Service
          startForeground(NOTIFICATION_ID, buildMediaNotification());
        }
      }

      @Override
      public void onPause() {
        unregisterNoisyReceiver();
        mediaPlayer.pause();
        updatePlaybackState();

        // Listing 17-11
        // Stop being a foreground service, but don't remove the notification
        stopForeground(false);
      }

      @Override
      public void onStop() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(focusChangeListener);
        updatePlaybackState();
        mMediaSession.setActive(false);
        mediaPlayer.stop();

        // Listing 17-11
        // Stop being a foreground service and remove the notification
        stopForeground(true);

        // Then call stopSelf to allow your service to be destroyed
        // now that playback has stopped
        stopSelf();
      }
    });
  }

  @Override
  public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                               int clientUid, Bundle rootHints) {

    // Returning null == no one can connect so we'll return something
    return new BrowserRoot(
      getString(R.string.app_name), // Name visible in Android Auto
      null);                  // Bundle of optional extras
  }

  @Override
  public void onLoadChildren(String parentId,
                             Result<List<MediaBrowserCompat.MediaItem>> result) {
    // If you want to allow users to browse media content your app returns on
    // Android Auto or Wear OS, return those results here.
    result.sendResult(new ArrayList<MediaBrowserCompat.MediaItem>());
  }

  /*
   * Listing 17-12: Building a Media Style Notification
   */
  public Notification buildMediaNotification() {
    MediaControllerCompat controller = mMediaSession.getController();
    MediaMetadataCompat mediaMetadata = controller.getMetadata();
    MediaDescriptionCompat description = mediaMetadata.getDescription();

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

    // Add description metadata from the media session
    builder
      .setContentTitle(description.getTitle())
      .setContentText(description.getSubtitle())
      .setSubText(description.getDescription())
      .setLargeIcon(description.getIconBitmap())
      .setContentIntent(controller.getSessionActivity())
      .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
        this, // Context
        PlaybackStateCompat.ACTION_STOP))
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

    // Add branding from your app
    builder
      .setSmallIcon(R.drawable.notification_icon)
      .setColor(ContextCompat.getColor(this, R.color.colorPrimary));

    // Add actions
    builder
      .addAction(new NotificationCompat.Action(
        R.drawable.pause, getString(R.string.pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
          this, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
      .addAction(new NotificationCompat.Action(
        R.drawable.skip_to_next, getString(R.string.skip_to_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
          this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

    // Add the MediaStyle
    builder
      .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
      .setShowActionsInCompactView(0)
      .setMediaSession(mMediaSession.getSessionToken())
      // These two lines are only required if your minSdkVersion is <API 21
      .setShowCancelButton(true)
      .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
        this, PlaybackStateCompat.ACTION_STOP)));

    return builder.build();
  }

  //

  MediaPlayer mediaPlayer;

  private MediaPlayer.OnPreparedListener myOnPreparedListener =
    new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
      }
    };

  private void initMediaPlayer() {
    try {
      MediaPlayer mediaPlayer = new MediaPlayer();
      // TODO Replace with actual music
      mediaPlayer.setDataSource("http://site.com/audio/mydopetunes.mp3");
      mediaPlayer.setOnPreparedListener(myOnPreparedListener);
      mediaPlayer.prepareAsync();
    } catch (IOException e) {
      Log.e(LOG_TAG, "Playback Error.", e);
    }
  }

  private AudioManager.OnAudioFocusChangeListener focusChangeListener =
    new AudioManager.OnAudioFocusChangeListener() {
      public void onAudioFocusChange(int focusChange) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        switch (focusChange) {
          case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) :
            // Lower the volume while ducking.
            mediaPlayer.setVolume(0.2f, 0.2f);
            break;
          case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) :
            mediaPlayer.pause();
            break;
          case (AudioManager.AUDIOFOCUS_LOSS) :
            mediaPlayer.stop();
            am.abandonAudioFocus(this);
            break;
          case (AudioManager.AUDIOFOCUS_GAIN) :
            // Return the volume to normal and resume if paused.
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.start();
            break;
          default: break;
        }
        updatePlaybackState();
      }
    };

  private class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals
                                                     (intent.getAction())) {
        pauseAudioPlayback();
      }
    }
  }

  private void pauseAudioPlayback() {
    mediaPlayer.pause();
    updatePlaybackState();
  }

  NoisyAudioStreamReceiver mNoisyAudioStreamReceiver = new NoisyAudioStreamReceiver();

  private void registerNoisyReceiver() {
    IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    registerReceiver(mNoisyAudioStreamReceiver, filter);
  }

  public void unregisterNoisyReceiver() {
    unregisterReceiver(mNoisyAudioStreamReceiver);
  }

  MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

  private void updateMetadata() {
    // TODO Update metadata for media being played.
    mMediaSession.setMetadata(metadataBuilder.build());
  }

  PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();

  private void updatePlaybackState() {
      playbackStateBuilder
        // Available actions
        .setActions(
          PlaybackStateCompat.ACTION_PLAY_PAUSE |
            PlaybackStateCompat.ACTION_PLAY |
            PlaybackStateCompat.ACTION_PAUSE |
            PlaybackStateCompat.ACTION_STOP |
            PlaybackStateCompat.ACTION_SEEK_TO)
        // Current playback state
        .setState(
          mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_STOPPED,
          0,          // Track position in ms
          1.0f); // Playback speed
      mMediaSession.setPlaybackState(playbackStateBuilder.build());
  }
}