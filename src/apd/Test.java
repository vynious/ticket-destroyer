package apd;
import apd.concert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {

    private final Concert concert;
    private final ExecutorService executorService;

    public Test(Concert concert, int numberOfThreads) {
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

//        Concert concert = new Concert(1, 100); // Create a apd.concert with 100 seats
//        ConcertBookingService bookingService = new ConcertBookingService(concert, threadCount); // Using 10 threads
//
//        // Simulate multiple seat apd.booking attempts
//        List<Future<Boolean>> results = new ArrayList<>();
//
//        for (int i = 0; i < 50; i++) { // Try to book the same seat multiple times concurrently
//            int seatId = 1; // All threads try to book seat with ID 1
//            results.add(bookingService.bookSeat(seatId));
//        }
//
//        // Check the results of apd.booking attempts
//        for (int i = 0; i < results.size(); i++) {
//            try {
//                boolean success = results.get(i).get(); // Retrieve the apd.booking result from each future
//                System.out.println("Attempt " + (i + 1) + ": " + (success ? "Success" : "Failed"));
//            } catch (Exception e) {
//                System.err.println("Error during apd.booking attempt " + (i + 1) + ": " + e.getMessage());
//            }
//        }
//
//        // Shutdown the ExecutorService
//        bookingService.shutdown();
    }
}
