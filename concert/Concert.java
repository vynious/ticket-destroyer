import java.util.HashMap;
import java.util.Map;
import lock.*;

public class Concert {
    private final int id; // Required
    private final int totalSeats; // Required
    private int seatsAvailable; // Derived from totalSeats
    private final Map<Integer, Seat> seatMap; // Optional

    // ========  Instantiation Methods ======= //

    private Concert(ConcertBuilder builder) {
        this.id = builder.id;
        this.totalSeats = builder.totalSeats;
        this.seatsAvailable = builder.totalSeats; // Initially all seats are available
        this.seatMap = builder.seatMap;
        this.seedSeats();
    }

    // Thread-safe method to book a seat in the concert
    public boolean bookSeat(int seatId) {
        Seat seat = seatMap.get(seatId);
        if (seat != null) {
            return seat.bookSeat(); // Delegate to the Seat class's bookSeat method
        } else {
            return false; // Seat does not exist
        }
    }

    public void minusSeat() {
        if (seatsAvailable > 0) {
            seatsAvailable--;
            this.totalSeats--;
        }
    }

    public void addSeat() {
        this.totalSeats++;
        this.seatsAvailable++;
    }

    private void seedSeats() {
        for (int i = 1; i <= this.totalSeats; i++) {
            int catNum = (i + 19) / 20; // Categorize seats based on number
            String category = "CAT" + catNum;
            seatMap.put(i, new Seat(i, i, category));
        }
    }

    // Getters
    public int getTotalSeats() {
        return totalSeats;
    }

    public int getId() {
        return id;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public static class ConcertBuilder {
        private final int id; // Required
        private final int totalSeats; // Required
        private final Map<Integer, Seat> seatMap; // Optional

        public ConcertBuilder(int id, int totalSeats) {
            this.id = id;
            this.totalSeats = totalSeats;
            this.seatMap = new HashMap<>(); // Initialize seatMap here
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
