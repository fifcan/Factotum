package net.riotopsys.factotum.api.internal;

import java.util.concurrent.*;

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
        protected void done() { completionQueue.add(task); }
        private final Future<V> task;
        private final Callable<V> callable;

        public Callable<V> getOriginalCallable() {
            return callable;
        }

    }

    private RunnableFuture<V> newTaskFor(Callable<V> task) {
        return new FutureTask<V>(task);
    }

    private RunnableFuture<V> newTaskFor(Runnable task, V result) {
        return new FutureTask<V>(task, result);
    }

    public FriendlyCompletionService(Executor executor) {
        if (executor == null)
            throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
                (AbstractExecutorService) executor : null;
        this.completionQueue = new LinkedBlockingQueue<Future<V>>();
    }

    public FriendlyCompletionService(Executor executor,
                                     BlockingQueue<Future<V>> completionQueue) {
        if (executor == null || completionQueue == null)
            throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
                (AbstractExecutorService) executor : null;
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
