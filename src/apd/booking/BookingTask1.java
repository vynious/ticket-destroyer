package apd.booking;

import apd.concert.Concert;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingTask1 implements Callable<String> {
    // Constructor to initialize all required fields
    private final Booker booker;
    private final Concert concert;
    private final int seatId;
    private final boolean threadSafe;
    private final AtomicInteger bookingCounter = new AtomicInteger(0);


    public BookingTask1(Booker booker, Concert concert, int seatId, boolean threadSafe) {
        this.booker = booker;
        this.concert = concert;
        this.seatId = seatId;
        this.threadSafe = threadSafe;
    }

    @Override
    public String call() {
        try {
            int bookerId = booker.getId();
            boolean success;
            if (threadSafe) {
                success = concert.bookSeat(seatId);
            } else {
                success = concert.bookSeatNotSafe(seatId);
            }
            String resultMessage;
            if (success) {
                bookingCounter.incrementAndGet();
                resultMessage = String.format("%s Booker ID %s: Seat ID %d - Successful. Seats Left: %d", Thread.currentThread().getName(),
                        bookerId, seatId, concert.getSeatsAvailable());
            } else {
                resultMessage = String.format("%s Booker ID %s: Seat ID %d - Failed. Seats Left: %d", Thread.currentThread().getName(),
                        bookerId, seatId, concert.getSeatsAvailable());
            }
            return resultMessage;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
