package com.ldlb.lock;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 根据hashMap实现分段锁（ReentrantLock）,提供lock tryLock  unlock atomic
 * 直接使用atomic等重载方法不用关心锁的获取和释放
 * @param <T>
 */
public class SegmentLock<T> {
    private Integer segments = 16;//默认分段数量
    private HashMap<Integer, Lock> lockMap = new HashMap<>();
    private long lockTimeout = 1000*20;
    private TimeUnit lockTimeoutUnit = TimeUnit.MILLISECONDS;

    public SegmentLock() {
        init(segments,lockTimeout,lockTimeoutUnit, false);
    }

    public SegmentLock(Integer counts, long lockTimeout, TimeUnit lockTimeoutUnit, boolean fair) {
        init(counts,lockTimeout,lockTimeoutUnit, fair);
    }

    private void init(Integer counts,long lockTimeout, TimeUnit lockTimeoutUnit, boolean fair) {
        if (counts != null) {
            segments = counts;
        }
        this.lockTimeout = lockTimeout;
        this.lockTimeoutUnit = lockTimeoutUnit;
        for (int i = 0; i < segments; i++) {
            lockMap.put(i, new ReentrantLock(fair));
        }
    }

    public void lock(T key) {
        ReentrantLock lock = (ReentrantLock) lockMap.get(key.hashCode() % segments);
        lock.lock();
    }

    public void tryLock(T key) {
        try {
            ReentrantLock lock = (ReentrantLock) lockMap.get(key.hashCode() % segments);
            if (!lock.tryLock(lockTimeout, lockTimeoutUnit)) {
                throw new RuntimeException("获取锁超时," + lockTimeout + " " + lockTimeoutUnit);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("尝试获取锁时被中断,"+e.toString());
        }
    }

    public void unlock(T key) {
        ReentrantLock lock = (ReentrantLock) lockMap.get(key.hashCode() % segments);
        lock.unlock();
    }

    /**
     * 无入参无返回值的原子性操作(抛出RuntimeException)
     */
    public void atomic(T key,Act0 act) {
        AtomicReference<Exception> error = new AtomicReference<>(null);
        try {
            this.lock(key);
            act.accept();
        } catch (Exception e) {
            error.set(e);
        }finally {
            this.unlock(key);
        }
        if (error.get()!= null) throw new RuntimeException(error.get());
    }

    /**
     * 无入参无返回值的原子性操作（抛出Exception）
     */
    public void atomicE(T key,ActE act) throws Exception {
        AtomicReference<Exception> error = new AtomicReference<Exception>(null);
        try {
            this.lock(key);
            act.accept();
        } catch (Exception e) {
            error.set(e);
        }finally {
            this.unlock(key);
        }
        if (error.get() != null) throw error.get();
    }

    /**
     * 无入参有返回值(抛出RuntimeException)
     */
    public <OUT> OUT atomic(T key,Func0<OUT> func) {
        final AtomicReference<OUT> res = new AtomicReference<OUT>(null);
        atomic(key, () -> res.set(func.accept()));
        return res.get();
    }

    /**
     * 无入参有返回值(抛出Exception)
     */
    public <OUT> OUT atomicE(T key,FuncE<OUT> func) throws Exception {
        AtomicReference<OUT> out = new AtomicReference<>(null);
        atomicE(key, () -> out.set(func.accept()));
        return out.get();
    }
}
