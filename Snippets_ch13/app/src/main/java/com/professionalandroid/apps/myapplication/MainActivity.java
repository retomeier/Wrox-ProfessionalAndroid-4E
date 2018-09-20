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

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  /*
   * Listing 13-9: Setting a Toolbar as your App Bar
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.basic_toolbar_activity);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  /*
   * Listing 13-3: Adding a Menu to an Activity
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // You should always call super.onCreateOptionsMenu()
    // to ensure this call is also dispatched to Fragments
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.my_menu, menu);

    return true;
  }

  /*
   * Listing 13-5: Modifying Menu Items dynamically
   */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    MenuItem menuItem = menu.findItem(R.id.action_filter);

    // Modify Menu Items
    menuItem.setVisible(false);
    return true;
  }

  /*
   * Listing 13-6: Handling Menu Item selections
   */
  public boolean onOptionsItemSelected(MenuItem item) {
    // Find which Menu Item has been selected
    switch (item.getItemId()) {
      // Check for each known Menu Item
      case (R.id.action_settings): {
        // TODO [ ... Perform menu handler actions ... ]
        return true;
      }

      // Pass on any unhandled Menu Items to super.onOptionsItemSelected
      // This is required to ensure that the up button and Fragment Menu Items
      // are dispatched properly.
      default: return super.onOptionsItemSelected(item);
    }
  }

  private void listing13_25() {
    // Listing 13-25: Displaying a Dialog Fragment
    String tag = "warning_dialog";
    DialogFragment dialogFragment = new PitchBlackDialogFragment();
    dialogFragment.show(getSupportFragmentManager(), tag);
  }

  private void listing13_26() {
    // Listing 13-26: Displaying a Toast
    Context context = this;
    String msg = "To health and happiness!";
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, msg, duration);

    // Remember, you must *always* call show()
    toast.show();
  }

  private void listing13_27() {
    View coordinatorLayout = findViewById(R.id.coordinatorlayout);
    // Listing 13-27: Building and showing a Snackbar
    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Deleted", Snackbar.LENGTH_LONG);

    // Define the action
    snackbar.setAction("Undo", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO Undo the deletion
      }
    });

    // React to the Snackbar being dismissed
    snackbar.addCallback(new Snackbar.Callback() {
      @Override
      public void onDismissed(Snackbar transientBottomBar, int event) {
        // TODO Finalize the deletion
      }
    });

    // Show the Snackbar
    snackbar.show();
  }
}
