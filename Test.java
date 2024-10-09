package apd;
import apd.booking.*;
import apd.concert.*;
import apd.lock.*;

public class Test {
    public static void main(String[] args) {
        Concert concert = new Concert.ConcertBuilder(1, 100).build();

        Runnable bookingTask = () -> {
            boolean success = concert.bookSeat(10);
            System.out.println(Thread.currentThread().getName() + " attempted to book seat 10: " + success);
        };

        Thread thread1 = new Thread(bookingTask);
        Thread thread2 = new Thread(bookingTask);
        Thread thread3 = new Thread(bookingTask);

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
