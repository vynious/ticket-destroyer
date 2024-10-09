import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockFactory {
    private static final ConcurrentHashMap<String, StampedLock> lockMap = new ConcurrentHashMap<>();

    // Method to get or create a lock for a given resource identifier
    public static StampedLock getLock(String resourceId) {
        return lockMap.computeIfAbsent(resourceId, key -> new StampedLock());
    }

    // Method to remove a lock from the lock map (optional, to clean up locks that are no longer needed)
    public static void removeLock(String resourceId) {
        lockMap.remove(resourceId);
    }

}
