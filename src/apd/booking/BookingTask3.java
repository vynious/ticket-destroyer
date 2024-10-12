package apd.booking;

import apd.concert.Concert;
import apd.concert.Seat;

import java.util.concurrent.Callable;

public class BookingTask3 implements Callable<String> {

    private final Booker booker;
    private final Concert concert;
    private final int seatId;

    public BookingTask3(Booker booker, Concert concert, int seatId) {
        this.booker = booker;
        this.concert = concert;
        this.seatId = seatId;
    }

    @Override
    public String call() throws InterruptedException {
        {
            String bookerName = booker.getName();
            boolean result = false;
            Seat seat = concert.getSeatById(seatId);
            System.out.printf("%s is attempting to book seat-%s\n",bookerName ,seatId);
            if (seat != null) {
                result = concert.bookSeatTDL(seatId, booker);
            }
            if (result) {
                booker.addBooking(new Booking(booker.getId(), concert.getId(), seatId));
            }
            return String.format("Booker: %s, Booking Result: %s", bookerName, result ? "Success" : "Failed");
        }
    }
}
