package apd.booking;

public class PendingState implements BookingState {
    @Override
    public void handleRequest(Booking booking) {
        System.out.println("Booking is in Pending State.");
        booking.setState(new SuccessState());
    }
}