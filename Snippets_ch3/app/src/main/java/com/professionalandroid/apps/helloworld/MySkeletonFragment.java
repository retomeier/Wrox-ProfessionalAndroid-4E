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

package com.professionalandroid.apps.helloworld;

/*
 * Listing 3-6: Fragment skeleton code
 */
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Listing 3-7: Fragment life cycle event handlers
 */
public class MySkeletonFragment extends Fragment {

  public MySkeletonFragment() {
    // Required empty public constructor
  }

  // Called once the Fragment has been created in order for it to
  // create its user interface.
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.my_skeleton_fragment_layout,
      container, false);
  }

  /*
  // Called when the Fragment is attached to its parent Activity.
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    // Get a reference to a Context representing
    // the parent component.
  }
  */

  // Called to do the initial creation of the Fragment.
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize the Fragment.
  }

  // Called once the parent Activity and the Fragment's UI have
  // been created.
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Complete the Fragment initialization – particularly anything
    // that requires the parent Activity to be initialized or the
    // Fragment's view to be fully inflated.
  }

  // Called at the start of the visible lifetime.
  @Override
  public void onStart() {
    super.onStart();

    // Apply any required UI change now that the Fragment is visible.
  }

  // Called at the start of the active lifetime.
  @Override
  public void onResume() {
    super.onResume();

    // Resume any paused UI updates, threads, or processes required
    // by the Fragment but suspended when it became inactive.
  }

  // Called at the end of the active lifetime.
  @Override
  public void onPause() {
    super.onPause();

    // Suspend UI updates, threads, or CPU intensive processes
    // that don't need to be updated when the Activity isn't
    // the active foreground activity.
    // Persist all edits or state changes
    // as after this call the process is likely to be killed.
  }

  // Called to save UI state changes at the
  // end of the active lifecycle.
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);

    // Save UI state changes to the savedInstanceState.
    // This bundle will be passed to onCreate, onCreateView, and
    // onCreateView if the parent Activity is killed and restarted.
  }

  // Called at the end of the visible lifetime.
  @Override
  public void onStop() {
    super.onStop();

    // Suspend remaining UI updates, threads, or processing
    // that aren't required when the Fragment isn't visible.
  }

  // Called when the Fragment's View has been detached.
  @Override
  public void onDestroyView() {
    super.onDestroyView();

    // Clean up resources related to the View.
  }

  // Called at the end of the full lifetime.
  @Override
  public void onDestroy() {
    super.onDestroy();

    // Clean up any resources including ending threads,
    // closing database connections etc.
  }

  /*
  // Called when the Fragment has been detached from its parent Activity.
  @Override
  public void onDetach() {
    super.onDetach();

    // Clean up any references to the parent Activity
    // including references to its Views or classes. Typically setting
    // those references to null.
  }
  */


  /*
   * Listing 3-12: Defining Fragment event callback interfaces
   */

  public interface OnFragmentInteractionListener {
    // TODO Update argument type and name
    void onFragmentInteraction(Uri uri);
  }

  private OnFragmentInteractionListener mListener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
                                   + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

}