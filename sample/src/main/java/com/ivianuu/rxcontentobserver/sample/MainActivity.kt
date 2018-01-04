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

package com.ivianuu.rxcontentobserver.sample

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import com.ivianuu.rxcontentobserver.RxContentObserver

import io.reactivex.functions.Function

class MainActivity : AppCompatActivity() {

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handlerThread = HandlerThread("joo")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val uri = Settings.Global.getUriFor(Settings.Global.BLUETOOTH_ON)
        RxContentObserver.observe(this, uri, handler) {
            Settings.Global.getInt(this@MainActivity.contentResolver, uri.lastPathSegment) }
                .subscribe { integer ->
                    Toast.makeText(this@MainActivity, "Changed " + integer,
                            Toast.LENGTH_SHORT).show()
                }

    }
}
