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
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.ivianuu.rxcontentobserver.Preconditions.checkNotNull;

/**
 * Entry point to create content observer observables
 */
public final class RxContentObserver {

    private static final Handler DEFAULT_HANDLER = new Handler(Looper.getMainLooper());

    private RxContentObserver() {
        // no instances
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri) {
        return observe(context, uri, (Handler) null);
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri,
                                              @Nullable Handler handler) {
        return observe(context, uri, handler, false);
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri,
                                              @Nullable Handler handler,
                                              boolean deliverSelfNotifications) {
        return observe(context, uri, handler, deliverSelfNotifications, false);
    }

    /**
     * Emits on changes of the uri
     */
    @CheckResult @NonNull
    public static Observable<Boolean> observe(@NonNull Context context,
                                              @NonNull Uri uri,
                                              @Nullable Handler handler,
                                              boolean deliverSelfNotifications,
                                              boolean notifyForDescendants) {
        checkNotNull(context, "context == null");
        checkNotNull(uri, "uri == null");
        if (handler == null) handler = DEFAULT_HANDLER;
        return ContentObserverObservable.create(
                context, uri, handler, deliverSelfNotifications, notifyForDescendants);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @NonNull Function<Uri, T> fetcher) {
        return observe(context, uri, null, fetcher);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @Nullable Handler handler,
                                            @NonNull Function<Uri, T> fetcher) {
        return observe(context, uri, handler, true, fetcher);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @Nullable Handler handler,
                                            boolean deliverSelfNotifications,
                                            @NonNull Function<Uri, T> fetcher) {
        return observe(
                context, uri, handler, deliverSelfNotifications, false, fetcher);
    }

    /**
     * Emits the value on changes and on first subscribe
     */
    @CheckResult @NonNull
    public static <T> Observable<T> observe(@NonNull Context context,
                                            @NonNull Uri uri,
                                            @Nullable Handler handler,
                                            boolean deliverSelfNotifications,
                                            boolean notifyForDescendants,
                                            @NonNull Function<Uri, T> fetcher) {
        checkNotNull(fetcher, "fetcher == null");
        return observe(context, uri, handler, deliverSelfNotifications, notifyForDescendants)
                .map(__ -> uri)
                .startWith(uri) // emit current value value
                .map(fetcher);
    }
}
