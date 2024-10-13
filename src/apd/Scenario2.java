package apd;
import apd.concert.*;
import apd.booking.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Scenario2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int totalSeats = 100; // Total number of seats available
        int noOfUsers = 1000;  // Number of users trying to book seats

        // Simulate booking without semaphore
        System.out.println("------- NOT SAFE -------");
        System.out.println("Running without semaphore...");
        ExecutorService executorServiceWO = Executors.newFixedThreadPool(1000);
        // Create a concert builder
        Concert.ConcertBuilder concertBuilderWOsem = new Concert.ConcertBuilder(1);
        // Adding seats to the concert
        for (int i = 1; i <= totalSeats; i++) {
            concertBuilderWOsem.addSeat(i, new Seat(i, "Standard"));
        }
        // Build a concert object
        Concert concertWithoutSemaphore = concertBuilderWOsem.build();
        List<Booking> bookingResultWOsem = runBookingSimulation(executorServiceWO, concertWithoutSemaphore, noOfUsers, null);

        // Wait for tasks to finish
        executorServiceWO.shutdown();
        if (!executorServiceWO.awaitTermination(2, TimeUnit.MINUTES)) {
            System.out.println("Some tasks did not finish in the allotted time.");
        }
        // Carry out test case
        ScenarioTest.testScenario(concertWithoutSemaphore,bookingResultWOsem);



        // Simulate booking with semaphore
        System.out.println("------- SAFE -------");
        System.out.println("Running with semaphore...");
        ExecutorService executorServiceW = Executors.newFixedThreadPool(10);
        // Create a concert builder
        Concert.ConcertBuilder concertBuilderWsem = new Concert.ConcertBuilder(2);
        // Adding seats to the concert
        for (int i = 1; i <= totalSeats; i++) {
            concertBuilderWsem.addSeat(i, new Seat(i, "Standard"));
        }
        // Build a concert object
        Concert concertWithSemaphore = concertBuilderWsem.build();
        Semaphore semaphore = new Semaphore(10);
        List<Booking> bookingResultWsem =  runBookingSimulation(executorServiceW, concertWithSemaphore, noOfUsers, semaphore);

        // Wait for tasks to finish
        executorServiceW.shutdown();
        if (!executorServiceW.awaitTermination(2, TimeUnit.MINUTES)) {
            System.out.println("Some tasks did not finish in the allotted time.");
        }
        // Carry out test case
        ScenarioTest.testScenario(concertWithSemaphore,bookingResultWsem);

    }

    /**
     * Simulates booking attempts for the given number of users.
     * 
     * @param executorService The thread pool for concurrent execution.
     * @param concert The concert that bookings will be made.
     * @param noOfUsers The number of users attempting to book seats.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static List<Booking> runBookingSimulation(
            ExecutorService executorService,
            Concert concert,
            int noOfUsers,
            Semaphore semaphore) throws InterruptedException, ExecutionException {
        List<Future<List<Booking>>> futures = new ArrayList<>();
        Random random = new Random();

        // Simulate users attempting to book random seats
        for (int i = 0; i < noOfUsers; i++) {
            int seatId = random.nextInt(concert.getTotalSeats())+1; // Random seat ID between 0 and totalSeats-1
            BookingTask2 task = new BookingTask2(new Booker(i,"booker"+i), concert,seatId,semaphore);
            futures.add(executorService.submit(task));
        }

        ArrayList<Booking> bookingList = new ArrayList<>();
        for (Future<List<Booking>> future : futures) {
            List<Booking> bookings = future.get();
            bookingList.addAll(bookings);
        }

        executorService.shutdown();
        return bookingList;
    }
}
