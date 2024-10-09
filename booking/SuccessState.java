package apd.booking;

public class SuccessState implements BookingState {
    @Override
    public void handleRequest(Booking booking) {
        System.out.println("Booking is success!.");
    }
}
