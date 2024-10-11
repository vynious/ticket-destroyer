package apd.concert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import apd.booking.Booker;
import apd.booking.Booking;
import apd.lock.*;

public class Concert {
    private final int id; // Required
    private final int totalSeats; // Required

    // Seat Management
    private final AtomicInteger seatsAvailable; // Tracks available seats in a thread-safe way
    private final Map<Integer, Seat> seatMap;   // Map of seat IDs to Seat objects
    private final StampedLock lock = new StampedLock(); // Lock for thread-safe operations

    // ========  Instantiation Methods ======= //

    private Concert(ConcertBuilder builder) {
        this.id = builder.id;
        this.totalSeats = builder.totalSeats;
        this.seatsAvailable = new AtomicInteger(builder.totalSeats); // Initially all seats are available
        this.seatMap = builder.seatMap;
        this.seedSeats();
    }

    // Thread-safe method to book a seat in the concert
    public boolean bookSeat(int seatId) throws InterruptedException {
        Seat seat = seatMap.get(seatId);
        if (seat != null && seat.bookSeat()) { // Attempt to book the seat using the Seat class's method
            seatsAvailable.decrementAndGet(); // Decrement the count of available seats if booking is successful
            return true;
        }
        return false; // Seat was already booked or does not exist
    }

    // Thread-safe method to cancel a seat in the concert
    public boolean cancelSeat(int seatId) {
        Seat seat = seatMap.get(seatId);
        if (seat != null && seat.cancelSeat()) { // Attempt to cancel the booking using the Seat class's method
            seatsAvailable.incrementAndGet(); // Increment the count of available seats if cancellation is successful
            return true;
        }
        return false; // Seat was not booked or does not exist
    }

    public boolean bookSeatNotSafe(int seatId) throws InterruptedException {
        Seat seat = seatMap.get(seatId);
        boolean success = seat.bookSeatNotSafe();
        if (success) {
            seatsAvailable.decrementAndGet();
        }
        return success;
    }

    public void minusSeat() {
        if (seatsAvailable.get() > 0) {
            seatsAvailable.getAndDecrement();
        }
    }

    public void addSeat() {
        if (seatsAvailable.get() < totalSeats) {
            this.seatsAvailable.getAndIncrement();
        }
    }

    private void seedSeats() {
        for (int i = 1; i <= this.totalSeats; i++) {
            int catNum = (i + 19) / 20; // Categorize seats based on number
            String category = "CAT" + catNum;
            seatMap.put(i, new Seat(i, category));
        }
    }

    // Getters
    public int getTotalSeats() {
        return totalSeats;
    }

    public int getId() {
        return id;
    }

    public Seat getSeatById(int seatId) {
        return seatMap.get(seatId);
    }

    public StampedLock getLock() {
        return this.lock;
    }

    public Map<Integer, Seat> getSeatMap() {
        return seatMap;
    }

    public int getSeatsAvailable() {
        return seatsAvailable.get();
    }

    public static class ConcertBuilder {
        private final int id; // Required
        private final int totalSeats; // Required
        private final Map<Integer, Seat> seatMap; // Optional

        public ConcertBuilder(int id, int totalSeats) {
            this.id = id;
            this.totalSeats = totalSeats;
            this.seatMap = new ConcurrentHashMap<>(); // Initialize seatMap here
        }

        public ConcertBuilder addSeat(int seatNumber, Seat seat) {
            this.seatMap.put(seatNumber, seat);
            return this;
        }

        public Concert build() {
            return new Concert(this);
        }
    }

    // ======= Booking Functionalities ========= //
}
