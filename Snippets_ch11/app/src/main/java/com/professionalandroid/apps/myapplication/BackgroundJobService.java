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

 /*
  * Listing 11-8: A Job Service using an Async Task
  */
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BackgroundJobService extends JobService {
  private AsyncTask<Void, Void, Boolean> mJobTask;

  @Override
  public boolean onStartJob(final JobParameters params) {
    // TODO Do work directly on the main Thread
    // return false; // if no time consuming work remains to be done.

    // Execute additional work within a background thread.
    mJobTask = new AsyncTask<Void, Void, Boolean>() {
      @Override
      protected Boolean doInBackground(Void... voids) {
        // TODO Do your background work.
        // Return true if the job succeeded or false if it should be
        // rescheduled due to a transient failure
        return true;
      }

      @Override
      protected void onPostExecute(Boolean success) {
        // Reschedule the job if it did not succeed
        jobFinished(params, !success);
      }
    };
    mJobTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    // You must return true to signify that you're doing work
    // in the background
    return true;
  }

  @Override
  public boolean onStopJob(JobParameters params) {
    if (mJobTask != null) {
      mJobTask.cancel(true);
    }
    // If we had to interrupt the job, reschedule it
    return true;

    // Return false if the job does not need to be rescheduled

  }
}