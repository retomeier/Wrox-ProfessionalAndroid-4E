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
 * Listing 11-7: A simple Job Service class
 */
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SimpleJobService extends JobService {

  private AsyncTask<Void, Void, Boolean> mJobTask;

  @Override
  public boolean onStartJob(JobParameters params) {

    // TODO Do work directly on the main Thread
    return false; // if no time consuming work remains to be done.
  }

  @Override
  public boolean onStopJob(JobParameters params) {
    // Return false if the job does not need to be rescheduled
    return false;
  }
}