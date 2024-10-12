package apd;
import apd.booking.Booker;
import apd.booking.Booking;
import apd.booking.BookingTask1;
import apd.concert.*;
import apd.lock.LockFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public class Scenario1 {

    private final Concert concert;
    private final ExecutorService executorService;;
    private final boolean threadSafe;

    public Scenario1(Concert concert, int numberOfThreads, boolean threadSafe) {
        this.concert = concert;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads); // Thread pool for concurrent apd.booking
        this.threadSafe = threadSafe;
    }

    public Future<String> book(Callable<String> task) {
        return executorService.submit(task);
    }

    /**
     * Shutdown the ExecutorService gracefully.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    public List<Booking> runBookingSimulation(int noOfBookers, int bookingAttempts, int contestSeats, Scenario1 bookingServiceMain) {
        // Simulate multiple seat apd.booking attempts
        List<Future<String>> results = new ArrayList<>();
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
            BookingTask1 bt1 = new BookingTask1(booker, concert, seatId, bookingServiceMain.threadSafe);
            results.add(book(bt1));
        }

        // Check the results of apd.booking attempts
        for (int i = 0; i < results.size(); i++) {
            try {
                String res = results.get(i).get(); // Retrieve the apd.booking result from each future
//                System.out.println(res);
            } catch (Exception e) {
                System.err.println("Error during apd.booking attempt " + (i + 1) + ": " + e.getMessage());
            }
        }

        ArrayList<Booking> bookingList = new ArrayList<>();
        for (Booker booker : bookers) {
            bookingList.addAll(booker.getBookings());
        }
        bookingServiceMain.shutdown();
        return bookingList;
    }

    public static void main(String[] args) {
        int threadCount = 1000;
        int totalSeats = 5;
        int bookingAttempts = 100;
        int noOfBookers = 100;

        int contestSeats = totalSeats;


        System.out.println("------- NOT SAFE -------");
        // Create a concert builder
        Concert.ConcertBuilder concertBuilder = new Concert.ConcertBuilder(1);
        // Adding seats to the concert
        for (int i = 1; i <= totalSeats; i++) {
            concertBuilder.addSeat(i, new Seat(i, "Standard"));
        }
        // Build a concert object
        Concert concertUnsafe = concertBuilder.build();
        Scenario1 threadNotSafeBookingSvc = new Scenario1(concertUnsafe, threadCount, false); // Using 10 threads
        List<Booking> bookingUnsafe = threadNotSafeBookingSvc.runBookingSimulation(noOfBookers, bookingAttempts, contestSeats, threadNotSafeBookingSvc);

        // Carry out test case
        ScenarioTest.testScenario(concertUnsafe,bookingUnsafe);



        System.out.println("------- SAFE -------");
        // Create a concert builder
        Concert.ConcertBuilder concertBuilder2 = new Concert.ConcertBuilder(2);
        // Adding seats to the concert
        for (int i = 1; i <= totalSeats; i++) {
            concertBuilder2.addSeat(i, new Seat(i, "Standard"));
        }
        // Build a concert object
        Concert concertSafe = concertBuilder2.build();
        Scenario1 threadSafeBookingSvc = new Scenario1(concertSafe, threadCount, true); // Using 10 threads
        List<Booking> bookingSafe = threadSafeBookingSvc.runBookingSimulation(noOfBookers, bookingAttempts, contestSeats, threadSafeBookingSvc);

        // Carry out test case
        ScenarioTest.testScenario(concertSafe,bookingSafe);
    }
}