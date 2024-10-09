package apd.concert;

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

    public boolean bookSeat() {
        long stamp = stampedLock.tryOptimisticRead(); // Start with an optimistic read
        boolean isBooked = !isAvailable;

        if (!stampedLock.validate(stamp)) { // Validate that the optimistic read was not interrupted
            stamp = stampedLock.readLock(); // Fallback to a read lock if optimistic read fails
            try {
                isBooked = !isAvailable;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }

        if (!isBooked) { // If the seat is available, upgrade to a write lock to book it
            stamp = stampedLock.writeLock();
            try {
                if (isAvailable) { // Double-check the seat status to ensure it's still available
                    isAvailable = false;
                    return true; // Successfully booked the seat
                }
            } finally {
                stampedLock.unlockWrite(stamp); // Release the write lock
            }
        }

        return false; // Seat was already booked or booking failed
    }

    public boolean isSeatAvailable() {
        long stamp = stampedLock.tryOptimisticRead(); // Optimistically read the seat's availability status
        boolean available = isAvailable;

        if (!stampedLock.validate(stamp)) { // Validate the optimistic read
            stamp = stampedLock.readLock(); // Fallback to a read lock if validation fails
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
}
