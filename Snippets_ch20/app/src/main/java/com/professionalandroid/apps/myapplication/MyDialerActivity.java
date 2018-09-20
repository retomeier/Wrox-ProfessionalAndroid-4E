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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyDialerActivity extends AppCompatActivity {

  private static final int CALL_PHONE_PERMISSION_REQUEST = 1;
  private static final int PHONE_STATE_PERMISSION_REQUEST = 2;
  private static final String TAG = "CH20_SNIPPETS";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_dialer);
  }

  private void listing20_5() {
    // Listing 20-5: Initiating a call using the system telephony stack
    int permission = ActivityCompat.checkSelfPermission(this,
      android.Manifest.permission.CALL_PHONE);

    if (permission == PackageManager.PERMISSION_GRANTED) {
      Intent whoyougonnacall = new Intent(Intent.ACTION_CALL,
        Uri.parse("tel:555-2368"));

      startActivity(whoyougonnacall);

      // If permission hasn't been granted, request it.
    } else {
      if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, android.Manifest.permission.CALL_PHONE)) {
        // TODO Display additional rationale for the requested permission.
      }

      ActivityCompat.requestPermissions(this,
        new String[]{android.Manifest.permission.CALL_PHONE},
        CALL_PHONE_PERMISSION_REQUEST);
    }
  }

  private void listing20_6() {
    String srvcName = Context.TELEPHONY_SERVICE;
    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(srvcName);

    // Listing 20-6: Accessing phone-type and the device’s phone number
    String phoneTypeStr = "unknown";

    int phoneType = telephonyManager.getPhoneType();

    switch (phoneType) {
      case (TelephonyManager.PHONE_TYPE_CDMA):
        phoneTypeStr = "CDMA";
        break;
      case (TelephonyManager.PHONE_TYPE_GSM) :
        phoneTypeStr = "GSM";
        break;
      case (TelephonyManager.PHONE_TYPE_SIP):
        phoneTypeStr = "SIP";
        break;
      case (TelephonyManager.PHONE_TYPE_NONE):
        phoneTypeStr = "None";
        break;
      default: break;
    }

    Log.d(TAG, phoneTypeStr);

    // -- These require READ_PHONE_STATE uses-permission --
    int permission = ActivityCompat.checkSelfPermission(this,
      android.Manifest.permission.READ_PHONE_STATE);
    if (permission == PackageManager.PERMISSION_GRANTED) {

      // Read the IMEI for GSM or MEID for CDMA
      String deviceId = telephonyManager.getDeviceId();

      // Read the software version on the phone (note -- not the SDK version)
      String softwareVersion = telephonyManager.getDeviceSoftwareVersion();

      // Get the phone's number (if available)
      String phoneNumber = telephonyManager.getLine1Number();

      // If permission hasn't been granted, request it.
    } else {
      if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, android.Manifest.permission.READ_PHONE_STATE)) {
        // TODO Display additional rationale for the requested permission.
      }
      ActivityCompat.requestPermissions(this,
        new String[]{android.Manifest.permission.READ_PHONE_STATE},
        PHONE_STATE_PERMISSION_REQUEST);
    }
  }
}
