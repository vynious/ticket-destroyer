package apd.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

public class LockFactory {
    private static final ConcurrentHashMap<String, StampedLock> lockMap = new ConcurrentHashMap<>();

    // Method to get or create a apd.lock for a given resource identifier
    public static StampedLock getLock(String resourceId) {
        return lockMap.computeIfAbsent(resourceId, key -> new StampedLock());
    }

    // Method to remove a apd.lock from the apd.lock map (optional, to clean up locks that are no longer needed)
    public static void removeLock(String resourceId) {
        lockMap.remove(resourceId);
    }

}
