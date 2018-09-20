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

package com.professionalandroid.apps.emergencyresponder;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

public class EmergencyResponderMainActivity extends AppCompatActivity {

  private static final int SMS_RECEIVE_PERMISSION_REQUEST = 1;
  public static final String SENT_SMS = "com.professionalandroid.emergencyresponder.SMS_SENT";

  ReentrantLock lock;
  ArrayList<String> requesters = new ArrayList<String>();
  private RequesterRecyclerViewAdapter mRequesterAdapter = new RequesterRecyclerViewAdapter(requesters);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_emergency_responder_main);
    lock = new ReentrantLock();
    wireUpButtons();

    ActivityCompat.requestPermissions(this,
      new String[]{Manifest.permission.RECEIVE_SMS,
      Manifest.permission.SEND_SMS,
      Manifest.permission.READ_PHONE_STATE},
      SMS_RECEIVE_PERMISSION_REQUEST);

    RecyclerView recyclerView = findViewById(R.id.requesterRecyclerListView);

    // Set the Recycler View adapter
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(mRequesterAdapter);
  }

  private void wireUpButtons() {
    Button okButton = findViewById(R.id.okButton);
    okButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        respond(true);
      }
    });

    Button notOkButton = findViewById(R.id.notOkButton);
    notOkButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        respond(false);
      }
    });
  }

  public void respond(boolean ok) {
    String okString = getString(R.string.allClearText);
    String notOkString = getString(R.string.maydayText);
    String outString = ok ? okString : notOkString;
    ArrayList<String> requestersCopy =
      (ArrayList<String>)requesters.clone();
    for (String to : requestersCopy)
      sendResponse(to, outString);
  }

  private void sendResponse(String to, String response) {
    // Check runtime permissions.
    int send_sms_permission = ActivityCompat.checkSelfPermission(this,
      Manifest.permission.SEND_SMS);

    int phone_state_permission = ActivityCompat.checkSelfPermission(this,
      Manifest.permission.READ_PHONE_STATE);

    if (send_sms_permission == PackageManager.PERMISSION_GRANTED &&
        phone_state_permission == PackageManager.PERMISSION_GRANTED) {

      // Remove the target from the list of people we
      // need to respond to.
      lock.lock();
      requesters.remove(to);
      mRequesterAdapter.notifyDataSetChanged();
      lock.unlock();

      Intent intent = new Intent(SENT_SMS);
      intent.putExtra("recipient", to);
      PendingIntent sentPI =
        PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

      // Send the message
      SmsManager sms = SmsManager.getDefault();
      sms.sendTextMessage(to, null, response, sentPI, null);
    } else {
      if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, Manifest.permission.SEND_SMS)) {
        // TODO Display additional rationale for the requested permission.
      }

      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.SEND_SMS,
          Manifest.permission.READ_PHONE_STATE},
        SMS_RECEIVE_PERMISSION_REQUEST);
    }
  }

  BroadcastReceiver emergencyResponseRequestReceiver =
    new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION )) {
          String queryString = getString(R.string.querystring).toLowerCase();
          Bundle bundle = intent.getExtras();

          if (bundle != null) {
            SmsMessage[] messages = getMessagesFromIntent(intent);
            for (SmsMessage message : messages) {
              if (message.getMessageBody().toLowerCase().contains(queryString))
                requestReceived(message.getOriginatingAddress());
            }
          }
        }
      }
    };

  public void requestReceived(String from) {
    if (!requesters.contains(from)) {
      lock.lock();
      requesters.add(from);
      mRequesterAdapter.notifyDataSetChanged();
      lock.unlock();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
    registerReceiver(emergencyResponseRequestReceiver, filter);

    IntentFilter attemptedDeliveryFilter = new IntentFilter(SENT_SMS);
    registerReceiver(attemptedSendReceiver, attemptedDeliveryFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(emergencyResponseRequestReceiver);
    unregisterReceiver(attemptedSendReceiver);
  }

  private BroadcastReceiver attemptedSendReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(SENT_SMS)) {
        if (getResultCode() != Activity.RESULT_OK) {
          String recipient = intent.getStringExtra("recipient");
          requestReceived(recipient);
        }
      }
    }
  };
}
