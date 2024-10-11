package apd;
import apd.concert.*;
import apd.booking.*;

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


    public Scenario2(Concert concert, boolean useSemaphore) {
        this.concert = concert;
        // Initialize semaphore to limit concurrent access to 10 users at a time
        this.semaphore = useSemaphore ? new Semaphore(10) : null;
    }

    public Concert getConcert(){
        return concert;
    }
    public Semaphore getSemaphore(){
        return semaphore;
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
        List<Future<List<Booking>>> futures = new ArrayList<>();
        Random random = new Random();

        // Simulate users attempting to book random seats
        for (int i = 0; i < noOfUsers; i++) {
            int seatId = random.nextInt(totalSeats)+1; // Random seat ID between 0 and totalSeats-1
            BookingTask2 task = new BookingTask2(new Booker(i,"booker"+i), scenario.getConcert(),seatId,scenario.getSemaphore() );
            futures.add(executorService.submit(task));
        }

        HashMap<Integer, Integer> checking = new HashMap<>();
        // Process the results of the booking attempts
        for (Future<List<Booking>> future : futures) {
            List<Booking> bookings = future.get();
            for(Booking booking : bookings){
                int seatID = booking.getSeatId();
                if (checking.containsKey(seatID)) {
                    checking.put(seatID, checking.get(seatID) + 1);
                } else {
                    checking.put(seatID, 1);
                }
            }
        }
        System.out.println(checking);
        executorService.shutdown();
    }
}
