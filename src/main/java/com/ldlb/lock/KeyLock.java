package com.ldlb.lock;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

public class KeyLock<T> {

    private  Map<T,Lock> lockMap = new ConcurrentHashMap<>();
    private long lockTimeout = 1000*20;
    private TimeUnit lockTimeoutUnit = TimeUnit.MILLISECONDS;

    public void tryLock(T key){
        try {
            Lock lock = lockMap.computeIfAbsent(key, t -> new ReentrantLock());
            if (!lock.tryLock(lockTimeout, lockTimeoutUnit)) {
                throw new RuntimeException("获取锁超时," + lockTimeout + " " + lockTimeoutUnit);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("尝试获取锁时被中断,"+e.toString());
        }
    }

    public void lock(T key){
        Lock lock = lockMap.computeIfAbsent(key, t -> new ReentrantLock());
        lock.lock();
    }

    public void unlock(T key){
        Lock lock = lockMap.computeIfAbsent(key, t -> new ReentrantLock());
        lock.unlock();
        clearCacheLock();
    }

    public void clearCacheLock() {
        if (lockMap.size() < 1000) return;

        lockMap.forEach((key, lock) -> {
            ReentrantLock reentrantLock = null;
            try {
                reentrantLock = (ReentrantLock)lock;
                if (reentrantLock.isLocked() || !reentrantLock.tryLock()) return;
                lockMap.remove(key,reentrantLock);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                reentrantLock.unlock();
            }
        });
    }

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

}
