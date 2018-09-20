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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MyActivity extends AppCompatActivity {

  private static final String TAG = "Chapter6Snippets";

  private boolean somethingWeird = true;
  private boolean itDontLookGood = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);

    myTextView = findViewById(R.id.linkify_text_view);

    // Listing 6-8: Finding the launch Intent for an Activity
    Intent intent = getIntent();
    String action = intent.getAction();
    Uri data = intent.getData();
  }

  private void listing6_1() {
    // Listing 6-1: Explicitly starting a specific Activity
    Intent intent = new Intent(MyActivity.this, MyOtherActivity.class);
    startActivity(intent);
  }

  private void listing6_2() {
    // Listing 6-2: Implicitly starting an Activity
    if (somethingWeird && itDontLookGood) {
      // Create the implicit Intent to use to start a new Activity.
      Intent intent =
        new Intent(Intent.ACTION_DIAL, Uri.parse("tel:555-2368"));

      // Check if an Activity exists to perform this action.
      PackageManager pm = getPackageManager();
      ComponentName cn = intent.resolveActivity(pm);
      if (cn == null) {
        // There is no Activity available to perform the action
        // Log an error and modify app behavior accordingly,
        // typically by disabling the UI element that would allow
        // users to attempt this action.
        Log.e(TAG, "Intent could not resolve to an Activity.");
      }
      else
        startActivity(intent);
    }
  }

  /*
   * Listing 6-3: Explicitly starting a sub-Activity for a result
   */
  private static final int SHOW_SUBACTIVITY = 1;

  private void startSubActivity() {
    Intent intent = new Intent(this, MyOtherActivity.class);
    startActivityForResult(intent, SHOW_SUBACTIVITY);
  }

  /*
   * Listing 6-4: Implicitly starting a sub-Activity for a result
   */
  private static final int PICK_CONTACT_SUBACTIVITY = 2;

  private void startSubActivityImplicitly() {
    // Create an Intent that requests an Activity capable
    // of allowing users to pick a contact.
    Uri uri = Uri.parse("content://contacts/people");
    Intent intent = new Intent(Intent.ACTION_PICK, uri);
    startActivityForResult(intent, PICK_CONTACT_SUBACTIVITY);
  }

  /*
   * Listing 6-6: Implementing an On Activity Result handler
   */
  private static final int SELECT_HORSE = 1;
  private static final int SELECT_GUN = 2;

  Uri selectedHorse = null;
  Uri selectedGun = null;

  @Override
  public void onActivityResult(int requestCode,
                               int resultCode,
                               Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case (SELECT_HORSE):
        if (resultCode == Activity.RESULT_OK)
          selectedHorse = data.getData();
        break;
      case (SELECT_GUN):
        if (resultCode == Activity.RESULT_OK)
          selectedGun = data.getData();
        break;
      default:
        break;
    }
  }

  private void listing6_10() {
    // Listing 6-10: Generating a list of possible actions
    // to be performed on specific data
    PackageManager packageManager = getPackageManager();

    // Create the intent used to resolve which actions should appear in the menu.
    Intent intent = new Intent();
    intent.setType("vnd.android.cursor.item/vnd.com.professionalandroid.provider.moonbase");
    intent.addCategory(Intent.CATEGORY_SELECTED_ALTERNATIVE);

    // Specify flags. In this case, return all matches
    int flags = PackageManager.MATCH_ALL;

    // Generate the list
    List<ResolveInfo> actions;
    actions = packageManager.queryIntentActivities(intent, flags);

    // Extract the list of action names
    ArrayList<CharSequence> labels = new ArrayList<CharSequence>();
    Resources r = getResources();
    for (ResolveInfo action : actions)
      labels.add(action.nonLocalizedLabel);
  }

  /*
   * Listing 6-11: Dynamic Menu population from advertised actions
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    // Create the intent used to resolve which actions
    // should appear in the menu.
    Intent intent = new Intent();
    intent.setType("vnd.android.cursor.item/vnd.com.professionalandroid.provider.moonbase");
    intent.addCategory(Intent.CATEGORY_SELECTED_ALTERNATIVE);

    // Normal menu options to let you set a group and ID
    // values for the menu items you're adding.
    int menuGroup = 0;
    int menuItemId = 0;
    int menuItemOrder = Menu.NONE;

    // Provide the name of the component that's calling
    // the action -- generally the current Activity.
    ComponentName caller = getComponentName();

    // Define intents that should be added first.
    Intent[] specificIntents = null;

    // The menu items created from the previous Intents
    // will populate this array.
    MenuItem[] outSpecificItems = null;

    // Set any optional flags.
    int flags = Menu.FLAG_APPEND_TO_GROUP;

    // Populate the menu
    menu.addIntentOptions(menuGroup,
      menuItemId,
      menuItemOrder,
      caller,
      specificIntents,
      intent,
      flags,
      outSpecificItems);
    return true;
  }

  TextView myTextView;

  private void listing6_12() {
    // Listing 6-12: Creating custom link strings in Linkify

    // Define the base URI.
    String baseUri = "content://com.paad.earthquake/earthquakes/";

    // Construct an Intent to test if there is an Activity capable of
    // viewing the content you are Linkifying. Use the Package Manager
    // to perform the test.
    PackageManager pm = getPackageManager();
    Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUri));
    boolean activityExists = testIntent.resolveActivity(pm) != null;

    // If there is an Activity capable of viewing the content
    // Linkify the text.
    if (activityExists) {
      int flags = Pattern.CASE_INSENSITIVE;
      Pattern p = Pattern.compile("\\bquake[\\s]?[0-9]+\\b", flags);
      Linkify.addLinks(myTextView, p, baseUri);
    }
  }

  String detectedLifeform = "Alf";
  float mLatitude = 10;
  float mLongitude = 10;

  private void listing6_15() {
    // Listing 6-15: Broadcasting an Intent
    Intent explicitIntent = new Intent(this, LifeformDetectedReceiver.class);
    explicitIntent.putExtra(LifeformDetectedReceiver.EXTRA_LIFEFORM_NAME, detectedLifeform);
    explicitIntent.putExtra(LifeformDetectedReceiver.EXTRA_LATITUDE, mLatitude);
    explicitIntent.putExtra(LifeformDetectedReceiver.EXTRA_LONGITUDE, mLongitude);
    sendBroadcast(explicitIntent);

    Intent intent = new Intent(LifeformDetectedReceiver.NEW_LIFEFORM_ACTION);
    intent.putExtra(LifeformDetectedReceiver.EXTRA_LIFEFORM_NAME, detectedLifeform);
    intent.putExtra(LifeformDetectedReceiver.EXTRA_LATITUDE, mLatitude);
    intent.putExtra(LifeformDetectedReceiver.EXTRA_LONGITUDE, mLongitude);
    sendBroadcast(intent);
  }

  /*
   * Listing 6-17: Registering and unregistering a Broadcast Receiver in code
   */
  private IntentFilter filter = new IntentFilter(LifeformDetectedReceiver.NEW_LIFEFORM_ACTION);
  private LifeformDetectedReceiver receiver = new LifeformDetectedReceiver();

  @Override
  public void onStart() {
    super.onStart();

    // Register the broadcast receiver.
    registerReceiver(receiver, filter);
  }

  @Override
  public void onStop() {
    super.onStop();

    // Unregister the receiver
    unregisterReceiver(receiver);
  }

  /*
   * Listing 6-22: Registering and unregistering a local Broadcast Receiver
   */
  @Override
  public void onResume() {
    super.onResume();

    // Register the broadcast receiver.
    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
    lbm.registerReceiver(receiver, filter);
  }
  @Override
  public void onPause() {
    super.onPause();

    // Unregister the receiver
    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
    lbm.unregisterReceiver(receiver);
  }

  private void listing6_18() {
    //Listing 6-18: Dynamically toggling manifest Receivers
    ComponentName myReceiverName = new ComponentName(this, LifeformDetectedReceiver.class);
    PackageManager pm = getPackageManager();

    // Enable a manifest receiver
    pm.setComponentEnabledSetting(myReceiverName,
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP);

    // Disable a manifest receiver
    pm.setComponentEnabledSetting(myReceiverName,
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP);
  }

  private void listing6_19() {
    // Listing 6-19: Determining docking state
    boolean isDocked = false;
    boolean isCar = false;
    boolean isDesk = false;

    IntentFilter dockIntentFilter = new IntentFilter(Intent.ACTION_DOCK_EVENT);

    Intent dock = registerReceiver(null, dockIntentFilter);

    if (dock != null) {
      int dockState = dock.getIntExtra(Intent.EXTRA_DOCK_STATE,
        Intent.EXTRA_DOCK_STATE_UNDOCKED);

      isDocked = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED;
      isCar = dockState == Intent.EXTRA_DOCK_STATE_CAR;
      isDesk = dockState == Intent.EXTRA_DOCK_STATE_DESK ||
                 dockState == Intent.EXTRA_DOCK_STATE_LE_DESK ||
                 dockState == Intent.EXTRA_DOCK_STATE_HE_DESK;
    }
  }

  private void listing6_20 () {
    // Listing 6-20: Determining battery and charge state information
    IntentFilter batIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent battery = registerReceiver(null, batIntentFilter);

    int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    boolean isCharging =
      status == BatteryManager.BATTERY_STATUS_CHARGING ||
        status == BatteryManager.BATTERY_STATUS_FULL;
  }

  private void listing6_21() {
    // Listing 6-21: Determining connectivity state information
    String svcName = Context.CONNECTIVITY_SERVICE;
    ConnectivityManager cm =
      (ConnectivityManager)getSystemService(svcName);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    boolean isConnected = activeNetwork.isConnectedOrConnecting();
    boolean isMobile = activeNetwork.getType() ==
                         ConnectivityManager.TYPE_MOBILE;
  }
}
