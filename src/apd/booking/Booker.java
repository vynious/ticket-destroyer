package apd.booking;

import apd.lock.LockFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.locks.StampedLock;

public class Booker {
    private int id;
    private String name;
    private List<Booking> bookings;

    public Booker(int id, String name) {
        this.id = id;
        this.name = name;
        this.bookings = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }

    public void addBooking(Booking booking) {
        StampedLock lock = LockFactory.getLock(name);
        long stamp = lock.writeLock();
        try {
            bookings.add(booking);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public List<Booking> getBookings() {
        StampedLock lock = LockFactory.getLock(name);
        long stamp = lock.readLock();
        try {
            return new ArrayList<>(bookings);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void removeBooking(int concertId, int seatId) {
        StampedLock lock = LockFactory.getLock(name);
        long stamp = lock.writeLock();
        try {
            bookings.removeIf(booking -> booking.getConcertId() == concertId && booking.getSeatId() == seatId);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Booker ID: %d, Name: %s%n", id, name));
        sb.append("Bookings:\n");
        sb.append(String.format("%-10s %-10s %-10s %-20s%n", "BookerID", "ConcertID", "SeatID", "Timestamp"));
        sb.append("-----------------------------------------------------------------\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Synchronize access to the bookings list
        StampedLock lock = LockFactory.getLock(name);
        long stamp = lock.readLock();
        try {
            for (Booking booking : bookings) {
                sb.append(String.format("%-10d %-10d %-10d %-20s%n",
                        booking.getBookerId(),
                        booking.getConcertId(),
                        booking.getSeatId(),
                        dateFormat.format(booking.getTimestamp())));
            }
        } finally {
            lock.unlockRead(stamp);
        }
        return sb.toString();
    }

}
