package apd.booking;

import apd.booking.*;
import apd.concert.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;


public class Booking {
    private int id;
    private int bookerId;
    private int concertId;
    private int seatId;
    private Date timestamp;
    private BookingState state;

    public Booking(int id, int bookerId, int concertId, int seatId) {
        this.id = id;
        this.bookerId = bookerId;
        this.concertId = concertId;
        this.seatId = seatId;

        this.state = new PendingState();  // Start with PendingState
        this.timestamp = new Date();
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

    public int getConcertId() {
        return concertId;
    }

    public int getSeatId() {
        return seatId;
    }

    public int getBookerId() {
        return bookerId;
    }
}