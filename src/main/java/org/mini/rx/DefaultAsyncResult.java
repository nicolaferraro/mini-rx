package org.mini.rx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DefaultAsyncResult<T> implements AsyncResult<T> {

    private Scheduler scheduler;

    private AtomicReference<Consumer<T>> consumer;

    private T result;
    private AtomicBoolean resultSet;

    private boolean completed;

    public DefaultAsyncResult(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.consumer = new AtomicReference<>();
        this.resultSet = new AtomicBoolean();
    }

    @Override
    public void accept(Consumer<T> consumer) {
        boolean set = this.consumer.compareAndSet(null, consumer);
        if (!set) {
            throw new IllegalStateException("Consumer already set");
        }
        checkCompletion();
    }

    public void setResult(T result) {
        boolean set = this.resultSet.compareAndSet(false, true);
        if (!set) {
            throw new IllegalStateException("Result already set");
        }
        this.result = result;
        checkCompletion();
    }

    private void checkCompletion() {
        scheduler.schedule(() -> {
            if (!completed && consumer.get() != null && resultSet.get()) {
                consumer.get().accept(result);
                completed = true;
            }
        });
    }

    @Override
    public Consumer<Consumer<T>> andThen(Consumer<? super Consumer<T>> after) {
        throw new UnsupportedOperationException();
    }
}
