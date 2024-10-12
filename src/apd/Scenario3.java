package apd;

import apd.booking.Booker;
import apd.booking.Booking;
import apd.booking.BookingTask3;
import apd.concert.Concert;
import apd.concert.Seat;

import java.util.*;
import java.util.concurrent.*;

public class Scenario3 {

    private final ExecutorService execSvc;

    public Scenario3(int numOfThreads) {
        this.execSvc = Executors.newFixedThreadPool(numOfThreads);
    }

    public Future<String> book(Callable<String> task) {
        return execSvc.submit(task);
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
        *   5. case 2: [unsuccessful booking]
        * */

        int numOfBookers = 50;
        int numOfSeats = 30;
        // Create a concert builder
        Concert.ConcertBuilder concertBuilder = new Concert.ConcertBuilder(2);
        // Adding seats to the concert
        for (int i = 1; i <= numOfSeats; i++) {
            concertBuilder.addSeat(i, new Seat(i, "Standard"));
        }
        // Build a concert object
        Concert concert = concertBuilder.build();
        Scenario3 timeDelayLocking = new Scenario3(numOfBookers);

        List<Future<String>> results = new ArrayList<>();

        // Create bookers for testing
        List<Booker> bookers = new ArrayList<>();
        for (int i = 1; i <= numOfBookers; i++) {
            bookers.add(new Booker(i, "booker-" + i));
        }

        Random rand = new Random();

        // randomly choose a seat to book
        for (int i = 0; i < numOfBookers; i++) {
            int seatId = rand.nextInt(numOfSeats) + 1;
            Booker booker = bookers.get(rand.nextInt(bookers.size()));
            BookingTask3 bt3 = new BookingTask3(booker, concert, seatId);
            results.add(timeDelayLocking.book(bt3));
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

        ArrayList<Booking> bookingList = new ArrayList<>();
        for (Booker booker : bookers) {
            System.out.println(booker.toString());
            bookingList.addAll(booker.getBookings());
        }
        // Carry out test case
        ScenarioTest.testScenario(concert,bookingList);
    }
}
