package com.ldlb.utils;

import com.ldlb.lock.Act0;
import com.ldlb.lock.Func0;
import com.ldlb.lock.Func1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

public class LockExecutor {

    private Lock lock;

    public LockExecutor(Lock lock) {
        this.lock = lock;
    }

    public Lock getLock() {
        return this.lock;
    }

    public <T> T atomic(Func0<T> act) {
        try {
            this.lock.lock();
            return act.accept();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            this.lock.unlock();
        }
    }

    public String test() {
        return "test执行，"+Thread.currentThread().getName();
    }
}
