package org.mini.rx;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AsyncTask {

    AsyncTask run(Runnable runnable);

    <T> void onResultDo(AsyncResult<T> res, Consumer<T> thenDo);

    <T> AsyncResult<T> thenReturn(Supplier<T> supplier);

}
