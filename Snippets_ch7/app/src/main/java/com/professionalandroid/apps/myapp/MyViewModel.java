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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Listing 7-2: Using a View Model to download on
 * a background thread using an AsyncTask
 */
public class MyViewModel extends AndroidViewModel {
  private static final String TAG = "MyViewModel";

  private MutableLiveData<List<String>> data;

  public MyViewModel(Application application) {
    super(application);
  }

  public LiveData<List<String>> getData() {
    if (data == null)
      data = new MutableLiveData<List<String>>();
    loadData();
    return data;
  }

  private void loadData() {
    new AsyncTask<Void, Void, List<String>>() {
      @Override
      protected List<String> doInBackground(Void... voids) {
        ArrayList<String> result = new ArrayList<>(0);
        String myFeed = getApplication().getString(R.string.my_feed);
        try {
          URL url = new URL(myFeed);

          // Create a new HTTP URL connection
          URLConnection connection = url.openConnection();
          HttpURLConnection httpConnection = (HttpURLConnection) connection;

          int responseCode = httpConnection.getResponseCode();
          if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = httpConnection.getInputStream();

            // Process the input stream to generate our result list
            result = processStream(in);
          }
          httpConnection.disconnect();
        } catch (MalformedURLException e) {
          Log.e(TAG, "Malformed URL Exception.", e);
        } catch (IOException e) {
          Log.e(TAG, "IO Exception.", e);
        }
        return result;
      }
      @Override
      protected void onPostExecute(List<String> _data) {
        // Update the Live Data data value.
        data.setValue(_data);
      }
    }.execute();
  }

  private ArrayList<String> processStream(InputStream inputStream) {
    ArrayList<String> result = new ArrayList<String>();
    List<Earthquake> earthquakes = null;
    try {
      earthquakes = parseJson(inputStream);
    } catch (IOException e) {
      Log.e(TAG, "Parsing Error", e);
    }

    if (earthquakes != null) {
      for (Earthquake earthquake : earthquakes) {
        result.add(earthquake.getDetails());
      }
    }
    return result;
  }

  /*
   * Listing 7-5: Parsing JSON using the JSON Parser
   */
  private List<Earthquake> parseJson(InputStream in) throws IOException {
    // Create a new Json Reader to parse the input.
    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

    try {
      // Create an empty list of earthquakes.
      List<Earthquake> earthquakes = null;

      // The root node of the Earthquake JSON feed is an object that
      // we must parse.
      reader.beginObject();
      while (reader.hasNext()) {
        String name = reader.nextName();

        // We are only interested in one sub-object: the array of
        // earthquakes labeled as features.
        if (name.equals("features")) {
          earthquakes = readEarthquakeArray(reader);
        } else {
          // We will ignore all other root level values and objects.
          reader.skipValue();
        }
      }
      reader.endObject();

      return earthquakes;

    } finally {
      reader.close();
    }
  }

  // Traverse the array of earthquakes.
  private List<Earthquake> readEarthquakeArray(JsonReader reader)
    throws IOException {
    List<Earthquake> earthquakes = new ArrayList<Earthquake>();

    // The earthquake details are stored in an array.
    reader.beginArray();
    while (reader.hasNext()) {
      // Traverse the array, parsing each earthquake.
      earthquakes.add(readEarthquake(reader));
    }
    reader.endArray();
    return earthquakes;
  }

  // Parse each earthquake object within the earthquake array.
  public Earthquake readEarthquake(JsonReader reader) throws IOException {
    String id = null;
    Location location = null;
    Earthquake earthquakeProperties = null;

    reader.beginObject();

    while (reader.hasNext()) {
      String name = reader.nextName();

      if (name.equals("id")) {
        // The ID is stored as a value.
        id = reader.nextString();
      } else if (name.equals("geometry")) {
        // The location is stored as a geometry object
        // that must be parsed.
        location = readLocation(reader);
      } else if (name.equals("properties")) {
        // Most of the earthquake details are stored as a
        // properties object that must be parsed.
        earthquakeProperties = readEarthquakeProperties(reader);
      } else {
        reader.skipValue();
      }
    }

    reader.endObject();

    // Construct a new Earthquake based on the parsed details.
    return new Earthquake(id,
      earthquakeProperties.getDate(),
      earthquakeProperties.getDetails(),
      location,
      earthquakeProperties.getMagnitude(),
      earthquakeProperties.getLink());
  }

  // Parse the properties object for each earthquake object
  // within the earthquake array.
  public Earthquake readEarthquakeProperties(JsonReader reader) throws IOException {
    Date date = null;
    String details = null;
    double magnitude = -1;
    String link = null;

    reader.beginObject();

    while (reader.hasNext()) {
      String name = reader.nextName();

      if (name.equals("time")) {
        long time = reader.nextLong();
        date = new Date(time);
      } else if (name.equals("place")) {
        details = reader.nextString();
      } else if (name.equals("url")) {
        link = reader.nextString();
      } else if (name.equals("mag")) {
        magnitude = reader.nextDouble();
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();

    return new Earthquake(null, date, details, null, magnitude, link);
  }

  // Parse the coordinates object to obtain a location.
  private Location readLocation(JsonReader reader) throws IOException {
    Location location = null;

    reader.beginObject();

    while (reader.hasNext()) {
      String name = reader.nextName();

      if (name.equals("coordinates")) {
        // The location coordinates are stored within an
        // array of doubles.
        List<Double> coords = readDoublesArray(reader);
        location = new Location("dummy");
        location.setLatitude(coords.get(0));
        location.setLongitude(coords.get(1));
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();

    return location;
  }

  // Parse an array of doubles.
  public List<Double> readDoublesArray(JsonReader reader) throws IOException {
    List<Double> doubles = new ArrayList<Double>();

    reader.beginArray();

    while (reader.hasNext()) {
      doubles.add(reader.nextDouble());
    }
    reader.endArray();

    return doubles;
  }
}