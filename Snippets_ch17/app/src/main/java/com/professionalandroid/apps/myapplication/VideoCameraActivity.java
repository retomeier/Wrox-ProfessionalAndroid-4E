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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class VideoCameraActivity extends AppCompatActivity {

  private static final String TAG = "CH17_VIDEORECORD";
  private static final int CAMERA_PERMISSIONS_REQUEST = 1;

  private CameraDevice mCamera;
  private CameraCaptureSession mCaptureSession;
  private SurfaceHolder mHolder;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_camera);
    SurfaceView surfaceView = findViewById(R.id.surfaceView);

    mHolder = surfaceView.getHolder();
    initCamera();
  }

  /*
   * Listing 17-21: Recording video using an Intent
   */
  private static final int RECORD_VIDEO = 0;

  private void startRecording() {

    // Generate the Intent.
    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

    // Launch the camera app.
    startActivityForResult(intent, RECORD_VIDEO);
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    if (requestCode == RECORD_VIDEO) {
      VideoView videoView = findViewById(R.id.videoView);
      videoView.setVideoURI(data.getData());
      videoView.start();
    }
  }

  /*
   * Listing 17-22: Preparing to record video using the Media Recorder
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void prepareMediaRecorder() {
    // Configure the input sources.
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

    // Set the output format and encoder.
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

    // Specify the output file
    File mediaDir = getExternalMediaDirs()[0];
    File outputFile = new File(mediaDir, "myvideorecording.mp4");
    mediaRecorder.setOutputFile(outputFile.getPath());

    // Prepare to record
    try {
      mediaRecorder.prepare();
    } catch (IOException e) {
      Log.d(TAG, "Media Recorder Failure.", e);
    }
  }

  /*
   *  Listing 17-23: Recording video
   */
  MediaRecorder mediaRecorder = new MediaRecorder();
  CaptureRequest.Builder mVideoRecordCaptureRequest;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  void startVideoRecording() {
    // We require both the preview surface and camera to be ready
    if (mCamera == null || mHolder.isCreating()) {
      return;
    }

    Surface previewSurface = mHolder.getSurface();
    prepareMediaRecorder();
    Surface videoRecordSurface = mediaRecorder.getSurface();

    // Create our video record CaptureRequest.Builder
    try {
      mVideoRecordCaptureRequest = mCamera.createCaptureRequest(
        CameraDevice.TEMPLATE_RECORD);
    } catch (CameraAccessException e) {
      Log.d(TAG, "Camera Access Exception.", e);
    }

    // Add both the video record Surface and the preview Surface
    mVideoRecordCaptureRequest.addTarget(videoRecordSurface);
    mVideoRecordCaptureRequest.addTarget(previewSurface);

    CameraCaptureSession.StateCallback captureSessionCallback
      = new CameraCaptureSession.StateCallback() {
      @Override
      public void onConfigured(@NonNull CameraCaptureSession session) {
        mCaptureSession = session;
        try {
          mCaptureSession.setRepeatingRequest(
            mVideoRecordCaptureRequest.build(),
            null,  // optional CaptureCallback
            null);      // optional Handler
          mediaRecorder.start();
        } catch (CameraAccessException | IllegalStateException e) {
          Log.d(TAG, "Recording failed.", e);
        }
      }

      @Override
      public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        // Handle failures
      }
    };

    try {
      mCamera.createCaptureSession(
        Arrays.asList(previewSurface, videoRecordSurface),
        captureSessionCallback,
        null); // optional Handler
    } catch (CameraAccessException e) {
      Log.e(TAG, "Camera Access Exception", e);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  CameraDevice.StateCallback cameraDeviceCallback =
    new CameraDevice.StateCallback() {
      @Override
      public void onOpened(@NonNull CameraDevice camera) {
        mCamera = camera;
      }

      @Override
      public void onDisconnected(@NonNull CameraDevice camera) {
        camera.close();
        mCamera = null;
      }

      @Override
      public void onError(@NonNull CameraDevice camera, int error) {
        // Something went wrong, tell the user
        camera.close();
        mCamera = null;
        Log.e(TAG, "Camera Error: " + error);
      }
  };

  /*
   * Listing 17-24: Adding files to the Media Store using the Media Scanner
   */
  private void mediaScan(final String filePath) {
    MediaScannerConnection.MediaScannerConnectionClient mediaScannerClient
      = new MediaScannerConnection.MediaScannerConnectionClient() {
      private MediaScannerConnection msc = null;

      {
        msc = new MediaScannerConnection(VideoCameraActivity.this, this);
        msc.connect();
      }

      public void onMediaScannerConnected() {
        // Optionally specify a MIME Type, or
        // have the Media Scanner imply one based
        // on the filename.
        String mimeType = null;
        msc.scanFile(filePath, mimeType);
      }

      public void onScanCompleted(String path, Uri uri) {
        msc.disconnect();
        Log.d(TAG, "File Added at: " + uri.toString());
      }
    };
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void initCamera() {
    try {
      CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
      String[] cameraIds = cameraManager.getCameraIdList();
      if (cameraIds.length == 0) return;

      String cameraId = cameraIds[0];

      if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
      }

      cameraManager.openCamera(cameraId, cameraDeviceCallback, null);
    } catch (CameraAccessException e) {
      Log.d(TAG, "No access to the camera.", e);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();

    mediaRecorder.release();
  }
}