package com.professionalandroid.apps.myapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/*
 * Listing 7-7: Implementing a Broadcast Receiver for
 * handling Download Manager broadcasts
 *
 * Listing 7-9: Finding details of completed downloads
 *
 */
public class DownloadsReceiver extends BroadcastReceiver {

  public long myDownloadReference;

  @Override
  public void onReceive(Context context, Intent intent) {
    DownloadManager downloadManager =
      (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

    String extraNotificationFileIds = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
    String extraFileId = DownloadManager.EXTRA_DOWNLOAD_ID;
    String action = intent.getAction();

    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
      long reference = intent.getLongExtra(extraFileId,-1);
      if (myDownloadReference == reference) {
        // Listing 7-9
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        myDownloadQuery.setFilterById(reference);

        Cursor myDownload = downloadManager.query(myDownloadQuery);

        if (myDownload.moveToFirst()) {
          int fileIdIdx =
            myDownload.getColumnIndex(DownloadManager.COLUMN_ID);

          long fileId = myDownload.getLong(fileIdIdx);

          Uri fileUri = downloadManager.getUriForDownloadedFile(fileId);

          // TODO Do something with downloaded file.
        }
        myDownload.close();
      }
    }
    else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
      long[] references = intent.getLongArrayExtra(extraNotificationFileIds);
      for (long reference : references)
        if (myDownloadReference == reference) {
          // TODO Respond to user selecting your file download notification.
        }
    }
  }
}