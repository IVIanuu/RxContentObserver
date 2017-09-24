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

package com.ivianuu.rxcontentobserver;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

/**
 * Entry point to create content observer observables
 */
public final class RxContentObserver {

    private RxContentObserver() {
        // no instances
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri) {
        return observe(context, uri, true);
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri,
                                              boolean deliverSelfNotifications) {
        return observe(context, uri, deliverSelfNotifications, false);
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri,
                                              boolean deliverSelfNotifications,
                                              boolean notifyForDescendants) {
        return ContentObserverObservable.create(
                context, uri, deliverSelfNotifications, notifyForDescendants);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @NonNull Function<Uri, T> fetcher) {
        return observe(context, uri, fetcher, true);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @NonNull Function<Uri, T> fetcher,
                                            boolean deliverSelfNotifications) {
        return observe(
                context, uri, fetcher, deliverSelfNotifications, false);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @NonNull Function<Uri, T> fetcher,
                                            boolean deliverSelfNotifications,
                                            boolean notifyForDescendants) {
        return observe(context, uri, deliverSelfNotifications, notifyForDescendants)
                .map(__ -> uri)
                .startWith(uri) // emit current value value
                .map(fetcher);
    }
}
