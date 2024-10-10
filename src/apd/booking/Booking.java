package apd.booking;

import apd.booking.*;
import apd.concert.*;

import java.util.Date;
import java.util.List;


public class Booking {
    private int id;
    private Date timestamp;
    private BookingState state;
    private List<Seat> seats;

    public Booking(int id, Date timestamp, List<Seat> seats) {
        this.id = id;
        this.timestamp = timestamp;
        this.state = new PendingState();  // Start with PendingState
        this.seats = seats;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public void handleState() {
        state.handleRequest(this);
    }

    // CRUD methods
    public void saveBooking() { /* Save apd.booking logic */ }
    public void deleteBooking() { /* Delete apd.booking logic */ }
    public void updateBooking() { /* Update apd.booking logic */ }

    // Getters
    public int getId() { return id; }
    public Date getTimestamp() { return timestamp; }
}