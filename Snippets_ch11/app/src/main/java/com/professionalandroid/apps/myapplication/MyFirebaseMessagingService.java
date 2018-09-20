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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/*
 * Listing 11-28: Handling the Firebase Notification callback
 * Listing 11-31: Receiving data using the Firebase Messaging Service
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(RemoteMessage message) {
    RemoteMessage.Notification notification = message.getNotification();
    if (notification != null) {
      String title = notification.getTitle();
      String body = notification.getBody();
      // Post your own notification using NotificationCompat.Builder
      // or send the information to your UI
    }

    // Listing 11-31: Receiving data using the Firebase Messaging Service
    Map<String,String> data = message.getData();
    if (data != null) {
      String value = data.get("your_key");
      // Post your own Notification using NotificationCompat.Builder
      // or send the information to your UI
    }
  }
}