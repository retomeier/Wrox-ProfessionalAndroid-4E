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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "CHAPTER8_SNIPPETS";

  private static final int PICTURE_REQUEST_CODE = 1;
  private static final int REQUEST_DIRECTORY_CODE = 2;

  /*
   * Listing 8-1: Programmatically adding a Fragment in onCreate
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    if (savedInstanceState == null) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.add(R.id.fragment_container, new MainFragment());
      ft.commit();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void listing8_4() {
    // Listing 8-4: Requesting access with Scoped Directory Access
    StorageManager sm =
      (StorageManager) getSystemService(Context.STORAGE_SERVICE);

    StorageVolume volume = sm.getPrimaryStorageVolume();
    Intent intent = volume.createAccessIntent(Environment.DIRECTORY_PICTURES);

    startActivityForResult(intent, PICTURE_REQUEST_CODE);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  /*
   * Listing 8-5: Receiving access with Scoped Directory Access
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
      Uri documentTreeUri = data.getData();

      // Use the returned URI to access the files within the directory
      handleDocumentTreeUri86(documentTreeUri);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  /*
   * Listing 8-6: Using Document Contract to parse a document tree
   */
  private void handleDocumentTreeUri86(Uri documentTreeUri) {
    Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
      documentTreeUri, DocumentsContract.getDocumentId(documentTreeUri));

    try (Cursor children = getContentResolver().query(childrenUri,
      new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        DocumentsContract.Document.COLUMN_MIME_TYPE},
      null /* selection */,
      null /* selectionArgs */,
      null /* sortOrder */)) {
        if (children == null) {
          return;
        }

        while (children.moveToNext()) {
          String documentId = children.getString(0);
          String mimeType = children.getString(1);
          Uri childUri = DocumentsContract.buildDocumentUriUsingTree(
            documentTreeUri, documentId);

          if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType)) {
            handleDocumentTreeUri86(childUri);
          } else {
            try (InputStream in = getContentResolver().openInputStream(childUri)) {
              // TODO Read the file
            } catch (FileNotFoundException e) {
              Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
              Log.e(TAG, e.getMessage(), e);
            }
          }
        }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  /*
   * Listing 8-7: Using Document File to parse directory document tree
   */
  private void handleDocumentTreeUri87(Uri documentTreeUri) {
    DocumentFile directory = DocumentFile.fromTreeUri(
      this, // Context
      documentTreeUri);

    DocumentFile[] files = directory.listFiles();

    for (DocumentFile file : files) {
      if (file.isDirectory()) {
        handleDocumentTreeUri87(file.getUri());
      } else {
        try (InputStream in =
               getContentResolver().openInputStream(file.getUri())) {
          // TODO Read the file
        } catch (FileNotFoundException e) {
          Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
          Log.e(TAG, e.getMessage(), e);
        }
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void listing8_8() {
    // Listing 8-8: Requesting access to a directory with the Storage Access Framework
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    startActivityForResult(intent, REQUEST_DIRECTORY_CODE);
  }
}