package apd.booking;

import apd.booking.*;
import apd.concert.*;

import java.util.Date;



public class Booking {
    private int bookerId;
    private int concertId;
    private int seatId;
    private Date timestamp;

    public Booking(int bookerId, int concertId, int seatId) {
        this.bookerId = bookerId;
        this.concertId = concertId;
        this.seatId = seatId;
        this.timestamp = new Date();
    }


    // CRUD methods
    public void saveBooking() { /* Save apd.booking logic */ }
    public void deleteBooking() { /* Delete apd.booking logic */ }
    public void updateBooking() { /* Update apd.booking logic */ }

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