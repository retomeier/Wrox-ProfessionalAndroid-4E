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
 * Listing 11-38: A skeleton Service class
 * Listing 11-41: Overriding Service restart behavior
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  // Listing 11-41: Overriding Service restart behavior
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // TODO Start your work on a background thread
    return START_STICKY;
  }
}