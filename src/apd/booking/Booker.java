package apd.booking;

import java.util.ArrayList;
import java.util.List;

import apd.concert.*;
import java.util.concurrent.Semaphore;

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
        bookings.add(booking);
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void removeBooking(int concertId, int seatId) {
        int toRemove = -1;
        for (Booking booking : bookings) {
            if (booking.getConcertId() == concertId && booking.getSeatId() == seatId) {
                toRemove = bookings.indexOf(booking);
                break;
            }
        }
        if (toRemove != -1) {
            bookings.remove(toRemove);
        }
    }
}