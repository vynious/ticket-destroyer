import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Create Booker
        Booker booker = new Booker(1, "John Doe");

        // Create Booking
        Booking booking = new Booking(1, new Date());
        booking.handleState();  // Should print: Booking is in Pending State.
        booking.handleState();  // Should print: Booking is Successful.

        int totalSeats = 20;
        // creating concet via builder
        Concert concert = new Concert.ConcertBuilder(1, 100);
        for (int i = 0; i<totalSeats/2; i++) {
            concert.addSeat(1, new Seat(1, 1, "VIP"));
        }
        for (int i = totalSeats/2; i<totalSeats; i++) {
            concert.addSeat(1, new Seat(1, 1, "Regular"));
        }
        concert.build();

    }
}
