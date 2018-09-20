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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class MyActivity extends AppCompatActivity {

  /*
   * Listing 11-5: Moving processing to a background Handler Thread
   * Listing 11-6: Sending information between Threads with Messages
   */
  private HandlerThread mWorkerThread;
  private Handler mHandler;

  private static final int BACKGROUND_WORK = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mWorkerThread = new HandlerThread("WorkerThread");
    mWorkerThread.start();

    mHandler = new Handler(mWorkerThread.getLooper());

    // Listing 11-6
    mHandler = new Handler(mWorkerThread.getLooper(),
      new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
          if (msg.what == BACKGROUND_WORK) {
            // TODO [ ... Time consuming operations ... ]
          }
          // else, handle a different type of message
          return false;
        }
      });
  }

  // This method is called on the main Thread.
  private void doBackgroundExecution() {
    mHandler.post(new Runnable() {
      public void run() {
        // TODO [ ... Time consuming operations ... ]
      }
    });

    // Listing 11-6
    mHandler.sendEmptyMessage(BACKGROUND_WORK);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      mWorkerThread.quitSafely();
    } else mWorkerThread.quit();
  }

  /*
   * Listing 11-1: An Asynchronous Task defi nition
   */

  // The Views in your UI that you want to update from the AsyncTask
  private ProgressBar asyncProgress;
  private TextView asyncTextView;

  private class MyAsyncTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... parameter) {
      // Moved to a background Thread.
      String result = "";
      int myProgress = 0;
      int inputLength = parameter[0].length();

      // Perform background processing task, update myProgress]
      for (int i = 1; i <= inputLength; i++) {
        myProgress = i;
        result = result + parameter[0].charAt(inputLength - i);
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        publishProgress(myProgress);
      }

      // Return the value to be passed to onPostExecute
      return result;
    }

    @Override
    protected void onPreExecute() {
      // Synchronized to UI Thread.
      // Update the UI to indicate that background loading is occurring
      asyncProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
      // Synchronized to UI Thread.
      // Update progress bar, Notification, or other UI elements
      asyncProgress.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
      // Synchronized to UI Thread.
      // Report results via UI update, Dialog, or Notifications
      asyncProgress.setVisibility(View.GONE);
      asyncTextView.setText(result);
    }
  }

  private void listing11_2() {
    // Listing 11-2: Executing an Async Task
    String input = "redrum ... redrum";
    new MyAsyncTask().execute(input);
  }

  private void listing11_3() {
    // Listing 11-3: Executing Async Tasks in parallel
    String input = "redrum ... redrum";
    new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, input);
  }

  /*
   * Listing 11-10: Scheduling a job that requires unmetered network and charging
   */

  // Can be any integer, just needs to be unique across your app
  private static final int BACKGROUND_UPLOAD_JOB_ID = 13;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public static void scheduleBackgroundUpload(Context context) {

    // Access the Job Scheduler
    JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

    // Get a reference to my Job Service implementation
    ComponentName jobServiceName = new ComponentName(
      context, BackgroundJobService.class);

    // Build a Job Info to run my Job Service
    jobScheduler.schedule(
      new JobInfo.Builder(BACKGROUND_UPLOAD_JOB_ID, jobServiceName)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
        .setRequiresCharging(true)
        // Wait at most a day before relaxing our network constraints
        .setOverrideDeadline(TimeUnit.DAYS.toMillis(1))
        .build());
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private static void listing11_11(Context context) {
    JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    ComponentName jobServiceName = new ComponentName(context, BackgroundJobService.class);

    // Listing 11-11: Scheduling a job with customized back-off criteria
    jobScheduler.schedule(
      new JobInfo.Builder(BACKGROUND_UPLOAD_JOB_ID, jobServiceName)
        // Require a network connection
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        // Require the device has been idle
        .setRequiresDeviceIdle(true)
        // Force Job to ignore constraints after 1 day
        .setOverrideDeadline(TimeUnit.DAYS.toMillis(1))
        // Retry after 30 seconds, with linear back-off
        .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_LINEAR)
        // Reschedule after the device has been rebooted
        .setPersisted(true)
        .build());
  }

  /*
   * Listing 11-14: Scheduling a job that requires unmetered network and
   * charging using the Firebase Job Dispatcher
   */
  private static final String BACKGROUND_UPLOAD_JOB_TAG = "background_upload";

  public static void scheduleFirebaseBackgroundUpload(Context context) {
    FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

    jobDispatcher.mustSchedule(
      jobDispatcher.newJobBuilder()
        .setTag(BACKGROUND_UPLOAD_JOB_TAG)
        .setService(FirebaseJobService.class)
        .setConstraints(
          Constraint.ON_UNMETERED_NETWORK,
          Constraint.DEVICE_CHARGING)
        .setTrigger(Trigger.executionWindow(
          0, // can start immediately
          (int) TimeUnit.DAYS.toSeconds(1))) // wait at most a day
        .build());
  }

  private void listing11_15() {
    // Listing 11-15: Using the Notification Manager
    NotificationManagerCompat notificationManager =
      NotificationManagerCompat.from(this);
  }

  /*
   * Listing 11-16: Creating a Notification Channel
   */
  private static final String MESSAGES_CHANNEL = "messages";

  public void createMessagesNotificationChannel(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = context.getString(R.string.messages_channel_name);
      NotificationChannel channel = new NotificationChannel(
        MESSAGES_CHANNEL,
        name,
        NotificationManager.IMPORTANCE_HIGH);

      NotificationManager notificationManager =
        context.getSystemService(NotificationManager.class);

      notificationManager.createNotificationChannel(channel);
    }
  }

  private void listing11_17(Context context) {
    NotificationManagerCompat notificationManager =
      NotificationManagerCompat.from(this);

    // Listing 11-17: Creating and posting a Notification
    final int NEW_MESSAGE_ID = 0;
    createMessagesNotificationChannel(context);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(
      context, MESSAGES_CHANNEL);

    // These would be dynamic in a real app
    String title = "Reto Meier";
    String text = "Interested in a new book recommendation?" +
                    " I have one you should check out!";

    builder.setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(text);

    notificationManager.notify(NEW_MESSAGE_ID, builder.build());
  }

  private void listing11_18(Context context, NotificationCompat.Builder builder) {
    // Listing 11-18: Adding a content Intent to start an Activity

    // This could be any Intent. Here we use the app's
    // launcher activity as a simple example
    Intent launchIntent = context.getPackageManager()
                            .getLaunchIntentForPackage(context.getPackageName());
    PendingIntent contentIntent = TaskStackBuilder.create(context)
                                    .addNextIntentWithParentStack(launchIntent)
                                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    builder.setContentIntent(contentIntent);
  }

  private void listing11_19(NotificationCompat.Builder builder, Context context) {
    String title = "This is a title";
    String text = "This is the body text, that goes on for some length";
    Bitmap profilePicture = BitmapFactory.decodeResource(context.getResources(),
                                                         R.drawable.ic_launcher_background);

    // Listing 11-19: Applying a Big Text Style to a Notification
    builder.setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(text)
      .setLargeIcon(profilePicture)
      .setStyle(new NotificationCompat.BigTextStyle().bigText(text));
  }

  private void listing11_20(NotificationCompat.Builder builder, Context context) {
    String title = "This is a title";
    String text = "This is the body text, that goes on for some length";
    Bitmap profilePicture = BitmapFactory.decodeResource(context.getResources(),
      R.drawable.ic_notification);
    Bitmap aBigBitmap = BitmapFactory.decodeResource(context.getResources(),
      R.drawable.ic_launcher_background);

    // Listing 11-20: Applying a Big Picture Style to a Notification
    builder.setSmallIcon(R.drawable.ic_notification)
      .setContentTitle(title)
      .setContentText(text)
      .setLargeIcon(profilePicture)
      .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(aBigBitmap));
  }

  private void listing11_21(NotificationCompat.Builder builder) {
    String userDisplayName = "Ian Lake";
    long message1TimeInMillis = currentTimeMillis() - (1000*20);
    long message2TimeInMillis = message1TimeInMillis + (1000*10);
    long message3TimeInMillis = message1TimeInMillis + (1000*5);
    String fromMe = null;

    // Listing 11-21: Creating a Messaging Style Notification
    builder
      .setShowWhen(true) // Show the time the Notification was posted
      .setStyle(new NotificationCompat.MessagingStyle(userDisplayName)
                  .addMessage("Hi Reto!", message1TimeInMillis, "Ian Lake")
                  .addMessage("How's it going?", message2TimeInMillis, "Ian Lake")
                  .addMessage("Very well indeed. And you?", message3TimeInMillis, fromMe));
  }

  private void listing11_22(NotificationChannel channel, NotificationCompat.Builder builder) {
    // Listing 11-22: Customizing a Notification's alerts
    // For Android 8.0+ higher devices:
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
      channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
      channel.setLightColor(Color.RED);
    } else {
      // For Android 7.1 or lower devices:
      builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(
          RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
        .setLights(Color.RED, 0, 1);
    }
  }

  private void listing11_23(NotificationCompat.Builder builder) {
    // Listing 11-23: Setting a Notification category and sender
    builder.setCategory(NotificationCompat.CATEGORY_CALL)
      .addPerson("tel:5558675309");
  }

  private void listing11_24(Context context, NotificationCompat.Builder builder) {
    Uri emailUri = Uri.parse("retomeier@notarealaddress.com");

    // Listing 11-24: Adding a Notification action
    Intent deleteAction = new Intent(context, DeleteBroadcastReceiver.class);

    deleteAction.setData(emailUri);
    PendingIntent deleteIntent = PendingIntent.getBroadcast(context, 0,
      deleteAction, PendingIntent.FLAG_UPDATE_CURRENT);

    builder.addAction(
      new NotificationCompat.Action.Builder(
        R.drawable.delete,
        context.getString(R.string.delete_action),
        deleteIntent).build());
  }

  private void listing11_25(Context context, NotificationCompat.Builder builder, Uri chatThreadUri) {
    // Listing 11-25: Adding a direct reply action

    // The key you'll use to later retrieve the reply
    final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";

    Intent replyAction = new Intent(context, ReplyBroadcastReceiver.class);
    replyAction.setData(chatThreadUri);

    PendingIntent replyIntent = PendingIntent.getBroadcast(context, 0,
      replyAction, PendingIntent.FLAG_UPDATE_CURRENT);

    // Construct the RemoteInput
    RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                .setLabel(context.getString(R.string.reply_hint_text))
                                .build();

    builder.addAction(
      new NotificationCompat.Action.Builder(
        R.drawable.reply,
        context.getString(R.string.reply_action),
        replyIntent)
        .addRemoteInput(remoteInput)
        .setAllowGeneratedReplies(true)
        .extend(new NotificationCompat.Action.WearableExtender()
                  .setHintDisplayActionInline(true))
        .build());
  }

  private void listing11_26(NotificationCompat.Builder builder) {
    String[] emailSubjects = {"Testing", "Feedback"};
    String accountName = "Home Gmail";

    // Listing 11-26: Building an InboxStyle group summary Notification
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    for (String emailSubject : emailSubjects)
      inboxStyle.addLine(emailSubject);

    builder.setSubText(accountName)
      .setGroup(accountName)
      .setGroupSummary(true)
      .setStyle(inboxStyle);
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Listing 11-30: Receiving data from a Firebase Notification
    Intent intent = getIntent();
    if (intent != null) {
      String value = intent.getStringExtra("your_key");
      // Change your behavior based on the value such as starting
      // the appropriate deep link activity
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private void listing11_32_to_34() {
    // Listing 11-32: Creating an alarm that triggers at the top of the hour
    // Get a reference to the Alarm Manager
    AlarmManager alarmManager =
      (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    // Find the trigger time
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.add(Calendar.HOUR, 1);
    long time = calendar.getTimeInMillis();

    // Create a Pending Intent that will broadcast and action
    String ALARM_ACTION = "ALARM_ACTION";
    Intent intentToFire = new Intent(ALARM_ACTION);
    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0,
      intentToFire, 0);

    // Set the alarm
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmIntent);

    // LISTING 11-33: Canceling an Alarm
    alarmManager.cancel(alarmIntent);

    // Listing 11-34: Setting an Alarm Clock
    // Create a Pending Intent that can be used to show or edit the alarm clock
    // when the alarm clock icon is touched
    Intent alarmClockDetails = new Intent(this, AlarmClockActivity.class);

    PendingIntent showIntent = PendingIntent.getActivity(this, 0,
      alarmClockDetails, 0);

    // Set the alarm clock, which will fire the alarmIntent at the set time
    alarmManager.setAlarmClock(
      new AlarmManager.AlarmClockInfo(time, showIntent),
      alarmIntent);
  }

  /*
   * Listing 11-36: Creating a Service Connection for Service binding
   */
  // Reference to the service
  private MyBoundService serviceRef;

  // Handles the connection between the service and activity
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {

      // Called when the connection is made.
      serviceRef = ((MyBoundService.MyBinder)service).getService();
    }

    public void onServiceDisconnected(ComponentName className) {
      // Received when the service unexpectedly disconnects.
      serviceRef = null;
    }
  };

  // Listing 11-37: Binding to a Service
  private void listing11_37() {
    // Bind to the service
    Intent bindIntent = new Intent(MyActivity.this, MyBoundService.class);
    bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
  }

  private void listing11_39() {
    // Listing 11-39: Starting a Service
    // Explicitly start My Service
    Intent intent = new Intent(this, MyService.class);
    intent.setAction("Upload");
    intent.putExtra("TRACK_NAME", "Best of Chet Haase");
    startService(intent);
  }

  private void listing11_40() {
    // Listing 11-40: Stopping a Service
    // Stop a service explicitly.
    stopService(new Intent(this, MyService.class));
  }
}