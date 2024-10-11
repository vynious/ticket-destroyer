package apd;
import apd.booking.Booker;
import apd.booking.Booking;
import apd.concert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcertBookingServiceMain {

    private final Concert concert;
    private final ExecutorService executorService;
    private boolean threadSafe;

    private AtomicInteger bookingCounter = new AtomicInteger(0);

    public ConcertBookingServiceMain(Concert concert, int numberOfThreads, boolean threadSafe) {
        this.concert = concert;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads); // Thread pool for concurrent apd.booking
        this.threadSafe = threadSafe;
    }

    /**
     * Attempts to book a specific seat concurrently.
     *
     * @param seatId The ID of the seat to book.
     * @return Future<Boolean> indicating success (true) or failure (false) of the apd.booking.
     */
    public Future<String> bookSeat(int bookerId, int concertId, int seatId) {
        return executorService.submit(() -> {
            try {
                boolean success;
                if (threadSafe) {
                    success = concert.bookSeat(seatId);
                } else {
                    success = concert.bookSeatNotSafe(seatId);
                }
                String resultMessage;
                if (success) {
                    bookingCounter.incrementAndGet();
                    Booking booking = new Booking(bookingCounter.get(), bookerId, concertId, seatId);
                    resultMessage = String.format("%s Booker ID %d: Seat ID %d - Successful. Seats Left: %d", Thread.currentThread().getName(),
                            bookerId, seatId, concert.getSeatsAvailable());
                } else {
                    resultMessage = String.format("%s Booker ID %d: Seat ID %d - Failed. Seats Left: %d", Thread.currentThread().getName(),
                            bookerId, seatId, concert.getSeatsAvailable());
                }
                return resultMessage;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Shutdown the ExecutorService gracefully.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    public void runBookingSimulation(int totalSeats, int noOfBookers, int bookingAttempts, int contestSeats, ConcertBookingServiceMain bookingServiceMain) {
        // Simulate multiple seat apd.booking attempts
        List<Future<String>> results = new ArrayList<>();
        Random random = new Random();

        // Create bookers for testing
        List<Booker> bookers = new ArrayList<>();
        for (int i = 1; i <= noOfBookers; i++) {
            bookers.add(new Booker(i, "Booker_" + i));
        }

        // Create high contention for seats 1-10
        for (int i = 0; i < bookingAttempts; i++) { // Try to book the same seat multiple times concurrently
            int seatId = random.nextInt(contestSeats) + 1;
            Booker booker = bookers.get(random.nextInt(bookers.size()));

            results.add(bookingServiceMain.bookSeat(booker.getId(), concert.getId(), seatId));
        }

        // Check the results of apd.booking attempts
        for (int i = 0; i < results.size(); i++) {
            try {
                String res = results.get(i).get(); // Retrieve the apd.booking result from each future
                System.out.println(res);
            } catch (Exception e) {
                System.err.println("Error during apd.booking attempt " + (i + 1) + ": " + e.getMessage());
            }
        }

        bookingServiceMain.shutdown();
    }

    public static void main(String[] args) {
        int threadCount = 1000;
        int totalSeats = 100;
        int bookingAttempts = 100;
        int noOfBookers = 100;

        int contestSeats = 1;

        Concert concert = new Concert.ConcertBuilder(1, totalSeats).build();

        System.out.println("------- SAFE -------");
        ConcertBookingServiceMain threadSafeBookingSvc = new ConcertBookingServiceMain(concert, threadCount, true); // Using 10 threads
        threadSafeBookingSvc.runBookingSimulation(totalSeats, noOfBookers, bookingAttempts, contestSeats, threadSafeBookingSvc);

        System.out.println();

        System.out.println("------- NOT SAFE -------");
        Concert concert2 = new Concert.ConcertBuilder(1, totalSeats).build();
        ConcertBookingServiceMain threadNotSafeBookingSvc = new ConcertBookingServiceMain(concert2, threadCount, false); // Using 10 threads
        threadNotSafeBookingSvc.runBookingSimulation(totalSeats, noOfBookers, bookingAttempts, contestSeats, threadNotSafeBookingSvc);
    }
}