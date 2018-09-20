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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.tech.NfcF;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class BlogViewer extends AppCompatActivity {

  private static final String TAG = "CH18_NFC";

  /*
   * Listing 18-19: Enabling and disabling the foreground dispatch system
   */
  NfcAdapter mNFCAdapter;

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    processIntent(intent);
  }

  @Override
  public void onPause() {
    super.onPause();
    mNFCAdapter.disableForegroundDispatch(this);
  }

  @Override
  public void onResume() {
    super.onResume();

    mNFCAdapter.enableForegroundDispatch(
      this,
      // Intent that will be used to package the Tag Intent.
      mNFCPendingIntent,
      // Array of Intent Filters used to declare the Intents you
      // wish to intercept.
      mIntentFiltersArray,
      // Array of Tag technologies you wish to handle.
      mTechListsArray);
  }

  /*
   * Listing 18-20: Configuring foreground dispatching parameters
   */
  //private NfcAdapter mNFCAdapter;

  private int NFC_REQUEST_CODE = 0;

  private PendingIntent mNFCPendingIntent;
  private IntentFilter[] mIntentFiltersArray;
  private String[][] mTechListsArray;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Get the NFC Adapter.
    NfcManager nfcManager = (NfcManager)getSystemService(Context.NFC_SERVICE);
    mNFCAdapter = nfcManager.getDefaultAdapter();

    // Create the Pending Intent.
    int flags = 0;
    Intent nfcIntent = new Intent(this, getClass());
    nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    mNFCPendingIntent =
      PendingIntent.getActivity(this, NFC_REQUEST_CODE, nfcIntent, flags);

    // Create an Intent Filter limited to the URI or MIME type to
    // intercept TAG scans from.
    IntentFilter tagIntentFilter =
      new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
    tagIntentFilter.addDataScheme("http");
    tagIntentFilter.addDataAuthority("blog.radioactiveyak.com", null);
    mIntentFiltersArray = new IntentFilter[] { tagIntentFilter };

    // Create an array of technologies to handle.
    mTechListsArray = new String[][] {
      new String[] {
        NfcF.class.getName()
      }
    };

    // Process the Intent used to start the Activity/
    String action = getIntent().getAction();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
      processIntent(getIntent());
  }

  private void processIntent(Intent intent) {
    // Listing 18-18: Extracting NFC tag payloads
    String action = intent.getAction();

    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
      Parcelable[] messages = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

      if (messages != null) {
        for (Parcelable eachMessage : messages) {
          NdefMessage message = (NdefMessage) eachMessage;
          NdefRecord[] records = message.getRecords();

          if (records != null) {
            for (NdefRecord record : records) {
              String payload = new String(record.getPayload());
              Log.d(TAG, payload);
            }
          }
        }
      }
    }
  }
}
