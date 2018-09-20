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

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.nio.charset.Charset;

public class BeamActivity extends AppCompatActivity {

  private static final String TAG = "CH18_BEAM";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_beam);

    // Listing 18-24: Extracting the Android Beam payload
    Parcelable[] messages
      = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    if (messages != null) {
      NdefMessage message = (NdefMessage) messages[0];
      if (message != null) {
        NdefRecord record = message.getRecords()[0];
        String payload = new String(record.getPayload());
        Log.d(TAG, "Payload: " + payload);
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Listing 18-21: Creating an Android Beam NDEF message
    String payload = "Two to beam across";
    String mimeType = "application/com.professionalandroid.apps.nfcbeam";

    byte[] tagId = new byte[0];
    NdefMessage nfcMessage = new NdefMessage(new NdefRecord[] {
      // Create the NFC payload.
      new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
        mimeType.getBytes(Charset.forName("US-ASCII")),
        tagId,
        payload.getBytes(Charset.forName("US-ASCII"))),

      // Add the AAR (Android Application Record)
      NdefRecord.createApplicationRecord("com.professionalandroid.apps.nfcbeam")
    });

    // Set static beam message
    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    nfcAdapter.setNdefPushMessage(nfcMessage, this);

    // Set dynamic beam message
    setBeamMessage();
  }

  /*
   * Listing 18-22: Setting your Android Beam message dynamically
   */
  private void setBeamMessage() {
    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    nfcAdapter.setNdefPushMessageCallback(
      new NfcAdapter.CreateNdefMessageCallback() {
        public NdefMessage createNdefMessage(NfcEvent event) {
          String payload = "Beam me up, Android!\n\n" +
                           "Beam Time: " + System.currentTimeMillis();
          NdefMessage message = createMessage(payload);
          return message;
        }
      }, this);
  }

  private NdefMessage createMessage(String payload) {
    String mimeType = "application/com.professionalandroid.apps.nfcbeam";
    byte[] tagId = new byte[0];

    NdefMessage nfcMessage = new NdefMessage(new NdefRecord[] {
      // Create the NFC payload.
      new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
        mimeType.getBytes(Charset.forName("US-ASCII")),
        tagId,
        payload.getBytes(Charset.forName("US-ASCII"))),

      // Add the AAR (Android Application Record)
      NdefRecord.createApplicationRecord("com.professionalandroid.apps.nfcbeam")
    });
    return nfcMessage;
  }


}
