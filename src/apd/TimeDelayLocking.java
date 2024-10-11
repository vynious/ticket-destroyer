package apd;

import apd.concert.Concert;
import apd.concert.Seat;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TimeDelayLocking {

    private final Concert concert;
    private final ExecutorService execSvc;

    public TimeDelayLocking(Concert concert, int numOfThreads) {
        this.concert = concert;
        this.execSvc = Executors.newFixedThreadPool(numOfThreads);
    }

    public Future<String> bookSeat(int seatId) {
        return execSvc.submit(() -> {
            String threadName = Thread.currentThread().getName();
            boolean result = false;
            Seat seat = concert.getSeatById(seatId);
            System.out.printf("%s is attempting to book seat-%s\n",threadName ,seatId);
            if (seat != null) {
                result = seat.bookSeatWithTDL();
            }
            return String.format("Thread: %s, Booking Result: %s", threadName, result ? "Success" : "Failed");
        });
    }

    public void shutdown() {
        execSvc.shutdown();
    }

    public static void main(String[] args) {
        /*
        *   time based locking
        *   1. multiple threads will access each seat
        *   2. after each thread manages to get hold of a seat (lock)
        *   3. initiate countdown to release the seat lock
        *   4. case 1: [successful booking]
        *       - creates a booking entry
        *       - set booking state to pending
        *       - thread manages to book and check out seats
        *       - set booking state to success
        *       - update booking class details
        *       - update the concert seat map
        *       - release lock for seat
        *   5. case 2: [unsuccessful booking]
        *       - creates a booking entry
        *       - set booking state to pending
        *       - thread fails to book and checkout seats
        *       - set booking state to canceled
        *       - update booking class details
        *       - no change to concert seat map
        *       - release lock for seat
        * */

        int numOfBookers = 10;
        int numOfSeats = 5;

        Concert concert = new Concert.ConcertBuilder(2, numOfSeats).build();
        TimeDelayLocking timeDelayLocking = new TimeDelayLocking(concert, numOfBookers);

        List<Future<String>> results = new ArrayList<>();

        Random rand = new Random();

        // randomly choose a seat to book
        for (int i = 0; i < numOfBookers; i++) {
            int seatId = rand.nextInt(numOfSeats) + 1;
            results.add(timeDelayLocking.bookSeat(seatId));
        }

        // Print out all the booking results
        for (Future<String> res : results) {
            try {
                System.out.println(res.get()); // Wait for the result and print it
            } catch (
                    InterruptedException |
                    ExecutionException e) {
                Thread.currentThread().interrupt();
                System.out.println("The booking task was interrupted.");
            }
        }
        timeDelayLocking.shutdown();
    }
}
