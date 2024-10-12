package apd;

import apd.booking.*;
import apd.concert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScenarioTest {
    public static void testScenario(Concert concert, List<Booking> bookings){
        System.out.println();
        System.out.println();
        System.out.println(concert.toString());

        boolean success = true;
        // Test: make sure seat availability should not be less than 0
        if( concert.getSeatsAvailable()<0){
            System.out.println("Failed Test case: Concert available seats should not be less than 0!");
            success = false;
        }
        // Test: make sure that only no seat is booked more than once
        HashMap<Integer, Integer> seatCheck = new HashMap<>();
        ArrayList<Integer> errorSeat = new ArrayList<>();
        for (Booking booking : bookings) {
            // If the seat is already present, increment its value by 1
            seatCheck.compute(booking.getSeatId(), (key, val) -> (val == null) ? 1 : val + 1);
        }
        seatCheck.forEach((key, value) -> {
            if(value>1){
                errorSeat.add(key);
            }
        });
        if (!errorSeat.isEmpty()){
            System.out.println("Failed Test case:" + errorSeat + " have not have more than 1 booker!");
            success = false;
        }

        // If pass all test case, print out success test case
        if(success){
            System.out.println("Success Test case: All Test case has passed");
            System.out.println("Test case Result: SUCCESS");
        }else{
            System.out.println("Test case Result: FAILED");
        }
        System.out.println();
        System.out.println();
    }
}
