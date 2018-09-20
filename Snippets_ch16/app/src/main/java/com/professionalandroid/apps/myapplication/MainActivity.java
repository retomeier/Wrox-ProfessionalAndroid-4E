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
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "CH16_SNIPPETS";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  private void listing16_1_2_3_4() {
    // Listing 16-1: Determining if a type of sensor is available
    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
      // TODO Barometer is available.
    } else {
      // TODO No barometer is available.
    }

    // Listing 16-2: Sensor Event Listener skeleton code
    final SensorEventListener mySensorEventListener = new SensorEventListener() {
      public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO React to new Sensor result.
      }

      public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO React to a change in Sensor accuracy.
      }
    };

    // Listing 16-3: Registering a Sensor Event Listener
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    sensorManager.registerListener(mySensorEventListener,
      sensor,
      SensorManager.SENSOR_DELAY_NORMAL);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Listing 16-4: Registering a Sensor Event Listener with a maximum Latency
      Sensor slowSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
      sensorManager.registerListener(mySensorEventListener,
        slowSensor,
        SensorManager.SENSOR_DELAY_NORMAL,
        10000000);
    }
  }

  /*
   * Listing 16-5: Trigger Event Listener skeleton code
   */
  TriggerEventListener triggerEventListener = new TriggerEventListener() {
    @Override
    public void onTrigger(TriggerEvent event) {
      // TODO React to trigger event.
    }
  };

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void listing16_6() {
    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    // Listing 16-6: Registering a Trigger Event Listener
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
    sensorManager.requestTriggerSensor(triggerEventListener, sensor);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void listing16_7() {
    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

    // Listing 16-7: Registering a Sensor Event Callback to receive Sensor Additional Info
    SensorEventCallback sensorEventCallback = new SensorEventCallback() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);
        // TODO Monitor Sensor changes.
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
        super.onAccuracyChanged(sensor, accuracy);
        // TODO React to a change in Sensor accuracy.
      }

      @Override
      public void onFlushCompleted(Sensor sensor) {
        super.onFlushCompleted(sensor);
        // FIFO of this sensor has been flushed.
      }

      @Override
      public void onSensorAdditionalInfo(SensorAdditionalInfo info) {
        super.onSensorAdditionalInfo(info);
        // TODO Monitor additional sensor information.
      }
    };

    sensorManager.registerListener(sensorEventCallback, sensor,
      SensorManager.SENSOR_DELAY_NORMAL);
  }

  private void listing16_8() {
    // Listing 16-8: Finding the screen orientation relative to the natural orientation
    WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    int rotation = display.getRotation();
    switch (rotation) {
      case (Surface.ROTATION_0) : break;   // Natural
      case (Surface.ROTATION_90) : break;  // On its left side
      case (Surface.ROTATION_180) : break; // Upside down
      case (Surface.ROTATION_270) : break; // On its right side
      default: break;
    }
  }

  private void listing16_9() {
    final SensorEventListener mySensorEventListener = new SensorEventListener() {
      // Listing 16-10: Calculating the device orientation using the rotation vector
      public void onSensorChanged(SensorEvent sensorEvent) {
        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        // Convert the result Vector to a Rotation Matrix.
        SensorManager.getRotationMatrixFromVector(rotationMatrix,
          sensorEvent.values);

        // Extract the orientation from the Rotation Matrix.
        SensorManager.getOrientation(rotationMatrix, orientation);
        Log.d(TAG, "Yaw: " + orientation[0]); // Yaw
        Log.d(TAG, "Pitch: " + orientation[1]); // Pitch
        Log.d(TAG, "Roll: " + orientation[2]); // Roll
      }

      public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    // Listing 16-9: Monitoring an accelerometer sensor
    SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    int sensorType = Sensor.TYPE_ACCELEROMETER;
    sm.registerListener(mySensorEventListener,
      sm.getDefaultSensor(sensorType),
      SensorManager.SENSOR_DELAY_NORMAL);
  }

  /*
   * Listing 16-11: Monitoring the accelerometer and magnetometer
   */
  private float[] mAccelerometerValues;
  private float[] mMagneticFieldValues;

  final SensorEventListener mCombinedSensorListener = new SensorEventListener() {
    public void onSensorChanged(SensorEvent sensorEvent) {
      if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        mAccelerometerValues = sensorEvent.values;
      if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        mMagneticFieldValues = sensorEvent.values;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  };

  private void registerCombinedListener() {
    SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    Sensor mfSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    sm.registerListener(mCombinedSensorListener,
      aSensor,
      SensorManager.SENSOR_DELAY_UI);

    sm.registerListener(mCombinedSensorListener,
      mfSensor,
      SensorManager.SENSOR_DELAY_UI);
  }

  private void listing16_12() {
    // Listing 16-12: Finding the current orientation using the accelerometer and magnetometer
    float[] values = new float[3];
    float[] R = new float[9];

    SensorManager.getRotationMatrix(R, null,
      mAccelerometerValues,
      mMagneticFieldValues);

    SensorManager.getOrientation(R, values);

    // Convert from radians to degrees if preferred.
    values[0] = (float) Math.toDegrees(values[0]); // Azimuth
    values[1] = (float) Math.toDegrees(values[1]); // Pitch
    values[2] = (float) Math.toDegrees(values[2]); // Roll
  }

  private long lastTime = 0;

  private void listing16_13() {
    // Listing 16-13: Calculating an orientation change using the gyroscope Sensor
    final float nanosecondsPerSecond = 1.0f / 100000000.0f;
    final float[] angle = new float[3];

    SensorEventListener myGyroListener = new SensorEventListener() {
      public void onSensorChanged(SensorEvent sensorEvent) {
        if (lastTime != 0) {
          final float dT = (sensorEvent.timestamp - lastTime) *
                             nanosecondsPerSecond;
          angle[0] += sensorEvent.values[0] * dT;
          angle[1] += sensorEvent.values[1] * dT;
          angle[2] += sensorEvent.values[2] * dT;
        }
        lastTime = sensorEvent.timestamp;
      }

      public void onAccuracyChanged(Sensor sensor, int accuracy) {
      }
    };

    SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    int sensorType = Sensor.TYPE_GYROSCOPE;
    sm.registerListener(myGyroListener, sm.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
  }

  private void listing16_14() {
    float[] values = new float[3];
    float[] inR = new float[9];
    float[] outR = new float[9];

    SensorManager.getRotationMatrix(inR, null,
      mAccelerometerValues,
      mMagneticFieldValues);

    // Listing 16-14: Remapping the orientation reference frame based on the natural orientation of the device
    // Determine the current orientation relative to the natural orientation
    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    int rotation = display.getRotation();
    int x_axis = SensorManager.AXIS_X;
    int y_axis = SensorManager.AXIS_Y;

    switch (rotation) {
      case (Surface.ROTATION_0): break;
      case (Surface.ROTATION_90):
        x_axis = SensorManager.AXIS_Y;
        y_axis = SensorManager.AXIS_MINUS_X;
        break;
      case (Surface.ROTATION_180):
        y_axis = SensorManager.AXIS_MINUS_Y;
        break;
      case (Surface.ROTATION_270):
        x_axis = SensorManager.AXIS_MINUS_Y;
        y_axis = SensorManager.AXIS_X;
        break;
      default: break;
    }

    SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);

    // Obtain the new, remapped, orientation values.
    SensorManager.getOrientation(outR, values);
  }

  private void listing16_15() {
    // Listing 16-15: Finding the current altitude using the barometer Sensor
    final SensorEventListener myPressureListener = new SensorEventListener() {
      public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
          float currentPressure = sensorEvent.values[0];

          // Calculate altitude
          float altitude = SensorManager.getAltitude(
            SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
            currentPressure);
        }
      }
      public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    int sensorType = Sensor.TYPE_PRESSURE;

    sm.registerListener(myPressureListener,
      sm.getDefaultSensor(sensorType),
      SensorManager.SENSOR_DELAY_NORMAL);
  }

  /*
   * Listing 16-16: Connecting a Sensor Event Listener to a heart rate monitor
   */
  private static final String HR_TAG = "HEART_RATE";
  private static final int BODY_SENSOR_PERMISSION_REQUEST = 1;

  @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
  private void connectHeartRateSensor() {
    int permission = ActivityCompat.checkSelfPermission(this,
      Manifest.permission.BODY_SENSORS);
    if (permission == PERMISSION_GRANTED) {
      // If permission granted, connect the event listener.
      doConnectHeartRateSensor();
    } else {
      if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, Manifest.permission.BODY_SENSORS)) {
        // TODO: Display additional rationale for the requested permission.
      }

      // Request the permission
      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.BODY_SENSORS}, BODY_SENSOR_PERMISSION_REQUEST);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == BODY_SENSOR_PERMISSION_REQUEST &&
          grantResults.length > 0 &&
          grantResults[0] == PERMISSION_GRANTED) {

      // If permission granted, connect the heart rate sensor.
      doConnectHeartRateSensor();
    } else {
      Log.d(TAG, "Body Sensor access permission denied.");
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
  private void doConnectHeartRateSensor() {
    SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    Sensor heartRateSensor = sm.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    if (heartRateSensor == null)
      Log.d(TAG, "No Heart Rate Sensor Detected.");
    else {
      sm.registerListener(mHeartRateListener, heartRateSensor,
        SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  final SensorEventListener mHeartRateListener = new SensorEventListener() {
    public void onSensorChanged(SensorEvent sensorEvent) {
      if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
        if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_NO_CONTACT ||
              sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
          Log.d(TAG, "Heart Rate Monitor not in contact or unreliable");
        } else {
          float currentHeartRate = sensorEvent.values[0];
          Log.d(TAG, "Heart Rate: " + currentHeartRate);
        }
      }
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  };
}
