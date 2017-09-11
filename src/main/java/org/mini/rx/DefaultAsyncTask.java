package org.mini.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultAsyncTask implements AsyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAsyncTask.class);

    private RxContext context;

    private String name;

    private LinkedList<Runnable> todoList;

    private AtomicInteger awaiting;

    private Scheduler serialScheduler;

    public DefaultAsyncTask(RxContext context, String name) {
        this.context = context;
        this.name = name;
        this.todoList = new LinkedList<>();
        this.awaiting = new AtomicInteger(0);
        this.serialScheduler = context.getSchedulerManager().serialized(this);
    }

    @Override
    public AsyncTask run(Runnable runnable) {
        serialScheduler.schedule(() -> {
            LOG.debug("Adding runnable to task {}", this);
            todoList.add(runnable);
            checkLoop();
        });
        return this;
    }

    @Override
    public <T> void onResultDo(AsyncResult<T> res, Consumer<T> thenDo) {
        awaiting.incrementAndGet();
        LOG.debug("Incremented awaiting to {} in task {}", awaiting, this);
        res.accept(x -> {
            serialScheduler.schedule(() -> {
                thenDo.accept(x);
                awaiting.decrementAndGet();
                LOG.debug("Decremented awaiting to {} in task {}", awaiting, this);
                checkLoop();
            });
        });
    }

    @Override
    public <T> AsyncResult<T> thenReturn(Supplier<T> supplier) {
        DefaultAsyncResult<T> result = new DefaultAsyncResult<>(serialScheduler);
        serialScheduler.schedule(() -> {
            LOG.debug("Adding runnable to task {}", this);
            todoList.add(() -> {
                result.setResult(supplier.get());
            });
            checkLoop();
        });
        return result;
    }

    private void checkLoop() {
        if (awaiting.get() == 0 && !todoList.isEmpty()) {
            LOG.debug("Executing runnable from task {} on thread {}", this, Thread.currentThread().getName());
            LOG.debug("Runnable queue size is {} in {}", this.todoList.size(), this);
            Runnable run = todoList.removeFirst();
            run.run();
            LOG.debug("Executed! Runnable queue size is now {} in {}", this.todoList.size(), this);
            serialScheduler.schedule(this::checkLoop);
        } else if (todoList.isEmpty()) {
            LOG.debug("todoList empty in task {}", this);
        } else {
            LOG.debug("cannot execute runnable in task {} now", this);
        }
    }

    @Override
    public String toString() {
        if (name != null) {
            return "DefaultAsyncTask{" +
                    "name='" + name + '\'' +
                    '}';
        }
        return super.toString();
    }
}
