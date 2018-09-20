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

package com.professionalandroid.apps.earthquake;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EarthquakeViewModel extends AndroidViewModel {
  private static final String TAG = "EarthquakeUpdate";
  private MutableLiveData<List<Earthquake>> earthquakes;
  public EarthquakeViewModel(Application application) {
    super(application);
  }
  public LiveData<List<Earthquake>> getEarthquakes() {
    if (earthquakes == null) {
      earthquakes = new MutableLiveData<List<Earthquake>>();
      loadEarthquakes();
    }
    return earthquakes;
  }
  // Asynchronously load the Earthquakes from the feed.
  public void loadEarthquakes() {
    new AsyncTask<Void, Void, List<Earthquake>>() {
      @Override
      protected List<Earthquake> doInBackground(Void... voids) {
        // Result ArrayList of parsed earthquakes.
        ArrayList<Earthquake> earthquakes = new ArrayList<>(0);

        // Get the XML
        URL url;
        try {
          String quakeFeed =
            getApplication().getString(R.string.earthquake_feed);
          url = new URL(quakeFeed);
          URLConnection connection;
          connection = url.openConnection();
          HttpURLConnection httpConnection = (HttpURLConnection)connection;
          int responseCode = httpConnection.getResponseCode();
          if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = httpConnection.getInputStream();
            DocumentBuilderFactory dbf =
              DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Parse the earthquake feed.
            Document dom = db.parse(in);
            Element docEle = dom.getDocumentElement();

            // Get a list of each earthquake entry.
            NodeList nl = docEle.getElementsByTagName("entry");
            if (nl != null && nl.getLength() > 0) {
              for (int i = 0 ; i < nl.getLength(); i++) {
                // Check to see if our loading has been cancelled, in which
                // case return what we have so far.
                if (isCancelled()) {
                  Log.d(TAG, "Loading Cancelled");
                  return earthquakes;
                }
                Element entry =
                  (Element)nl.item(i);
                Element id =
                  (Element)entry.getElementsByTagName("id").item(0);
                Element title =
                  (Element)entry.getElementsByTagName("title").item(0);
                Element g =
                  (Element)entry.getElementsByTagName("georss:point")
                             .item(0);
                Element when =
                  (Element)entry.getElementsByTagName("updated").item(0);
                Element link =
                  (Element)entry.getElementsByTagName("link").item(0);
                String idString = id.getFirstChild().getNodeValue();
                String details = title.getFirstChild().getNodeValue();
                String hostname = "http://earthquake.usgs.gov";
                String linkString = hostname + link.getAttribute("href");
                String point = g.getFirstChild().getNodeValue();
                String dt = when.getFirstChild().getNodeValue();
                SimpleDateFormat sdf =
                  new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                Date qdate = new GregorianCalendar(0,0,0).getTime();
                try {
                  qdate = sdf.parse(dt);
                } catch (ParseException e) {
                  Log.e(TAG, "Date parsing exception.", e);
                }
                String[] location = point.split(" ");
                Location l = new Location("dummyGPS");
                l.setLatitude(Double.parseDouble(location[0]));
                l.setLongitude(Double.parseDouble(location[1]));
                String magnitudeString = details.split(" ")[1];
                int end = magnitudeString.length()-1;
                double magnitude =
                  Double.parseDouble(magnitudeString.substring(0, end));
                if (details.contains("-"))
                  details = details.split("-")[1].trim();
                else
                  details = "";
                final Earthquake earthquake = new Earthquake(idString,
                  qdate,
                  details, l,
                  magnitude,
                  linkString);

                // Add the new earthquake to our result array.
                earthquakes.add(earthquake);
              }
            }
          }
          httpConnection.disconnect();
        } catch (MalformedURLException e) {
          Log.e(TAG, "MalformedURLException", e);
        } catch (IOException e) {
          Log.e(TAG, "IOException", e);
        } catch (ParserConfigurationException e) {
          Log.e(TAG, "Parser Configuration Exception", e);
        } catch (SAXException e) {
          Log.e(TAG, "SAX Exception", e);
        }

        // Return our result array.
        return earthquakes;
      }

      @Override
      protected void onPostExecute(List<Earthquake> data) {
        // Update the Live Data with the new list.
        earthquakes.setValue(data);
      }
    }.execute();
  }
}