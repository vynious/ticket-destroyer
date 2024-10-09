import concert.*;

public class Seat {
    private int id;
    private boolean isAvailable;
    private String category;
    private Concert concert;
    private final StampedLock stampedLock = new StampedLock();

    public Seat(int id, String category, Concert concert) {
        this.id = id;
        this.isAvailable = true;
        this.category = category;
        this.concert = concert;
    }

    public boolean bookSeat() {
        long stamp = stampedLock.tryOptimisticRead(); // Start with an optimistic read
        boolean isBooked = !seatAvailable;

        if (!stampedLock.validate(stamp)) { // Validate that the optimistic read was not interrupted
            stamp = stampedLock.readLock(); // Fallback to a read lock if optimistic read fails
            try {
                isBooked = !seatAvailable;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }

        if (!isBooked) { // If the seat is available, upgrade to a write lock to book it
            stamp = stampedLock.writeLock();
            try {
                if (seatAvailable) { // Double-check the seat status to ensure it's still available
                    seatAvailable = false;
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
        boolean available = seatAvailable;

        if (!stampedLock.validate(stamp)) { // Validate the optimistic read
            stamp = stampedLock.readLock(); // Fallback to a read lock if validation fails
            try {
                available = seatAvailable;
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
    public Concert getConcert() { return concert; }
}
