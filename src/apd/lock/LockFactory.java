package apd.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class LockFactory {
    private static final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    /**
     * Method to get or create a lock for a given resource identifier.
     * The type of lock can be specified by passing a supplier of the lock.
     *
     * @param resourceId The ID of the resource for which the lock is requested.
     * @param lockSupplier A supplier that provides a lock when needed (can be ReentrantLock, StampedLock, etc.)
     * @return The lock associated with the given resource.
     */
    public static Lock getLock(String resourceId, Supplier<? extends Lock> lockSupplier) {
        return lockMap.computeIfAbsent(resourceId, key -> lockSupplier.get());
    }

    /**
     * Method to remove a lock from the lock map.
     * This can be used to clean up locks that are no longer needed.
     *
     * @param resourceId The ID of the resource for which the lock should be removed.
     */
    public static void removeLock(String resourceId) {
        lockMap.remove(resourceId);
    }
}