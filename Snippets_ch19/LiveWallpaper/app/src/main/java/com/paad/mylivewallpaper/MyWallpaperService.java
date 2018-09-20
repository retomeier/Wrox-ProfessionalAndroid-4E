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

package com.paad.mylivewallpaper;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/*
 * Listing 19-23: Wallpaper Service Engine skeleton code
 * Listing 19-24: Creating a Wallpaper Service
 */
public class MyWallpaperService extends WallpaperService {

  // Listing 19-24: Creating a Wallpaper Service
  @Override
  public Engine onCreateEngine() {
    return new MyWallpaperServiceEngine();
  }

  public class MyWallpaperServiceEngine extends WallpaperService.Engine {

    private static final int FPS = 30;
    private final Handler handler = new Handler();

    @Override
    public void onCreate(SurfaceHolder surfaceHolder) {
      super.onCreate(surfaceHolder);
      // TODO Handle initialization.
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xOffsetStep, float yOffsetStep,
                                 int xPixelOffset, int yPixelOffset) {
      super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
      // Triggered whenever the user swipes between multiple
      // home-screen panels.
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
      super.onTouchEvent(event);
      // Triggered when the Live Wallpaper receives a touch event
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
      super.onSurfaceCreated(holder);
      // TODO Surface has been created, begin the update loop that will
      // update the Live Wallpaper.
      drawFrame();
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
      handler.removeCallbacks(drawSurface);
      super.onSurfaceDestroyed(holder);
    }

    private synchronized void drawFrame() {
      final SurfaceHolder holder = getSurfaceHolder();

      if (holder != null && holder.getSurface().isValid()) {
        Canvas canvas = null;
        try {
          canvas = holder.lockCanvas();
          if (canvas != null) {
            // Draw on the Canvas!
          }
        } finally {
          if (canvas != null && holder != null)
            holder.unlockCanvasAndPost(canvas);
        }

        // Schedule the next frame
        handler.removeCallbacks(drawSurface);
      }
      handler.postDelayed(drawSurface, 1000 / FPS);
    }

    // Runnable used to allow you to schedule frame draws.
    private final Runnable drawSurface = new Runnable() {
      public void run() {
        drawFrame();
      }
    };
  }
}