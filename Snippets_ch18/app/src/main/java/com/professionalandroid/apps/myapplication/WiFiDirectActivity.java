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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WiFiDirectActivity extends AppCompatActivity {

  private static final String TAG = "CH18_WIFIDIRECT";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wi_fi_direct);
  }

  /*
   * Listing 18-9: Initializing Wi-Fi Direct
   */
  private WifiP2pManager mWifiP2pManager;
  private WifiP2pManager.Channel mWifiDirectChannel;

  private void initializeWiFiDirect() {
    mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);

    mWifiDirectChannel = mWifiP2pManager.initialize(this, getMainLooper(),
      new WifiP2pManager.ChannelListener() {
        public void onChannelDisconnected() {
          Log.d(TAG, "Wi-Fi P2P channel disconnected.");
        }
      }
    );
  }

  /*
   * Listing 18-10: Creating a Wi-Fi P2P Manager Action Listener
   */
  private WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
    public void onFailure(int reason) {
      String errorMessage = "WiFi Direct Failed: ";
      switch (reason) {
        case WifiP2pManager.BUSY :
          errorMessage += "Framework busy."; break;
        case WifiP2pManager.ERROR :
          errorMessage += "Internal error."; break;
        case WifiP2pManager.P2P_UNSUPPORTED :
          errorMessage += "Unsupported."; break;
        default:
          errorMessage += "Unknown error."; break;
      }
      Log.e(TAG, errorMessage);
    }

    public void onSuccess() {
      // Success!
      // Return values will be returned using a Broadcast Intent
    }
  };

  /*
   * Listing 18-11: Receiving a Wi-Fi Direct status change
   */
  BroadcastReceiver p2pStatusReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      int state = intent.getIntExtra(
        WifiP2pManager.EXTRA_WIFI_STATE,
        WifiP2pManager.WIFI_P2P_STATE_DISABLED);

      switch (state) {
        case (WifiP2pManager.WIFI_P2P_STATE_ENABLED):
          // TODO Enable discovery option in the UI.
          break;
        default:
          // TODO Disable discovery option in the UI.
      }
    }
  };

  /*
   * Listing 18-12: Discovering Wi-Fi Direct peers
   */
  private void discoverPeers() {
    IntentFilter intentFilter = new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    registerReceiver(peerDiscoveryReceiver, intentFilter);

    mWifiP2pManager.discoverPeers(mWifiDirectChannel, actionListener);
  }

  BroadcastReceiver peerDiscoveryReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      mWifiP2pManager.requestPeers(mWifiDirectChannel,
        new WifiP2pManager.PeerListListener() {
          public void onPeersAvailable(WifiP2pDeviceList peers) {
            // TODO Update UI with new list of peers.
          }
        });
    }
  };

  /*
   * Listing 18-13: Requesting a connection to a Wi-Fi Direct peer
   */
  private void connectTo(WifiP2pDevice peerDevice) {
    WifiP2pConfig config = new WifiP2pConfig();
    config.deviceAddress = peerDevice.deviceAddress;
    mWifiP2pManager.connect(mWifiDirectChannel, config, actionListener);
  }


  /*
   * Listing 18-14: Connecting to a Wi-Fi Direct peer
   */
  BroadcastReceiver connectionChangedReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Extract the NetworkInfo
      String extraKey = WifiP2pManager.EXTRA_NETWORK_INFO;
      NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(extraKey);

      // Check if we're connected
      if (networkInfo.isConnected()) {
        mWifiP2pManager.requestConnectionInfo(mWifiDirectChannel,
          new WifiP2pManager.ConnectionInfoListener() {
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
              // If the connection is established
              if (info.groupFormed) {
                // If we're the server
                if (info.isGroupOwner) {
                  // TODO Initiate server socket.
                }
                // If we're the client
                else if (info.groupFormed) {
                  // TODO Initiate client socket.
                }
              }
            }
          });
      } else {
        Log.d(TAG, "Wi-Fi Direct Disconnected.");
      }
    }
  };

  /*
   * Listing 18-15: Creating a server Socket
   */
  Socket mServerClient;
  int port = 8666;

  private void startWifiDirectServer() {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      mServerClient = serverSocket.accept();
      // TODO Once connected, use mServerClient to send messages.
    } catch (IOException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  /*
   * Listing 18-16: Creating a client Socket
   */
  int timeout = 10000;
  // int port = 8666;

  private void startWifiDirectClient(String hostAddress) {
    Socket socket = new Socket();
    InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);
    try {
      socket.bind(null);
      socket.connect(socketAddress, timeout);
      listenForWiFiMessages(socket);
    } catch (IOException e) {
      Log.e(TAG, "IO Exception.", e);
    }
  }

  boolean mListening = false;

  private String listenForWiFiMessages(final Socket socket) {
    Thread acceptThread = new Thread(new Runnable() {
      public void run() {

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
              while ((bytesRead == bufferSize) &&
                       (buffer[bufferSize - 1] != 0)) {
                result = result + new String(buffer, 0, bytesRead - 1);
                bytesRead = instream.read(buffer);
              }
              result = result + new String(buffer, 0, bytesRead - 1);
              Log.d(TAG, result);
            }
          }
        } catch (IOException e) {
          Log.e(TAG, "Message received failed.", e);
        }
      }
    });
    acceptThread.start();
    return null;
  }
}
