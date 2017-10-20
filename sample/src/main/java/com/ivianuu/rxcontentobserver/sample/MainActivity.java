/*
 * Copyright 2017 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.rxcontentobserver.sample;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ivianuu.rxcontentobserver.RxContentObserver;

import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread handlerThread = new HandlerThread("joo");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        Uri uri = Settings.Global.getUriFor(Settings.Global.BLUETOOTH_ON);
        RxContentObserver.observe(this, uri, handler, getFetcher())
                .subscribe(integer -> Toast.makeText(MainActivity.this, "Changed " + integer, Toast.LENGTH_SHORT).show());

    }

    private Function<Uri, Integer> getFetcher() {
        return uri -> Settings.Global.getInt(MainActivity.this.getContentResolver(), uri.getLastPathSegment());
    }
}
