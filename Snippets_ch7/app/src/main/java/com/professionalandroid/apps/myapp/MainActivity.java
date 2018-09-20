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

package com.professionalandroid.apps.myapp;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "SNIPPETS_CH7";

  /*
   *  Listing 7-3: Using Live Data and a View Model from an Activity
   *  Obtain (or create) an instance of the View Model
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MyViewModel myViewModel = ViewModelProviders.of(this)
                                .get(MyViewModel.class);

    // Get the current data and observe it for changes.
    myViewModel.getData()
      .observe(this, new Observer<List<String>>() {
        @Override
        public void onChanged(@Nullable List<String> data) {
          // Update your UI with the loaded data.
          // Returns cached data automatically after a configuration change,
          // and will be fired again if underlying Live Data object is modified.
        }
      });
  }

  // This method MUST be run on a background thread.
  private void listing7_1() {
    // TODO Replace with Google Places API call
    String myFeed = "";

    // Listing 7-1: Opening an Internet data stream
    try {
      URL url = new URL(myFeed);

      // Create a new HTTP URL connection
      URLConnection connection = url.openConnection();
      HttpURLConnection httpConnection = (HttpURLConnection) connection;

      int responseCode = httpConnection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        InputStream in = httpConnection.getInputStream();
        processStream(in);
      }
      httpConnection.disconnect();
    } catch (MalformedURLException e) {
      Log.e(TAG, "Malformed URL Exception.", e);
    } catch (IOException e) {
      Log.e(TAG, "IO Exception.", e);
    }
  }

  /*
   * Listing 7-4: Parsing XML using the XML Pull Parser
   */
  private void processStream(InputStream inputStream) {
    // Create a new XML Pull Parser.
    XmlPullParserFactory factory;
    try {
      factory = XmlPullParserFactory.newInstance();
      factory.setNamespaceAware(true);
      XmlPullParser xpp = factory.newPullParser();

      // Assign a new input stream.
      xpp.setInput(inputStream, null);
      int eventType = xpp.getEventType();

      // Allocate a variable for extracted name tags.
      String name;

      // Continue until the end of the document is reached.
      while (eventType != XmlPullParser.END_DOCUMENT) {

        // Check for a start tag of the results tag.
        if (eventType == XmlPullParser.START_TAG &&
              xpp.getName().equals("result")) {
          eventType = xpp.next();

          // Process each result within the result tag.
          while (!(eventType == XmlPullParser.END_TAG &&
                     xpp.getName().equals("result"))) {

            // Check for the name tag within the results tag.
            if (eventType == XmlPullParser.START_TAG &&
                  xpp.getName().equals("name")) {

              // Extract the POI name.
              name = xpp.nextText();
              doSomethingWithName(name);
            }

            // Move on to the next tag.
            eventType = xpp.next();
          }

          // Do something with each POI name.
        }

        // Move on to the next result tag.
        eventType = xpp.next();
      }
    } catch (XmlPullParserException e) {
      Log.e("PULLPARSER", "XML Pull Parser Exception", e);
    } catch (IOException e) {
      Log.e("PULLPARSER", "IO Exception", e);
    }
  }

  private void doSomethingWithName(String name) {
    // TODO Do Something with the POI name.
  }

  private void listing7_6() {
    // Listing 7-6: Downloading files using the Download Manager
    DownloadManager downloadManager =
      (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

    Uri uri = Uri.parse(
      "http://developer.android.com/shareables/icon_templates-v4.0.zip");

    DownloadManager.Request request = new DownloadManager.Request(uri);
    long reference = downloadManager.enqueue(request);
  }

  private void listing7_11() {
    // Listing 7-10: Finding details of paused downloads

    // Obtain the Download Manager Service.
    String serviceString = Context.DOWNLOAD_SERVICE;
    DownloadManager downloadManager;
    downloadManager = (DownloadManager)getSystemService(serviceString);

    // Create a query for paused downloads.
    DownloadManager.Query pausedDownloadQuery = new DownloadManager.Query();
    pausedDownloadQuery.setFilterByStatus(DownloadManager.STATUS_PAUSED);

    // Query the Download Manager for paused downloads.
    Cursor pausedDownloads = downloadManager.query(pausedDownloadQuery);

    // Find the column indexes for the data we require.
    int reasonIdx = pausedDownloads.getColumnIndex(DownloadManager.COLUMN_REASON);
    int titleIdx = pausedDownloads.getColumnIndex(DownloadManager.COLUMN_TITLE);
    int fileSizeIdx = pausedDownloads.getColumnIndex(
      DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
    int bytesDLIdx = pausedDownloads.getColumnIndex(
      DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);

    // Iterate over the result Cursor.
    while (pausedDownloads.moveToNext()) {
      // Extract the data we require from the Cursor.
      String title = pausedDownloads.getString(titleIdx);
      int fileSize = pausedDownloads.getInt(fileSizeIdx);
      int bytesDL = pausedDownloads.getInt(bytesDLIdx);

      // Translate the pause reason to friendly text.
      int reason = pausedDownloads.getInt(reasonIdx);
      String reasonString = "Unknown";
      switch (reason) {
        case DownloadManager.PAUSED_QUEUED_FOR_WIFI :
          reasonString = "Waiting for WiFi."; break;
        case DownloadManager.PAUSED_WAITING_FOR_NETWORK :
          reasonString = "Waiting for connectivity."; break;
        case DownloadManager.PAUSED_WAITING_TO_RETRY :
          reasonString = "Waiting to retry."; break;
        default : break;
      }

      // Construct a status summary
      StringBuilder sb = new StringBuilder();
      sb.append(title).append("\n");
      sb.append(reasonString).append("\n");
      sb.append("Downloaded ").append(bytesDL).append(" / " ).append(fileSize);

      // Display the status
      Log.d("DOWNLOAD", sb.toString());
    }

    // Close the result Cursor.
    pausedDownloads.close();
  }
}
