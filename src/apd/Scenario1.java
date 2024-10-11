package apd;
import apd.booking.Booker;
import apd.booking.BookingTask1;
import apd.concert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Scenario1 {

    private final Concert concert;
    private final ExecutorService executorService;;

    public Scenario1(Concert concert, int numberOfThreads, boolean threadSafe) {
        this.concert = concert;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads); // Thread pool for concurrent apd.booking
    }

    /**
     * Shutdown the ExecutorService gracefully.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    public void runBookingSimulation(int noOfBookers, int bookingAttempts, int contestSeats, Scenario1 bookingServiceMain) {
        // Simulate multiple seat apd.booking attempts
        List<String> results = new ArrayList<>();
        Random random = new Random();

        // Create bookers for testing
        List<Booker> bookers = new ArrayList<>();
        for (int i = 1; i <= noOfBookers; i++) {
            bookers.add(new Booker(i, "Booker" + i));
        }

        // Create high contention for seats 1-10
        for (int i = 0; i < bookingAttempts; i++) { // Try to book the same seat multiple times concurrently
            int seatId = random.nextInt(contestSeats) + 1;
            Booker booker = bookers.get(random.nextInt(bookers.size()));

            BookingTask1 bt1 = new BookingTask1(booker, concert, seatId, true);

            results.add(bt1.call());
        }

        // Check the results of apd.booking attempts
        for (int i = 0; i < results.size(); i++) {
            try {
                String res = results.get(i); // Retrieve the apd.booking result from each future
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

        int contestSeats = 5;


        System.out.println("------- NOT SAFE -------");
        Concert concert2 = new Concert.ConcertBuilder(1, totalSeats).build();
        Scenario1 threadNotSafeBookingSvc = new Scenario1(concert2, threadCount, false); // Using 10 threads
        threadNotSafeBookingSvc.runBookingSimulation(noOfBookers, bookingAttempts, contestSeats, threadNotSafeBookingSvc);

        System.out.println();

        System.out.println("------- SAFE -------");
        Concert concert = new Concert.ConcertBuilder(1, totalSeats).build();
        Scenario1 threadSafeBookingSvc = new Scenario1(concert, threadCount, true); // Using 10 threads
        threadSafeBookingSvc.runBookingSimulation(noOfBookers, bookingAttempts, contestSeats, threadSafeBookingSvc);

    }
}