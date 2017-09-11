package org.mini.rx;

import java.util.function.Consumer;

@FunctionalInterface
public interface AsyncResult<T> extends Consumer<Consumer<T>> {
}
