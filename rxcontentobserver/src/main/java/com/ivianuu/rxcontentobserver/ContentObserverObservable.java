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

/**
 * Wraps an content observer and emits the values
 */
final class ContentObserverObservable implements ObservableOnSubscribe<Boolean> {

    private static final Handler HANDLER = new Handler();

    private final Context context;
    private final Uri uri;
    private final boolean deliverSelfNotifications;
    private final boolean notifyForDescendants;

    private ContentObserverObservable(Context context,
                                      Uri uri,
                                      boolean deliverSelfNotifications,
                                      boolean notifyForDescendants) {
        this.context = context;
        this.uri = uri;
        this.deliverSelfNotifications = deliverSelfNotifications;
        this.notifyForDescendants = notifyForDescendants;
    }

    /**
     * Emits on changes of the underlying content observer
     */
    @CheckResult @NonNull
    static Observable<Boolean> create(@NonNull Context context,
                                     @NonNull Uri uri,
                                     boolean deliverSelfNotifications,
                                     boolean notifyForDescendants) {
        return Observable.create(new ContentObserverObservable(
                context, uri, deliverSelfNotifications, notifyForDescendants));
    }

    @Override
    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
        ContentObserver observer = new ContentObserver(HANDLER) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (!e.isDisposed()) {
                    e.onNext(selfChange);
                }
            }

            @Override
            public boolean deliverSelfNotifications() {
                return deliverSelfNotifications;
            }
        };

        e.setCancellable(() -> context.getContentResolver().unregisterContentObserver(observer));

        context.getContentResolver()
                .registerContentObserver(uri, notifyForDescendants, observer);
    }
}
