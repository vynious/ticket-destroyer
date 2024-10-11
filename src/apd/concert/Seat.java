package apd.concert;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.StampedLock;

public class Seat {
    private int id;
    private boolean isAvailable;
    private String category;
    private final StampedLock stampedLock = new StampedLock();

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
}
