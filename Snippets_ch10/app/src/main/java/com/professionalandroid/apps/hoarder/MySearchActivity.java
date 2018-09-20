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

package com.professionalandroid.apps.hoarder;

import android.app.Application;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Listing 10-32: Performing a search and displaying the results
 */
public class MySearchActivity extends AppCompatActivity
                              implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";
  private MySearchResultRecyclerViewAdapter mAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_search);

    // Set the adapter
    mAdapter = new MySearchResultRecyclerViewAdapter(null, mListener);

    // Update the Recycler View
    RecyclerView resultsRecyclerView = findViewById(R.id.list);
    resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    resultsRecyclerView.setAdapter(mAdapter);

    // Initiate the Cursor Loader
    getSupportLoaderManager().initLoader(0, null, this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    // If the search Activity exists, and another search
    // is performed, set the launch Intent to the newly
    // received search Intent and perform a new search.
    setIntent(intent);
    getSupportLoaderManager().restartLoader(0, null, this);
  }

  @NonNull
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // Extract the search query from the Intent.
    String query = getIntent().getStringExtra(SearchManager.QUERY);

    // Construct the new query in the form of a Cursor Loader.
    String[] projection = {
      HoardDB.HoardContract.KEY_ID,
      HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN,
      HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN
    };

    String where = HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN
                     + " LIKE ?";
    String[] whereArgs = {"%" + query + "%"};
    String sortOrder = HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN +
                         " COLLATE LOCALIZED ASC";

    // Create the new Cursor loader.
    return new CursorLoader(this, MyHoardContentProvider.CONTENT_URI,
      projection, where, whereArgs, sortOrder);
  }

  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
    // Replace the result Cursor displayed by the Cursor Adapter with
    // the new result set.
    mAdapter.setCursor(cursor);
  }

  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    // Remove the existing result Cursor from the List Adapter.
    mAdapter.setCursor(null);
  }

  private OnListItemInteractionListener mListener =
    new OnListItemInteractionListener() {
      @Override
      public void onListItemClick(Uri selectedContent) {
        // TODO If an item is clicked, open an Activity
        // to display further details.
      }
  };

  public class MySearchResultRecyclerViewAdapter
    extends RecyclerView.Adapter<MySearchResultRecyclerViewAdapter.ViewHolder> {

    private Cursor mValues;
    private OnListItemInteractionListener mClickListener;

    private int mHoardIdIndex = -1;
    private int mHoardNameIndex = -1;
    private int mHoardAmountIndex = -1;

    public MySearchResultRecyclerViewAdapter(Cursor items,
                                             OnListItemInteractionListener clickListener) {
      mValues = items;
      mClickListener = clickListener;
    }

    public void setCursor(Cursor items) {
      mValues = items;
      if (items != null) {
        mHoardIdIndex =
          items.getColumnIndex(HoardDB.HoardContract.KEY_ID);

        mHoardNameIndex =
          items.getColumnIndex(
            HoardDB.HoardContract.KEY_GOLD_HOARD_NAME_COLUMN);

        mHoardAmountIndex =
          items.getColumnIndex(
            HoardDB.HoardContract.KEY_GOLD_HOARDED_COLUMN);
      }

      notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.searchresult_item, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
      if (mValues != null) {
        // Move the Cursor to the correct position, extract the
        // search result values, and assign them to the UI for
        // each search result.
        mValues.moveToPosition(position);

        holder.mNameView.setText(mValues.getString(mHoardNameIndex));
        holder.mAmountView.setText(mValues.getString(mHoardAmountIndex));

        // Create a Uri that points to this search result item.
        int rowId = mValues.getInt(mHoardIdIndex);
        final Uri rowAddress =
          ContentUris.withAppendedId(MyHoardContentProvider.CONTENT_URI, rowId);

        // Return the Uri to this search result item if clicked.
        holder.mView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mClickListener.onListItemClick(rowAddress);
          }
        });
      }
    }

    @Override
    public int getItemCount() {
      if (mValues != null)
        return mValues.getCount();
      else
        return 0;
    }

    // View Holder is used as a template to encapsulate the UI
    // for each search result item.
    public class ViewHolder extends RecyclerView.ViewHolder {
      public final View mView;
      public final TextView mNameView;
      public final TextView mAmountView;

      public ViewHolder(View view) {
        super(view);
        mView = view;
        mNameView = view.findViewById(R.id.id);
        mAmountView = view.findViewById(R.id.content);
      }
    }
  }

  // Interface used to encapsulate the behavior when a user
  // clicks on a search result item.
  public interface OnListItemInteractionListener {
    void onListItemClick(Uri selectedContent);
  }
}