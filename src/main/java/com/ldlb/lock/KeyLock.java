package com.ldlb.lock;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        lock.lock();
    }

}
