package org.mini.rx.userapp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mini.rx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class AsyncTaskTest {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncTaskTest.class);

    private static RxContext ctx;

    @BeforeClass
    public static void init() {
        ctx = new DefaultRxContext();
    }

    @AfterClass
    public static void destroy() throws IOException {
        ctx.close();
    }


    private AsyncResult<List<String>> getList() {
        AsyncTask task = ctx.newAsyncTask("main");

        List<String> strings = new LinkedList<>();

        return task.run(() -> {

            strings.add("Hello");

            task.onResultDo(getName("!!"), strings::add);

        }).run(() -> {

            List<String> copy = new LinkedList<>(strings);
            strings.clear();
            strings.addAll(copy.stream().map(String::toUpperCase).collect(Collectors.toList()));

        }).thenReturn(() -> strings);
    }

    private AsyncResult<String> getName(String suffix) {
        return ctx.newAsyncTask("sub").thenReturn(() -> "Nicola" + suffix);
    }

    @Parameters
    public static List<Object[]> data() {
        // To avoid heisenbugs
        return Arrays.asList(new Object[1000][0]);
    }

    @Test
    public void testAsyncDsl() throws InterruptedException {

        AsyncResult<List<String>> result = getList();
        Var<List<String>> var = new Var<>();
        CountDownLatch latch = new CountDownLatch(1);
        result.accept(lst -> {
            var.set(lst);
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        String concat = var.get().stream().reduce((s1, s2) -> s1 + " " + s2).get();
        assertEquals("HELLO NICOLA!!", concat);
    }


}
