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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

/*
 * Listing 13-24: Configuring an Alert Dialog in an AppCompat Dialog Fragment
 */
public class PitchBlackDialogFragment extends AppCompatDialogFragment {
  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("It is Pitch Black")
      .setMessage("You are likely to be eaten by a Grue.")
      .setPositiveButton(
        "Move Forward",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int arg1) {
            eatenByGrue();
          }
        })
      .setNegativeButton(
        "Go Back",
        new DialogInterface.OnClickListener(){
          @Override
          public void onClick(DialogInterface dialog, int arg1) {
            // do nothing
          }
        });

    // Create and return the AlertDialog
    return builder.create();
  }

  private void eatenByGrue() {
    // TODO Handle being eaten.
  }
}