import apd.concert.Concert;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeDelayLocking {

    private Concert concert;
    private ExecutorService execSvc;

    public TimeDelayLocking(Concert concert, int numOfThreads) {
        this.concert = concert;
        this.execSvc = Executors.newFixedThreadPool(numOfThreads);
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
        *
        *   5. case 3: [unsuccessful booking]
        *       - creates a booking entry
        *       - set booking state to pending
        *       - thread fails to book and checkout seats
        *       - set booking state to canceled
        *       - update booking class details
        *       - no change to concert seat map
        *       - release lock for seat
        *
        * */

        int numOfBookers = 4;
        int bookingAttempts = 10;
        int numOfSeats = 3;

        Concert concert = new Concert.ConcertBuilder(2, 3).build();
        TimeDelayLocking timeDelayLocking = new TimeDelayLocking(concert, numOfBookers);

        // create a new booking system


        Random rand = new Random();
        // randomly choose a seat to book
        for (int i = 0; i < bookingAttempts; i++) {
            // all users attempt booking

        }


    }
}
