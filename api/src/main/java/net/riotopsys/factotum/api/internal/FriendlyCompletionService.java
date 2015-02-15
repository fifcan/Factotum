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

package net.riotopsys.factotum.api.internal;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public class FriendlyCompletionService<V> implements CompletionService<V> {
    private final Executor executor;
    private final AbstractExecutorService aes;
    private final BlockingQueue<Future<V>> completionQueue;

    public class QueueingFuture extends FutureTask<Void> {
        QueueingFuture(RunnableFuture<V> task, Callable<V> callable) {
            super(task, null);
            this.task = task;
            this.callable = callable;
        }

        protected void done() {
            completionQueue.add(task);
        }

        private final Future<V> task;
        private final Callable<V> callable;

        public Callable<V> getOriginalCallable() {
            return callable;
        }

    }

    private RunnableFuture<V> newTaskFor(Callable<V> task) {
        return new FutureTask<V>(task);
    }

    public FriendlyCompletionService(Executor executor) {
        if (executor == null)
            throw new NullPointerException();
        this.executor = executor;

        if (executor instanceof AbstractExecutorService) {
            this.aes = (AbstractExecutorService) executor;
        } else {
            this.aes = null;
        }

        this.completionQueue = new LinkedBlockingQueue<Future<V>>();
    }

    public FriendlyCompletionService(Executor executor,
                                     BlockingQueue<Future<V>> completionQueue) {
        if (executor == null || completionQueue == null)
            throw new NullPointerException();
        this.executor = executor;
        if (executor instanceof AbstractExecutorService) {
            this.aes = (AbstractExecutorService) executor;
        } else {
            this.aes = null;
        }
        this.completionQueue = completionQueue;
    }

    public Future<V> submit(Callable<V> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task);
        executor.execute(new QueueingFuture(f, task));
        return f;
    }

    public Future<V> submit(Runnable task, V result) {
        throw new UnsupportedOperationException();
    }

    public Future<V> take() throws InterruptedException {
        return completionQueue.take();
    }

    public Future<V> poll() {
        return completionQueue.poll();
    }

    public Future<V> poll(long timeout, TimeUnit unit)
            throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

}
