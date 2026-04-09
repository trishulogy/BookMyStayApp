import java.util.*;

/**
 * BookMyStayApp - Hotel Booking System
 * Includes Validation, Booking, Cancellation, and Reporting
 *
 * @author Yato
 * @version 1.0
 */

// 🔹 Custom Exception (UC9)
class InvalidBookingException extends Exception {
    InvalidBookingException(String message) {
        super(message);
    }
}

// 🔹 Abstract Class
abstract class Room {
    String type;
    int beds;
    double price;

    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price: Rs." + price);
    }
}

// 🔹 Room Types
class SingleRoom extends Room {
    SingleRoom() { super("Single Room", 1, 1000); }
}
class DoubleRoom extends Room {
    DoubleRoom() { super("Double Room", 2, 2000); }
}
class SuiteRoom extends Room {
    SuiteRoom() { super("Suite Room", 3, 5000); }
}

// 🔹 Inventory
class RoomInventory {
    private HashMap<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    void reduceAvailability(String type) throws InvalidBookingException {
        int current = getAvailability(type);
        if (current <= 0) {
            throw new InvalidBookingException("No rooms available for " + type);
        }
        availability.put(type, current - 1);
    }

    void increaseAvailability(String type) {
        availability.put(type, getAvailability(type) + 1);
    }

    boolean isValidRoomType(String type) {
        return availability.containsKey(type);
    }

    void displayInventory() {
        System.out.println("\n===== Inventory =====");
        for (String t : availability.keySet()) {
            System.out.println(t + " -> " + availability.get(t));
        }
    }
}

// 🔹 Reservation
class Reservation {
    String guestName;
    String roomType;
    String reservationId;
    String roomId;
    boolean isCancelled = false;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// 🔹 Queue
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    void add(Reservation r) {
        queue.add(r);
    }

    Reservation next() {
        return queue.poll();
    }

    boolean has() {
        return !queue.isEmpty();
    }
}

// 🔹 Booking Service (with Validation)
class BookingService {

    private Set<String> usedRoomIds = new HashSet<>();
    private int counter = 1;

    private List<Reservation> confirmed = new ArrayList<>();

    List<Reservation> getConfirmed() {
        return confirmed;
    }

    void process(BookingQueue q, RoomInventory inv) {

        System.out.println("\n===== Processing Bookings =====");

        while (q.has()) {

            Reservation r = q.next();

            try {
                // 🔴 UC9 VALIDATIONS

                if (r.guestName == null || r.guestName.isEmpty()) {
                    throw new InvalidBookingException("Guest name cannot be empty");
                }

                if (!inv.isValidRoomType(r.roomType)) {
                    throw new InvalidBookingException("Invalid room type: " + r.roomType);
                }

                // Allocation
                String roomId = r.roomType.replace(" ", "").toUpperCase() + counter++;

                if (usedRoomIds.contains(roomId)) {
                    throw new InvalidBookingException("Duplicate room allocation attempt");
                }

                // Reduce inventory safely
                inv.reduceAvailability(r.roomType);

                usedRoomIds.add(roomId);

                r.roomId = roomId;
                r.reservationId = "RES" + counter;

                confirmed.add(r);

                System.out.println("Booking Confirmed: " + r.guestName + " -> " + roomId);

            } catch (InvalidBookingException e) {
                // 🔥 Graceful failure (NO crash)
                System.out.println("Booking Failed: " + e.getMessage());
            }
        }
    }
}

// 🔹 Cancellation (same as UC10)
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    void cancel(String reservationId,
                List<Reservation> history,
                RoomInventory inv) {

        for (Reservation r : history) {

            if (r.reservationId.equals(reservationId) && !r.isCancelled) {

                rollbackStack.push(r.roomId);

                inv.increaseAvailability(r.roomType);

                r.isCancelled = true;

                System.out.println("\nCancelled: " + reservationId);
                return;
            }
        }

        System.out.println("\nInvalid cancellation request");
    }
}

// 🔹 Main
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== BookMyStayApp =====");

        RoomInventory inv = new RoomInventory();

        BookingQueue q = new BookingQueue();

        // ✅ Valid
        q.add(new Reservation("Alice", "Single Room"));

        // ❌ Invalid room type
        q.add(new Reservation("Bob", "Luxury Room"));

        // ❌ Empty name
        q.add(new Reservation("", "Double Room"));

        BookingService bs = new BookingService();
        bs.process(q, inv);

        inv.displayInventory();

        // Cancel test
        CancellationService cs = new CancellationService();

        if (!bs.getConfirmed().isEmpty()) {
            cs.cancel(bs.getConfirmed().get(0).reservationId,
                      bs.getConfirmed(),
                      inv);
        }

        inv.displayInventory();
    }
}