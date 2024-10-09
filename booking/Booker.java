import Booking;

public class Booker {
    private int id;
    private String name;
    private List<Booking> bookings;

    public Booker(int id, String name) {
        this.id = id;
        this.name = name;
        this.bookings = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
}
