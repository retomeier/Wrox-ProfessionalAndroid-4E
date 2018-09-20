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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private boolean revealed = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button revealButton = findViewById(R.id.reveal_button);
    revealButton.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          if (revealed)
            listing12_4();
          else
            listing12_3();

          revealed = !revealed;

          listing12_5(MainActivity.this);
        }
      }
    });
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void listing12_3() {
    // Listing 12-3: Using a circular reveal to show a View
    final View view = findViewById(R.id.hidden_view);

    // Center the reveal on the middle of the View
    int centerX = view.getWidth() / 2;
    int centerY = view.getHeight() / 2;

    // Determine what radius circle will cover the entire View
    float coveringRadius = (float) Math.hypot(centerX, centerY);

    // Build the circular reveal
    Animator anim = ViewAnimationUtils.createCircularReveal(
      view,
      centerX,
      centerY,
      0,    // initial radius
      coveringRadius // final covering radius
    );

    // Set the View to VISIBLE before starting the animation
    view.setVisibility(View.VISIBLE);
    anim.start();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void listing12_4() {
    final View view = findViewById(R.id.hidden_view);
    int centerX = view.getWidth() / 2;
    int centerY = view.getHeight() / 2;
    float coveringRadius = (float) Math.hypot(centerX, centerY);

    // Listing 12-4: Using a circular reveal to hide a View
    // Build the circular hide animation
    Animator anim = ViewAnimationUtils.createCircularReveal(
      view,
      centerX,
      centerY,
      coveringRadius, // initial radius
      0      // final radius
    );

    anim.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        // Set the view to invisible only at the end of the animation
        view.setVisibility(View.INVISIBLE);
      }
    });

    anim.start();
  }

  private void listing12_5(Context context) {
    TextView userNameView = findViewById(R.id.username_view);
    ImageView avatarView = findViewById(R.id.avatar_view);

    // Listing 12-5: Initiating a shared element Activity transition
    Intent intent = new Intent(context, SecondActivity.class);

    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
      this,
      Pair.create((View) avatarView,
        ViewCompat.getTransitionName(avatarView)),
      Pair.create((View) userNameView,
        ViewCompat.getTransitionName(userNameView))
    ).toBundle();

    startActivity(intent, bundle);
  }
}
