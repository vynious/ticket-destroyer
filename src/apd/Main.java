package apd;

import apd.booking.*;
import apd.concert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        // Create Booker
        Booker booker = new Booker(1, "John Doe");

        int totalSeats = 20;
        // creating concet via builder
        Concert.ConcertBuilder concertBuilder = new Concert.ConcertBuilder(1, 100);
        for (int i = 0; i<totalSeats/2; i++) {
            concertBuilder.addSeat(1, new Seat(1, "VIP"));
        }
        for (int i = totalSeats/2; i<totalSeats; i++) {
            concertBuilder.addSeat(1, new Seat(1, "Regular"));
        }
        concertBuilder.build();
        Concert concert = concertBuilder.build();

        // Create Booking
        Booking booking = new Booking(1, booker.getId(), concert.getId(), 1);
        booking.handleState();  // Should print: Booking is in Pending State.
        booking.handleState();  // Should print: Booking is Successful.
    }
}
