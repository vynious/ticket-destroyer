package apd;
import apd.concert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Scenario2 {

    private final Concert concert;
    private final Semaphore semaphore; // Semaphore to limit concurrent booking attempts
    private final boolean useSemaphore;

    public Scenario2(Concert concert, boolean useSemaphore) {
        this.concert = concert;
        this.useSemaphore = useSemaphore;
        // Initialize semaphore to limit concurrent access to 10 users at a time
        this.semaphore = useSemaphore ? new Semaphore(10) : null;
    }

    /**
     * Attempts to book a specific seat concurrently.
     *
     * @param seatId The ID of the seat to book.
     * @return Future<Boolean> indicating success (true) or failure (false) of the booking.
     */
    public Future<Integer> bookSeat(ExecutorService executorService, int seatId) {
        return executorService.submit(() -> {
            if (useSemaphore) {
                // If semaphore is enabled, acquire permit before booking
                Thread.sleep(10); // Simulate processing delay
                semaphore.acquire();
            }
        
            try {
                Seat seat = concert.getSeatById(seatId);
                Thread.sleep(10); // Simulate processing delay
                boolean success;
                if (seat != null) {
                    if(useSemaphore){
                        success = concert.bookSeat(seatId);
                    }
                    else{
                        success = concert.bookSeatNotSafe(seatId); // Attempt to book the seat
                    }
                    if (!success) {
                        // System.out.println(Thread.currentThread().getName() + " failed to book seat " + seatId);
                        return -1;
                    } else {
                        System.out.println(Thread.currentThread().getName() + " successfully booked seat " + seatId);
                    }
                    return seatId;
                }
                return -1; // Return false if seat doesn't exist
            } finally {
                // If semaphore is enabled, release the permit after booking attempt
                if (useSemaphore) {
                    Thread.sleep(10); // Simulate processing delay
                    semaphore.release();
                }
            }
        });
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int totalSeats = 100; // Total number of seats available
        int noOfUsers = 1000;  // Number of users trying to book seats

        // Simulate booking without semaphore
        ExecutorService executorServiceWO = Executors.newFixedThreadPool(1000);
        Concert concertWithoutSemaphore = new Concert.ConcertBuilder(1, totalSeats).build();
        System.out.println("Running without semaphore...");
        Scenario2 scenarioWOsem = new Scenario2(concertWithoutSemaphore, false);
        runBookingSimulation(executorServiceWO, scenarioWOsem, noOfUsers, totalSeats);

        // Wait for tasks to finish
        executorServiceWO.shutdown();
        if (!executorServiceWO.awaitTermination(2, TimeUnit.MINUTES)) {
            System.out.println("Some tasks did not finish in the allotted time.");
        }
        System.out.println(concertWithoutSemaphore.toString());

        // Simulate booking with semaphore
        ExecutorService executorServiceW = Executors.newFixedThreadPool(10);
        System.out.println("Running with semaphore...");
        Concert concertWithSemaphore = new Concert.ConcertBuilder(2, totalSeats).build();
        Scenario2 scenarioWsem = new Scenario2(concertWithSemaphore, true);
        runBookingSimulation(executorServiceW, scenarioWsem, noOfUsers, totalSeats);

        // Wait for tasks to finish
        executorServiceW.shutdown();
        if (!executorServiceW.awaitTermination(2, TimeUnit.MINUTES)) {
            System.out.println("Some tasks did not finish in the allotted time.");
        }
        System.out.println(concertWithSemaphore.toString());
    }

    /**
     * Simulates booking attempts for the given number of users.
     * 
     * @param executorService The thread pool for concurrent execution.
     * @param scenario The booking scenario to run.
     * @param noOfUsers The number of users attempting to book seats.
     * @param totalSeats The total number of seats available.
     * @throws InterruptedException
     * @throws ExecutionException 
     */
    public static void runBookingSimulation(ExecutorService executorService, Scenario2 scenario, int noOfUsers, int totalSeats) throws InterruptedException, ExecutionException {
        List<Future<Integer>> futures = new ArrayList<>();
        Random random = new Random();

        // Simulate users attempting to book random seats
        for (int i = 0; i < noOfUsers; i++) {
            int seatId = random.nextInt(totalSeats)+1; // Random seat ID between 0 and totalSeats-1
            futures.add(scenario.bookSeat(executorService, seatId));
        }

        HashMap<Integer, Integer> checking = new HashMap<>();
        // Process the results of the booking attempts
        for (Future<Integer> future : futures) {
            Integer seatID = future.get();

            if (checking.containsKey(seatID)) {
                checking.put(seatID, checking.get(seatID) + 1);
            } else {
                checking.put(seatID, 1);
            }
        }
        System.out.println(checking);
        executorService.shutdown();
    }
}
