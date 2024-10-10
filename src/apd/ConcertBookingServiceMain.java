package apd;
import apd.concert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcertBookingServiceMain {

    private final Concert concert;
    private final ExecutorService executorService;

    public ConcertBookingServiceMain(Concert concert, int numberOfThreads) {
        this.concert = concert;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads); // Thread pool for concurrent apd.booking
    }

    /**
     * Attempts to book a specific seat concurrently.
     *
     * @param seatId The ID of the seat to book.
     * @return Future<Boolean> indicating success (true) or failure (false) of the apd.booking.
     */
    public Future<Boolean> bookSeat(int seatId) {
        return executorService.submit(() -> {
            Seat seat = concert.getSeatById(seatId);
            if (seat != null) {
                return seat.bookSeat(); // Attempt to book the seat using optimistic locking
            }
            return false; // Return false if seat doesn't exist
        });
    }

    /**
     * Shutdown the ExecutorService gracefully.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    public static void main(String[] args) {
        int threadCount = 10;
        int totalSeats = 100;
        int noOfUsers = 100;

        Concert concert = new Concert.ConcertBuilder(1, totalSeats).build();
        ConcertBookingServiceMain bookingService = new ConcertBookingServiceMain(concert, threadCount); // Using 10 threads

        // Simulate multiple seat apd.booking attempts
        List<Future<Boolean>> results = new ArrayList<>();
        List<Integer> seatIds = new ArrayList<>();
        Random random = new Random();

        // Create high contention for seats 1-10
        for (int i = 0; i < noOfUsers; i++) { // Try to book the same seat multiple times concurrently
            int seatId = random.nextInt(10) + 1;
            seatIds.add(seatId);
            results.add(bookingService.bookSeat(seatId));
        }

        // Check the results of apd.booking attempts
        for (int i = 0; i < results.size(); i++) {
            int seatId = seatIds.get(i); // Retrieve the seatId for the current attempt
            try {
                boolean success = results.get(i).get(); // Retrieve the apd.booking result from each future
                System.out.println("Attempt " + (i + 1) + ": Seat ID " + seatId + " - " + (success ? "Success" : "Failed"));
            } catch (Exception e) {
                System.err.println("Error during apd.booking attempt " + (i + 1) + ": " + e.getMessage());
            }
        }

        // Shutdown the ExecutorService
        bookingService.shutdown();
    }
}
