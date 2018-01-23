package ServerWorker.src.worker.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeSet<E> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private final Set<E> set = new HashSet<>();

    public boolean set(E o) {
        writeLock.lock();
        try {

            System.out.println("Adding element by thread" + Thread.currentThread().getName());
            return set.add(o);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean remove(E o) {
        writeLock.lock();
        try {
            System.out.println("Removing element by thread" + Thread.currentThread().getName());
            return set.remove(o);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean contains(E o) {
        readLock.lock();
        try {
            System.out.println("Checking for element by thread" + Thread.currentThread().getName());
            return set.contains(o);
        } finally {
            readLock.unlock();
        }

    }

    public long size() {
        readLock.lock();
        try {
            System.out.println("Getting size by thread" + Thread.currentThread().getName());
            return set.size();
        } finally {
            readLock.unlock();
        }
    }
}