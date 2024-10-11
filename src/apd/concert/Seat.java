package apd.concert;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class Seat {
    private int id;
    private boolean isAvailable;
    private String category;
    private final StampedLock stampedLock = new StampedLock();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Seat(int id, String category) {
        this.id = id;
        this.isAvailable = true;
        this.category = category;
    }

    public boolean bookSeat() {
        long stamp = stampedLock.tryOptimisticRead(); // Start with an optimistic read
        boolean isBooked = !isAvailable;

        if (!stampedLock.validate(stamp)) { // Validate that the optimistic read was not interrupted
            stamp = stampedLock.readLock(); // Fallback to a read apd.lock if optimistic read fails
            try {
                isBooked = !isAvailable;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }

        if (!isBooked) { // If the seat is available, upgrade to a write apd.lock to book it
            stamp = stampedLock.writeLock();
            try {
                if (isAvailable) { // Double-check the seat status to ensure it's still available
                    isAvailable = false;
                    return true; // Successfully booked the seat
                }
            } finally {
                stampedLock.unlockWrite(stamp); // Release the write apd.lock
            }
        }

        return false; // Seat was already booked or apd.booking failed
    }

    public boolean isSeatAvailable() {
        long stamp = stampedLock.tryOptimisticRead(); // Optimistically read the seat's availability status
        boolean available = isAvailable;

        if (!stampedLock.validate(stamp)) { // Validate the optimistic read
            stamp = stampedLock.readLock(); // Fallback to a read apd.lock if validation fails
            try {
                available = isAvailable;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }

        return available;
    }

    public void updateIsAvailable(boolean availability) {
        this.isAvailable = availability;
    }

    // Getters and setters
    public int getId() { return id; }
    public boolean getIsAvailable() { return isAvailable; }
    public String getCategory() { return category; }

    public boolean isSeatAvailableTDL() {
        readWriteLock.readLock().lock();
        try {
            return isAvailable;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean bookSeatWithTDL() {
        boolean contestable = false;

        try {
            // Try to acquire the write lock

            contestable = readWriteLock.writeLock().tryLock(12, TimeUnit.MILLISECONDS);

            if (!contestable) {
                System.out.println(Thread.currentThread().getName() + " failed to get lock for seat-" + this.id);
                return false; // Could not acquire lock or seat is not available
            }

            if (!this.isSeatAvailableTDL()) {
                System.out.println(Thread.currentThread().getName() + " got the lock for seat-" + this.id + " but its too late ~");
                return false;
            }

            long startTime = System.currentTimeMillis();

            System.out.println(Thread.currentThread().getName() + " got the lock for seat-" + this.id);

            // Simulate a chance of delay (e.g., due to network delay or user indecision)
            this.simulateBookingSession();

            // Check if the total time taken exceeds the allowed 1000 ms (1 second)
            if (System.currentTimeMillis() - startTime >= 10) {
                return false; // Booking took too long, indicating timeout
            }

            // Critical section: Mark the seat as booked
            this.isAvailable = false; // Set seat to not available

            System.out.println(Thread.currentThread().getName() + " successfully booked the seat.");
            return true;

        } catch (InterruptedException e) {
            // Restore the interrupted status to handle it properly
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " was interrupted during booking.");
            return false;

        } finally {
            // Release the lock only if it was successfully acquired
            if (contestable) {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    public void simulateBookingSession() throws InterruptedException {
        Random rand = new Random();
        boolean simulateTimeout = rand.nextBoolean();

        // Randomly decide if booking session should simulate a delay (50% chance)
        if (simulateTimeout) {
            System.out.println(Thread.currentThread().getName() + " is experiencing a delay...");
            Thread.sleep(10); // Introduce delay of 10 ms
        }
    }
}
