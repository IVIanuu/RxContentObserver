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

package com.ivianuu.rxcontentobserver

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.annotation.CheckResult
import io.reactivex.Observable

/**
 * Entry point to create content observer observables
 */
object RxContentObserver {

    val DEFAULT_HANDLER = Handler(Looper.getMainLooper())

    /**
     * Emits [Boolean]'s on changes of the [uri]
     */
    @CheckResult
    @JvmStatic
    @JvmOverloads
    fun observe(context: Context,
                uri: Uri,
                handler: Handler = DEFAULT_HANDLER,
                deliverSelfNotifications: Boolean = false,
                notifyForDescendants: Boolean = false
    ): Observable<Boolean> {
        return Observable.create { e ->
            val observer = object : ContentObserver(handler) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    e.onNext(selfChange)
                }

                override fun deliverSelfNotifications(): Boolean {
                    return deliverSelfNotifications
                }
            }

            e.setCancellable { context.contentResolver.unregisterContentObserver(observer) }

            context.contentResolver
                    .registerContentObserver(uri, notifyForDescendants, observer)
        }
    }

    /**
     * Emits [T] on changes and on first subscribe
     */
    @CheckResult
    @JvmOverloads
    @JvmStatic
    inline fun <T> observe(context: Context,
                           uri: Uri,
                           handler: Handler = DEFAULT_HANDLER,
                           deliverSelfNotifications: Boolean = false,
                           notifyForDescendants: Boolean = false,
                           crossinline fetcher: (Uri) -> T
    ): Observable<T> {
        return observe(context, uri, handler, deliverSelfNotifications, notifyForDescendants)
                .map { uri }
                .startWith(uri) // emit current value value
                .map { fetcher(it) }
    }
}