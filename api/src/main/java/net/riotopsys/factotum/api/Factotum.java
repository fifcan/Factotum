/*
 * Copyright 2015 C. A. Fitzgerald
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.riotopsys.factotum.api;

import net.riotopsys.factotum.api.interfaces.ICancelRequest;
import net.riotopsys.factotum.api.interfaces.IOnTaskCompletionCallback;
import net.riotopsys.factotum.api.interfaces.IOnTaskCreationCallback;
import net.riotopsys.factotum.api.internal.FriendlyCompletionService;
import net.riotopsys.factotum.api.internal.FriendlyCompletionService.QueueingFuture;
import net.riotopsys.factotum.api.internal.PauseableThreadPoolExecutor;
import net.riotopsys.factotum.api.internal.PriorityComparator;
import net.riotopsys.factotum.api.internal.RequestCallable;
import net.riotopsys.factotum.api.internal.ResultWrapper;

import java.util.Iterator;
import java.util.concurrent.CompletionService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class Factotum {

    private static final String TAG = Factotum.class.getSimpleName();

    private PauseableThreadPoolExecutor executor;
    private CompletionService completionService;
    private Thread resultCollector;

    private IOnTaskCompletionCallback onTaskCompletionCallback;

    private IOnTaskCreationCallback onTaskCreationCallback;

    private static AtomicLong seq = new AtomicLong(0);

    private ResultCallback resultCallback;

    private Factotum(Builder builder) {

        executor = new PauseableThreadPoolExecutor(
                builder.maximumPoolSize,
                builder.maximumPoolSize,
                builder.keepAliveTime,
                builder.keepAliveTimeUnit,
                new PriorityBlockingQueue<>(1, new PriorityComparator()),
                new ThreadFactory() {
                    public int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName(String.format("%s-%d", TAG, count++));
                        return t;
                    }
                });

        completionService = new FriendlyCompletionService(executor);

        onTaskCompletionCallback = builder.onTaskCompletionCallback;

        onTaskCreationCallback = builder.onTaskCreationCallback;

        resultCallback = new ResultCallback(completionService) {
            @Override
            public void onResult(ResultWrapper result) {
                onTaskCompletionCallback.onTaskCompletion(result);
            }
        };

        resultCollector = new Thread(resultCallback);
        resultCollector.setName(String.format("%s-collector", TAG));
        resultCollector.start();

    }

    public void shutdown() {
        resultCallback.stop();
        executor.shutdownNow();
    }

    public void addRequest(AbstractRequest request) {
        if (request.isCanceled()) {
            return;
        }
        Object handler = request.getTaskHandler();
        onTaskCreationCallback.onTaskCreation(handler);
        completionService.submit(new RequestCallable(request, handler, seq.getAndIncrement()));
    }

    public synchronized void issueCancelation(ICancelRequest cancelRequest) {
        executor.pause();

        Iterator<Runnable> iter = executor.getQueue().iterator();
        while (iter.hasNext()) {
            RequestCallable callable = (RequestCallable) ((QueueingFuture) iter.next()).getOriginalCallable();
            AbstractRequest request = callable.getRequest();

            if (cancelRequest.match(request.getGroup())) {
                request.cancel();
            }

            if (request.isCanceled()) {
                iter.remove();
            }
        }

        executor.resume();
    }

    public static class Builder {

        private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;

        private long keepAliveTime = 1;

        private TimeUnit keepAliveTimeUnit = TimeUnit.MINUTES;

        private IOnTaskCompletionCallback onTaskCompletionCallback = new IOnTaskCompletionCallback() {
            @Override
            public void onTaskCompletion(ResultWrapper object) {
                //noop
            }
        };

        private IOnTaskCreationCallback onTaskCreationCallback = new IOnTaskCreationCallback() {
            @Override
            public void onTaskCreation(Object task) {
                //noop
            }
        };

        public Builder setKeepAliveTime(long keepAliveTime, TimeUnit keepAliveTimeUnit) {

            if (maximumPoolSize <= 0) {
                throw new IllegalArgumentException("max pool size must be greater then 0");
            }
            if (keepAliveTimeUnit == null) {
                throw new IllegalArgumentException("argument cannot be null");
            }
            this.keepAliveTime = keepAliveTime;
            this.keepAliveTimeUnit = keepAliveTimeUnit;
            return this;
        }

        public Builder setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder setOnTaskCompletionCallback(IOnTaskCompletionCallback onTaskCompletionCallback) {
            if (onTaskCompletionCallback == null) {
                throw new IllegalArgumentException("argument cannot be null");
            }
            this.onTaskCompletionCallback = onTaskCompletionCallback;
            return this;
        }

        public Builder setOnTaskCreationCallback(IOnTaskCreationCallback onTaskCreationCallback) {
            if (onTaskCreationCallback == null) {
                throw new IllegalArgumentException("argument cannot be null");
            }
            this.onTaskCreationCallback = onTaskCreationCallback;
            return this;
        }

        public Factotum build() {
            return new Factotum(this);
        }
    }

}
