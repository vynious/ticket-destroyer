package apd.concert;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public boolean bookSeat() throws InterruptedException {
        long stamp = stampedLock.writeLock(); // Directly acquire a write lock to ensure thread safety
        try {
            if (isAvailable) { // Check if the seat is still available
                Thread.sleep(10);
                isAvailable = false; // Mark the seat as booked
                return true; // Successfully booked the seat
            }
        } finally {
            stampedLock.unlockWrite(stamp); // Release the write lock
        }
        return false; // Seat was already booked
    }

    public boolean bookSeatNotSafe() throws InterruptedException {
        if (isAvailable) { // Check if the seat is available
            Thread.sleep(10);
            isAvailable = false; // Mark the seat as booked
            return true;
        }
        return false; // Seat was already booked
    }

    public boolean cancelSeat() {
        long stamp = stampedLock.writeLock(); // Directly acquire a write lock to ensure thread safety
        try {
            if (!isAvailable) { // Check if the seat is currently booked
                isAvailable = true; // Mark the seat as available
                return true; // Successfully canceled the booking
            }
        } finally {
            stampedLock.unlockWrite(stamp); // Release the write lock
        }
        return false; // Seat was not booked, so cancellation failed
    }

    public void updateIsAvailable(boolean availability) {
        isAvailable = availability;
    }

    // Getters and setters
    public int getId() { return id; }
    public boolean getIsAvailable() { return isAvailable; }
    public String getCategory() { return category; }

    public StampedLock getLock() {
        return this.stampedLock;
    }

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
