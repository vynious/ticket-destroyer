package apd.booking;


import apd.concert.Concert;
import java.util.concurrent.Semaphore;

public class BookingTask2 implements Runnable {
    private final Booker booker;
    private final Concert concert;
    private final int seatId;
    private final Semaphore semaphore;
    private final boolean useSemaphore;

    // Constructor to initialize all required fields
    public BookingTask2(Booker booker, Concert concert, int seatId, Semaphore semaphore, boolean useSemaphore) {
        this.booker = booker;
        this.concert = concert;
        this.seatId = seatId;
        this.semaphore = semaphore;
        this.useSemaphore = useSemaphore;
    }

    // Override the run() method from the Runnable interface
    @Override
    public void run() {
        try {
            // Attempt to book the seat for the concert
            int bookedSeatId = booker.bookSeatForConcert(concert, seatId, semaphore, useSemaphore);

            if (bookedSeatId != -1) {
                System.out.println(Thread.currentThread().getName() + " successfully booked seat " + bookedSeatId + " for concert " + concert.getId());
            } else {
                System.out.println(Thread.currentThread().getName() + " failed to book seat " + seatId);
            }
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getName() + " was interrupted during booking.");
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }
}