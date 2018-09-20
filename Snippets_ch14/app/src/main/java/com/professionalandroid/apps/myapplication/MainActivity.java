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

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  private static final int VOICE_RECOGNITION = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /*
   * Listing 14-1: Using text-to-speech
   */
  private static int TTS_DATA_CHECK = 1;

  private TextToSpeech tts = null;
  private boolean ttsIsInit = false;

  private void initTextToSpeech() {
    Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
    startActivityForResult(intent, TTS_DATA_CHECK);
  }

  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    if (requestCode == TTS_DATA_CHECK) {
      if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
          public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
              ttsIsInit = true;
              if (tts.isLanguageAvailable(Locale.UK) >= 0)
                tts.setLanguage(Locale.UK);
              tts.setPitch(0.8f);
              tts.setSpeechRate(1.1f);
              speak();
            }
          }
        });
      } else {
        Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        startActivity(installVoice);
      }
    }

    // Listing 14-3: Finding the results of a speech recognition request
    if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK) {
      ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      float[] confidence = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
      // TODO Do something with the recognized voice strings
    }
  }

  private void speak() {
    if (tts != null && ttsIsInit) {
      tts.speak("Hello, Android old chap!", TextToSpeech.QUEUE_ADD, null);
    }
  }

  @Override
  public void onDestroy() {
    if (tts != null) {
      tts.stop();
      tts.shutdown();
    }
    super.onDestroy();
  }

  private void listing14_2() {
    // Listing 14-2: Initiating a speech recognition request
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

    // Specify free form input
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
      RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "or forever hold your peace");
    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

    startActivityForResult(intent, VOICE_RECOGNITION);
  }

  private void listing14_4() {
    // Listing 14-4: Finding the results of a speech recognition request
    Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

    startActivityForResult(intent, 0);
  }

  private void listing14_5(View myView) {
    // Listing 14-5: Reacting to changes in system UI visibility
    myView.setOnSystemUiVisibilityChangeListener(
      new View.OnSystemUiVisibilityChangeListener() {
        public void onSystemUiVisibilityChange(int visibility) {
          if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
            // TODO Display Action Bar and Status Bar
          }
          else {
            // TODO Hide Action Bar and Status Bar
          }
        }
      });
  }
}