public class CancelledState implements BookingState {
    @Override
    public void handleRequest(Booking booking) {
        System.out.println("Booking has been Cancelled.");
    }
}
