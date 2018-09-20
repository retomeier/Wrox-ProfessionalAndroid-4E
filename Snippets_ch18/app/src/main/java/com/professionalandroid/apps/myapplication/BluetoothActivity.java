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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

  private static final String TAG = "CH18_BLUETOOTH";
  private static final int REQUEST_ACCESS_COARSE_LOCATION = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bluetooth);
  }

  /*
   * Listing 18-1: Accessing the default Bluetooth
   * Listing 18-2: Enabling Bluetooth
   * Listing 18-4: Monitoring discoverability request approval
   */
  private BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();

  private static final int ENABLE_BLUETOOTH = 1;

  private void initBluetooth() {
    if (!mBluetooth.isEnabled()) {
      // Bluetooth isn't enabled, prompt the user to turn it on.
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(intent, ENABLE_BLUETOOTH);
    } else {
      // Bluetooth is enabled, initialize the UI.
      initBluetoothUI();
    }
  }

  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    if (requestCode == ENABLE_BLUETOOTH)
      if (resultCode == RESULT_OK) {
        // Bluetooth has been enabled, initialize the UI.
        initBluetoothUI();
      }

    // Listing 18-4: Monitoring discoverability request approval
    if (requestCode == DISCOVERY_REQUEST) {
      if (resultCode == RESULT_CANCELED) {
        Log.d(TAG, "Discovery canceled by user.");
      }
    }
  }

  private void initBluetoothUI() {
    // TODO Update the UI when Bluetooth has been enabled.
  }

  /*
   * Listing 18-3: Enabling discoverability
   */
  private static final int DISCOVERY_REQUEST = 2;

  private void enable_discovery() {
    startActivityForResult(
      new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
      DISCOVERY_REQUEST);
  }

  /*
   * Listing 18-5: Discovering remote Bluetooth Devices
   */
  //private BluetoothAdapter mBluetooth;

  private List<BluetoothDevice> deviceList = new ArrayList<>();

  private void startDiscovery() {
    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String remoteDeviceName =
          intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
        BluetoothDevice remoteDevice =
          intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        deviceList.add(remoteDevice);
        Log.d(TAG, "Discovered " + remoteDeviceName);
      }
    };

    if (ContextCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_COARSE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
      mBluetooth = BluetoothAdapter.getDefaultAdapter();

      registerReceiver(discoveryResult,
        new IntentFilter(BluetoothDevice.ACTION_FOUND));

      if (mBluetooth.isEnabled() && !mBluetooth.isDiscovering()) {
        deviceList.clear();
        mBluetooth.startDiscovery();
      } else
        ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
          REQUEST_ACCESS_COARSE_LOCATION);
    }
  }

  /*
   * Listing 18-6: Listening for Bluetooth Socket connection requests
   */
  //private BluetoothAdapter mBluetooth;
  private BluetoothSocket mBluetoothSocket;

  private UUID startServerSocket() {
    UUID uuid = UUID.randomUUID(); // Listener socket must know this value.
    String name = "bluetoothserver";

    mBluetooth = BluetoothAdapter.getDefaultAdapter();
    try {
      final BluetoothServerSocket btserver = mBluetooth.listenUsingRfcommWithServiceRecord(name, uuid);

      Thread acceptThread = new Thread(new Runnable() {
        public void run() {
          try {
            // Block until client connection established.
            mBluetoothSocket = btserver.accept();

            // Start listening for messages.
            listenForMessages();
          } catch (IOException e) {
            Log.e(TAG, "Server connection IO Exception", e);
          }
        }
      });
      acceptThread.start();
    } catch (IOException e) {
      Log.e(TAG, "Socket listener IO Exception", e);
    }
    return uuid;
  }

  private void listenForMessages() {
    // TODO Listen for messages between sockets.
  }

  /*
   * Listing 18-7: Creating a client Bluetooth Socket
   */
  //private BluetoothSocket mBluetoothSocket;

  private void connectToServerSocket(BluetoothDevice device, UUID uuid) {
    try{
      BluetoothSocket clientSocket = device.createRfcommSocketToServiceRecord(uuid);

      // Block until server connection accepted.
      clientSocket.connect();

      // Add a reference to the socket used to send messages.
      mBluetoothSocket = clientSocket;

      // Start listening for messages.
      listenForMessages();
    } catch (IOException e) {
      Log.e(TAG, "Bluetooth client I/O Exception.", e);
    }
  }

  /*
   *  Listing 18-8: Sending and receiving strings using Bluetooth Sockets
   */
  private void sendMessage(BluetoothSocket socket, String message) {
    OutputStream outputStream;
    try {
      outputStream = socket.getOutputStream();

      // Add a stop character.
      byte[] byteArray = (message + " ").getBytes();
      byteArray[byteArray.length-1] = 0;
      outputStream.write(byteArray);
    } catch (IOException e) {
      Log.e(TAG, "Failed to send message: " + message, e);
    }
  }

  private boolean mListening = false;

  private String listenForMessages(BluetoothSocket socket,
                                   StringBuilder incoming) {
    String result = "";
    mListening = true;

    int bufferSize = 1024;
    byte[] buffer = new byte[bufferSize];

    try {
      InputStream instream = socket.getInputStream();
      int bytesRead = -1;
      while (mListening) {
        bytesRead = instream.read(buffer);
        if (bytesRead != -1) {
          while ((bytesRead == bufferSize) && (buffer[bufferSize-1] != 0)) {
            result = result + new String(buffer, 0, bytesRead - 1);
            bytesRead = instream.read(buffer);
          }

          result = result + new String(buffer, 0, bytesRead - 1);
          incoming.append(result);
        }
      }
    } catch (IOException e) {
      Log.e(TAG, "Message receive failed.", e);
    }
    return result;
  }
}
