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
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MediaSnippetsActivity extends AppCompatActivity {

  private static final String TAG = "CH17_SNIPPETS";
  private static final int TAKE_PICTURE = 1;
  private static final int CAMERA_PERMISSION_REQUEST = 2;

  private ImageView imageView;

  private SurfaceHolder mHolder;
  private CameraDevice mCamera;
  private CameraCaptureSession mCaptureSession;

  /*
   * Listing 17-19: Taking a picture
   */
  private ImageReader mImageReader;
  private ImageReader.OnImageAvailableListener mOnImageAvailableListener;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_media_snippets);
    imageView = findViewById(R.id.image_view);
    SurfaceView surfaceView = findViewById(R.id.picture_surface_view);
    mHolder = surfaceView.getHolder();

    initCamera();

    SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder holder) {
        try {
          startCameraCaptureSession();
        } catch (CameraAccessException e) {
          Log.d(TAG, "Camera capture failed.", e);
        }
      }

      @Override
      public void surfaceDestroyed(SurfaceHolder holder) {
      }

      @Override
      public void surfaceChanged(SurfaceHolder holder, int format,
                                 int width, int height) {
      }
    };

    mHolder.addCallback(surfaceHolderCallback);
    mHolder.setFixedSize(400, 300);

    int largestWidth = 400;  // Read from characteristics
    int largestHeight = 300; // Read from characteristics

    mOnImageAvailableListener
      = new ImageReader.OnImageAvailableListener() {

      @Override
      public void onImageAvailable(ImageReader reader) {
        try (Image image = reader.acquireNextImage()) {
          Image.Plane[] planes = image.getPlanes();
          if (planes.length > 0) {
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            saveImage(data);
          }
        }
      }
    };

    mImageReader = ImageReader.newInstance(largestWidth, largestHeight,
      ImageFormat.JPEG,
      2); // maximum number of images to return

    mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
      null); // optional Handler

    try {
      if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // Request the permission
        ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        return;
      }
      cameraManager.openCamera(cameraId, cameraDeviceCallback, null);
    } catch (Exception e) {
      Log.e(TAG, "Unable to open the camera", e);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void takePicture() {
    try {
      CaptureRequest.Builder takePictureBuilder = mCamera.createCaptureRequest(
        CameraDevice.TEMPLATE_STILL_CAPTURE);

      takePictureBuilder.addTarget(mImageReader.getSurface());

      mCaptureSession.capture(takePictureBuilder.build(),
        null,  // CaptureCallback
        null);      // optional Handler
    } catch (CameraAccessException e) {
      Log.e(TAG, "Error capturing the photo", e);
    }
  }

  private void saveImage(byte[] data) {
    // Save the image JPEG data to external storage
    FileOutputStream outStream = null;
    try {
      File outputFile = new File(
        getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test.jpg");
      outStream = new FileOutputStream(outputFile);
      outStream.write(data);
      outStream.close();
    } catch (FileNotFoundException e) {
      Log.e(TAG, "File Not Found", e);
    } catch (IOException e) {
      Log.e(TAG, "IO Exception", e);
    }
  }

  CaptureRequest.Builder mPreviewCaptureRequest;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void startCameraCaptureSession() throws CameraAccessException {
    // We require both the surface and camera to be ready
    if (mCamera == null || mHolder.isCreating()) {
      return;
    }

    Surface previewSurface = mHolder.getSurface();

    // Create our preview CaptureRequest.Builder
    mPreviewCaptureRequest = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
    mPreviewCaptureRequest.addTarget(previewSurface);
    CameraCaptureSession.StateCallback captureSessionCallback
      = new CameraCaptureSession.StateCallback() {
      @Override
      public void onConfigured(@NonNull CameraCaptureSession session) {
        mCaptureSession = session;
        try {
          mCaptureSession.setRepeatingRequest(
            mPreviewCaptureRequest.build(),
            null, // optional CaptureCallback
            null); // optional Handler
        } catch (CameraAccessException | IllegalStateException e) {
          Log.e(TAG, "Capture Session Exception.", e);
          // Handle failures
        }
      }

      @Override
      public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
        Log.e(TAG, "Capture Session Configuration Failed.");
        // Handle failures
      }
    };

    try {
      mCamera.createCaptureSession(Arrays.asList(previewSurface),
        captureSessionCallback,
        null); // optional Handler
    } catch (CameraAccessException e) {
      Log.e(TAG, "Camera Access Exception", e);
    }
  }

  MediaPlayer mediaPlayer;

  private MediaPlayer.OnPreparedListener myOnPreparedListener =
    new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
        listing17_4();
      }
    };

  private void listing17_1() {
    try {
      // Listing 17-1: Playback using the Media Player
      MediaPlayer mediaPlayer = new MediaPlayer();
      mediaPlayer.setDataSource("http://site.com/audio/mydopetunes.mp3");
      mediaPlayer.setOnPreparedListener(myOnPreparedListener);
      mediaPlayer.prepareAsync();
    } catch (IOException e) {
      Log.e(TAG, "Playback Error.", e);
    }
  }

  private void listing17_4() {
    // Listing 17-4: Requesting audio focus
    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    // Request audio focus for playback
    int result = am.requestAudioFocus(focusChangeListener,
      // Use the music stream.
      AudioManager.STREAM_MUSIC,
      // Request ongoing focus.
      AudioManager.AUDIOFOCUS_GAIN);

    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
      mediaPlayer.start();
    }
  }

  /*
   * Listing 17-5: Responding to the loss of audio focus
   */
  private AudioManager.OnAudioFocusChangeListener focusChangeListener =
    new AudioManager.OnAudioFocusChangeListener() {
      public void onAudioFocusChange(int focusChange) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (focusChange) {
          case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
            // Lower the volume while ducking.
            mediaPlayer.setVolume(0.2f, 0.2f);
            break;
          case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
            mediaPlayer.pause();
            break;
          case (AudioManager.AUDIOFOCUS_LOSS):
            mediaPlayer.stop();
            am.abandonAudioFocus(this);
            break;
          case (AudioManager.AUDIOFOCUS_GAIN):
            // Return the volume to normal and resume if paused.
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.start();
            break;
          default:
            break;
        }
      }
    };

  MediaRecorder mediaRecorder = new MediaRecorder();

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void listing17_13() {
    try {
      // Listing 17-13: Preparing to record audio using the Media Recorder

      // Configure the input sources.
      mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

      // Set the output format and encoder.
      mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

      // Specify the output file
      File mediaDir = getExternalMediaDirs()[0];
      File outputFile = new File(getExternalMediaDirs()[0], "myaudiorecording.3gp");
      mediaRecorder.setOutputFile(outputFile.getPath());

      // Prepare to record
      mediaRecorder.prepare();

      // Start Recording
      mediaRecorder.start();

    } catch (IOException e) {
      Log.e(TAG, "Media Recording Error.", e);
    }
  }

  private void listing17_14() {
    // Listing 17-14: Stopping an audio recording
    mediaRecorder.stop();

    // Reset and release the media recorder.
    mediaRecorder.reset();
    mediaRecorder.release();
  }

  private void listing17_15(Context context) {
    // Listing 17-15: Requesting a full-size picture using an Intent

    // Create an output file.
    File outputFile = new File(
      context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
      "test.jpg");
    Uri outputUri = FileProvider.getUriForFile(context,
      BuildConfig.APPLICATION_ID + ".files", outputFile);

    // Generate the Intent.
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

    // Launch the camera app.
    startActivityForResult(intent, TAKE_PICTURE);
  }

  /*
   * Listing 17-16: Receiving pictures from an Intent
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == TAKE_PICTURE) {
      File outputFile = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test.jpg");

      // Check if the result includes a thumbnail Bitmap
      if (data != null) {
        if (data.hasExtra("data")) {
          Bitmap thumbnail = data.getParcelableExtra("data");
          imageView.setImageBitmap(thumbnail);
        }
      } else {
        // If there is no thumbnail image data, the image
        // will have been stored in the target output URI.
        // Resize the full image to fit in our image view.
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
        factoryOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(outputFile.getPath(), factoryOptions);
        int imageWidth = factoryOptions.outWidth;
        int imageHeight = factoryOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(imageWidth / width,
          imageHeight / height);

        // Decode the image file into a Bitmap sized to fill the View
        factoryOptions.inJustDecodeBounds = false;
        factoryOptions.inSampleSize = scaleFactor;
        Bitmap bitmap =
          BitmapFactory.decodeFile(outputFile.getPath(),
            factoryOptions);
        imageView.setImageBitmap(bitmap);
      }
    }
  }

  CameraManager cameraManager;
  String cameraId;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void initCamera() {
    cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    String[] cameraIds = new String[0];

    try {
      cameraIds = cameraManager.getCameraIdList();
      if (cameraIds.length == 0) return;

      cameraId = cameraIds[0];
    } catch (CameraAccessException e) {
      Log.e(TAG, "Camera Error.", e);
      return;
    }
  }

  // Listing 17-18: Opening a camera device
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

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void listing17_17_18() {
    initCamera();
    CameraCharacteristics characteristics;

    try {
      characteristics = cameraManager.getCameraCharacteristics(cameraId);

      // Listing 17-17: Determining the direction of a camera device
      int facing = characteristics.get(CameraCharacteristics.LENS_FACING);

      if (facing == CameraCharacteristics.LENS_FACING_BACK) {
        // back camera
      } else if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
        // front camera
      } else {
        // external cameraCameraCharacteristics.LENS_FACING_EXTERNAL
      }
    } catch (CameraAccessException e) {
      Log.e(TAG, "Camera Error.", e);
    }

    try {
      if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // Request the permission
        ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        return;
      }

      cameraManager.openCamera(cameraId, cameraDeviceCallback, null);
    } catch (Exception e) {
      Log.e(TAG, "Unable to open the camera", e);
    }
  }

  private void listing17_20() {
    // Listing 17-20: Reading and modifying EXIF data
    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
      "test.jpg");
    try {
      ExifInterface exif = new ExifInterface(file.getCanonicalPath());

      // Read the camera model
      String model = exif.getAttribute(ExifInterface.TAG_MODEL);
      Log.d(TAG, "Model: " + model);

      // Set the camera make
      exif.setAttribute(ExifInterface.TAG_MAKE, "My Phone");

      // Finally, call saveAttributes to save the updated tag data
      exif.saveAttributes();
    } catch (IOException e) {
      Log.e(TAG, "IO Exception", e);
    }
  }
}
