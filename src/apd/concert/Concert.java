package apd.concert;

import apd.booking.Booker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public class Concert {
    private final int id; // Required
    private final int totalSeats; // Required

    // Seat Management
    private final AtomicInteger seatsAvailable; // Tracks available seats in a thread-safe way
    private final Map<Integer, Seat> seatMap;   // Map of seat IDs to Seat objects

    // ========  Instantiation Methods ======= //

    private Concert(ConcertBuilder builder) {
        this.id = builder.id;
        this.totalSeats = builder.totalSeats;
        this.seatsAvailable = new AtomicInteger(builder.totalSeats); // Initially all seats are available
        this.seatMap = builder.seatMap;
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

    public boolean bookSeatNotSafe(int seatId) throws InterruptedException {
        Seat seat = seatMap.get(seatId);
        boolean success = seat.bookSeatNotSafe();
        if (success) {
            seatsAvailable.decrementAndGet();
        }
        return success;
    }

    // Thread-safe method to book a seat in the concert
    public boolean bookSeatTDL(int seatId, Booker booker) throws InterruptedException {
        Seat seat = seatMap.get(seatId);
        if (seat != null && seat.bookSeatWithTDL(booker)) { // Attempt to book the seat using the Seat class's method
            seatsAvailable.decrementAndGet(); // Decrement the count of available seats if booking is successful
            return true;
        }
        return false; // Seat was already booked or does not exist
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

    public Map<Integer, Seat> getSeatMap() {
        return seatMap;
    }

    public int getSeatsAvailable() {
        return seatsAvailable.get();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Concert ID: ").append(id)
        .append("\nTotal Seats: ").append(totalSeats)
        .append("\nSeats Available: ").append(seatsAvailable)
        .append("\nSeat Distribution:\n");

        // Group seats by category and display them
        Map<String, Integer> seatCategories = new HashMap<>();
        for (Seat seat : seatMap.values()) {
            seatCategories.put(seat.getCategory(), seatCategories.getOrDefault(seat.getCategory(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : seatCategories.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" seats\n");
        }

        return sb.toString();
    }

    public static class ConcertBuilder {
        private final int id; // Required
        private int totalSeats; // Required
        private final Map<Integer, Seat> seatMap; // Optional

        public ConcertBuilder(int id) {
            this.id = id;
            this.seatMap = new ConcurrentHashMap<>(); // Initialize seatMap here
        }

        public ConcertBuilder addSeat(int seatNumber, Seat seat) {
            this.totalSeats++;
            this.seatMap.put(seatNumber, seat);
            return this;
        }

        public Concert build() {
            return new Concert(this);
        }
    }
}
