package apd.booking;
import apd.concert.Concert;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import apd.concert.*;

public class BookingTask2 implements Callable<List<Booking>> {
    private final Booker booker;
    private final Concert concert;
    private final int seatId;
    private final Semaphore semaphore;

    // Constructor to initialize all required fields
    public BookingTask2(Booker booker, Concert concert, int seatId, Semaphore semaphore) {
        this.booker = booker;
        this.concert = concert;
        this.seatId = seatId;
        this.semaphore = semaphore;
    }

    // Override the run() method from the Runnable interface
    @Override
    public List<Booking> call() throws InterruptedException {
        boolean useSemaphore = semaphore != null;
        if (useSemaphore) {
            // Simulate processing delay and acquire semaphore permit
            Thread.sleep(10);
            semaphore.acquire();
        }

        try {
            // Attempt to book the seat
            Seat seat = concert.getSeatById(seatId);
            Thread.sleep(10); // Simulate processing delay
            boolean success;

            if (seat != null) {
                if (useSemaphore) {
                    success = concert.bookSeat(seatId);  // Safe booking with semaphore
                } else {
                    success = concert.bookSeatNotSafe(seatId); // Unsafe booking without semaphore
                }

                if (success) {
                    // Seat booking successful, add it to Booker's list of bookings
                    Booking newBooking = new Booking(booker.getId(), concert.getId(), seatId);
                    booker.addBooking(newBooking);
//                    System.out.println(booker.getName() + " successfully booked seat " + seatId);
                }
            }
        } finally {
            // Release semaphore after booking attempt (if semaphore is used)
            if (useSemaphore) {
                Thread.sleep(10); // Simulate processing delay
                semaphore.release();
            }
        }

        return booker.getBookings();
    }
}