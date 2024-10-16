package apd.concert;

import apd.booking.Booker;
import apd.lock.LockFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Seat {
    private final int id;
    private boolean isAvailable;
    private final String category;
    private final String name;

    public Seat(int id, String category) {
        this.id = id;
        this.isAvailable = true;
        this.category = category;
        this.name = "seat-" + id;
    }

    public boolean bookSeat() throws InterruptedException {
        StampedLock stampedLock = this.getLock(name);
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
        StampedLock stampedLock = this.getLock(name);
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

    // Getters and setters
    public int getId() {return id;}
    public String getName() {return name;}
    public boolean getIsAvailable() { return isAvailable;}
    public String getCategory() { return category; }
    public StampedLock getLock(String name) {
        return LockFactory.getLock(name);
    }


    public boolean bookSeatWithTDL(Booker booker) throws InterruptedException {
        long stamp = 0L;
        StampedLock stampedLock = this.getLock(name);
        try {
            // Try to acquire the write lock
            stamp = stampedLock.tryWriteLock(12, TimeUnit.MILLISECONDS);

            if (stamp == 0L) {
                System.out.println(booker.getName() + " failed to get lock for seat-" + this.id);
                return false; // Could not acquire lock
            }

            // Since we have the write lock, we can directly check isAvailable
            if (!this.isAvailable) {
                System.out.println(booker.getName() + " got the lock for seat-" + this.id + " but it's too late ~");
                return false;
            }

            long startTime = System.currentTimeMillis();

            System.out.println(booker.getName() + " got the lock for seat-" + this.id);

            // Simulate a chance of delay (e.g., due to network delay or user indecision)
            this.simulateBookingSession(booker);

            // Check if the total time taken exceeds the allowed 10 ms
            if (System.currentTimeMillis() - startTime >= 10) {
                return false; // Booking took too long, indicating timeout
            }

            // Critical section: Mark the seat as booked
            this.isAvailable = false; // Set seat to not available

            System.out.println(booker.getName() + " successfully booked the seat.");
            return true;

        } catch (InterruptedException e) {
            // Restore the interrupted status to handle it properly
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " was interrupted during booking.");
            return false;

        } finally {
            // Release the lock only if it was successfully acquired
            if (stamp != 0L) {
                stampedLock.unlockWrite(stamp);
            }
        }
    }

    public void simulateBookingSession(Booker booker) throws InterruptedException {
        Random rand = new Random();
        boolean simulateTimeout = rand.nextBoolean();

        // Randomly decide if booking session should simulate a delay (50% chance)
        if (simulateTimeout) {
            System.out.println(booker.getName() + " is experiencing a delay...");
            Thread.sleep(10); // Introduce delay of 10 ms
        }
    }
}
