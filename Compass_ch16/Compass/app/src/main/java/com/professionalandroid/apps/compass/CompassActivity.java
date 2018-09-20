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

package com.professionalandroid.apps.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class CompassActivity extends AppCompatActivity {

  private CompassView mCompassView;
  private SensorManager mSensorManager;
  private int mScreenRotation;
  private float[] mNewestValues;

  private void updateOrientation(float[] values) {
    if (mCompassView!= null) {
      mCompassView.setBearing(values[0]);
      mCompassView.setPitch(values[1]);
      mCompassView.setRoll(-values[2]);
      mCompassView.invalidate();
    }
  }

  private float[] calculateOrientation(float[] values) {
    float[] rotationMatrix = new float[9];
    float[] remappedMatrix = new float[9];
    float[] orientation = new float[3];

    // Determine the rotation matrix
    SensorManager.getRotationMatrixFromVector(rotationMatrix, values);

    // Remap the coordinates based on the natural device orientation.
    int x_axis = SensorManager.AXIS_X;
    int y_axis = SensorManager.AXIS_Y;
    switch (mScreenRotation) {
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

    SensorManager.remapCoordinateSystem(rotationMatrix,
      x_axis, y_axis,
      remappedMatrix);

    // Obtain the current, corrected orientation.
    SensorManager.getOrientation(remappedMatrix, orientation);

    // Convert from Radians to Degrees.
    values[0] = (float) Math.toDegrees(orientation[0]);
    values[1] = (float) Math.toDegrees(orientation[1]);
    values[2] = (float) Math.toDegrees(orientation[2]);
    return values;
  }

  private final SensorEventListener mSensorEventListener
    = new SensorEventListener() {
    public void onSensorChanged(SensorEvent sensorEvent) {
      mNewestValues = calculateOrientation(sensorEvent.values);
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  };

  private void updateGUI() {
    runOnUiThread(new Runnable() {
      public void run() {
        updateOrientation(mNewestValues);
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_compass);
    mCompassView = findViewById(R.id.compassView);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

    Display display = wm.getDefaultDisplay();
    mScreenRotation = display.getRotation();

    mNewestValues = new float[] {0, 0, 0};

    Timer updateTimer = new Timer("compassUpdate");
    updateTimer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        updateGUI();
      }
    }, 0, 1000/60);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Sensor rotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    mSensorManager.registerListener(mSensorEventListener,
      rotationVector,
      SensorManager.SENSOR_DELAY_FASTEST);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(mSensorEventListener);
  }
}
