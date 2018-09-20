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

package com.professionalandroid.apps.gforcemeter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.professionalandroid.apps.gforcemeter.R;

import java.util.Timer;
import java.util.TimerTask;

public class ForceMeterActivity extends AppCompatActivity {

  private final double calibration = SensorManager.STANDARD_GRAVITY;

  private SensorManager mSensorManager;
  private TextView mAccelerationTextView;
  private TextView mMaxAccelerationTextView;
  private float mCurrentAcceleration = 0;
  private float mMaxAcceleration = 0;

  private final SensorEventListener mSensorEventListener
    = new SensorEventListener() {

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void onSensorChanged(SensorEvent event) {
      double x = event.values[0];
      double y = event.values[1];
      double z = event.values[2];
      double a = Math.round(Math.sqrt(Math.pow(x, 2) +
                                      Math.pow(y, 2) +
                                      Math.pow(z, 2)));

      mCurrentAcceleration = Math.abs((float)(a-calibration));

      if (mCurrentAcceleration > mMaxAcceleration)
        mMaxAcceleration = mCurrentAcceleration;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_force_meter);

    mAccelerationTextView = findViewById(R.id.acceleration);
    mMaxAccelerationTextView = findViewById(R.id.maxAcceleration);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    Timer updateTimer = new Timer("gForceUpdate");
    updateTimer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        updateGUI();
      }
    }, 0, 100);
  }

  @Override
  protected void onResume() {
    super.onResume();

    Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    mSensorManager.registerListener(mSensorEventListener,
      accelerometer,
      SensorManager.SENSOR_DELAY_FASTEST);
  }

  @Override
  protected void onPause() {
    super.onPause();

    mSensorManager.unregisterListener(mSensorEventListener);
  }

  private void updateGUI() {
    runOnUiThread(new Runnable() {
      public void run() {
        String currentG = mCurrentAcceleration /
                            SensorManager.STANDARD_GRAVITY
                            + "Gs";

        mAccelerationTextView.setText(currentG);
        mAccelerationTextView.invalidate();

        String maxG = mMaxAcceleration/SensorManager.STANDARD_GRAVITY
                        + "Gs";

        mMaxAccelerationTextView.setText(maxG);
        mMaxAccelerationTextView.invalidate();
      }
    });
  }
}
