// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.Map;
// import java.util.HashMap;


// public class ConcertBookingSv {


//     // Scenario 1: Booking a single seat
//     public boolean reserveSeat(int seatId) {
//         if (seatAvailability.getOrDefault(seatId, false)) {
//             seatAvailability.put(seatId, false);  // Reserve the seat
//             return true;
//         }
//         return false;
//     }

//     // Scenario 2: Reserve from available seat inventory
//     public boolean decrementSeatInventory() {
//         if (availableSeats.get() > 0) {
//             return availableSeats.decrementAndGet() >= 0;
//         }
//         return false;
//     }

//     // Scenario 3: Booking multiple adjacent seats (group booking)
//     public boolean reserveAdjacentSeats(int[] seatIds) {
//         // Check if all seats are available
//         for (int seatId : seatIds) {
//             if (!seatAvailability.getOrDefault(seatId, false) || groupSeatLocks.get(seatId)) {
//                 return false;  // If any seat is not available, booking fails
//             }
//         }

//         // Reserve all adjacent seats
//         for (int seatId : seatIds) {
//             seatAvailability.put(seatId, false);
//             groupSeatLocks.put(seatId, true);
//         }
//         return true;
//     }

//     // Scenario 4: Cancel booking and make seat available again
//     public synchronized boolean cancelSeat(int seatId) {
//         if (!seatAvailability.getOrDefault(seatId, true)) {
//             seatAvailability.put(seatId, true);  // Seat is now available
//             groupSeatLocks.put(seatId, false);   // Unlock seat block
//             return true;
//         }
//         return false;
//     }


//     // Utility method to check available seats count
//     public int getAvailableSeats() {
//         return availableSeats.get();
//     }

//     // Utility method to check if a seat is available
//     public boolean isSeatAvailable(int seatId) {
//         return seatAvailability.getOrDefault(seatId, false);
//     }

//     // Utility method to check if adjacent seats are available
//     public boolean areAdjacentSeatsAvailable(int[] seatIds) {
//         for (int seatId : seatIds) {
//             if (!seatAvailability.getOrDefault(seatId, false) || groupSeatLocks.get(seatId)) {
//                 return false;
//             }
//         }
//         return true;
//     }


// }

