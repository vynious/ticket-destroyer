package apd.booking;

import java.text.SimpleDateFormat;
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

    // Getters
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