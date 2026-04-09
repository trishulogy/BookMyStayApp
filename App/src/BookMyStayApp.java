import java.util.*;

/**
 * BookMyStayApp - Hotel Booking System
 * Now includes Cancellation & Inventory Rollback
 *
 * @author Yato
 * @version 1.0
 */

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

    void reduceAvailability(String type) {
        availability.put(type, getAvailability(type) - 1);
    }

    void increaseAvailability(String type) {
        availability.put(type, getAvailability(type) + 1);
    }

    void displayInventory() {
        System.out.println("\n===== Current Inventory =====");
        for (String t : availability.keySet()) {
            System.out.println(t + " -> " + availability.get(t));
        }
    }
}

// 🔹 Search
class SearchService {
    void search(Room[] rooms, RoomInventory inv) {
        System.out.println("\n===== Available Rooms =====");

        for (Room r : rooms) {
            int avail = inv.getAvailability(r.type);
            if (avail > 0) {
                r.displayDetails();
                System.out.println("Available: " + avail);
                System.out.println("-----------------------");
            }
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

// 🔹 Booking Service
class BookingService {

    private Set<String> usedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> allocations = new HashMap<>();
    private int counter = 1;

    private List<Reservation> confirmed = new ArrayList<>();

    List<Reservation> getConfirmed() {
        return confirmed;
    }

    void process(BookingQueue q, RoomInventory inv) {

        while (q.has()) {

            Reservation r = q.next();

            if (inv.getAvailability(r.roomType) > 0) {

                String roomId = r.roomType.replace(" ", "").toUpperCase() + counter++;

                if (!usedRoomIds.contains(roomId)) {

                    usedRoomIds.add(roomId);
                    r.roomId = roomId;
                    r.reservationId = "RES" + counter;

                    allocations.putIfAbsent(r.roomType, new HashSet<>());
                    allocations.get(r.roomType).add(roomId);

                    inv.reduceAvailability(r.roomType);
                    confirmed.add(r);

                    System.out.println("Booked: " + r.guestName + " -> " + roomId);
                }

            } else {
                System.out.println("Failed: " + r.guestName);
            }
        }
    }
}

// 🔹 UC10: Cancellation Service
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    void cancel(String reservationId,
                List<Reservation> history,
                RoomInventory inventory) {

        for (Reservation r : history) {

            if (r.reservationId.equals(reservationId) && !r.isCancelled) {

                // push to stack (LIFO)
                rollbackStack.push(r.roomId);

                // restore inventory
                inventory.increaseAvailability(r.roomType);

                // mark cancelled
                r.isCancelled = true;

                System.out.println("\nCancelled Reservation: " + reservationId);
                System.out.println("Room Released: " + r.roomId);
                return;
            }
        }

        System.out.println("\nInvalid or Already Cancelled Reservation!");
    }

    void showRollbackStack() {
        System.out.println("\nRollback Stack (LIFO): " + rollbackStack);
    }
}

// 🔹 History
class BookingHistory {
    private List<Reservation> list = new ArrayList<>();

    void add(Reservation r) {
        list.add(r);
    }

    List<Reservation> getAll() {
        return list;
    }
}

// 🔹 Report
class ReportService {

    void show(List<Reservation> list) {
        System.out.println("\n===== Booking History =====");

        for (Reservation r : list) {
            System.out.println(r.reservationId + " | " + r.guestName +
                    " | " + r.roomType +
                    " | Cancelled: " + r.isCancelled);
        }
    }
}

// 🔹 Main
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== BookMyStayApp =====");

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        RoomInventory inv = new RoomInventory();
        new SearchService().search(rooms, inv);

        // Booking
        BookingQueue q = new BookingQueue();
        q.add(new Reservation("Alice", "Single Room"));
        q.add(new Reservation("Bob", "Double Room"));

        BookingService bs = new BookingService();
        bs.process(q, inv);

        // History
        BookingHistory history = new BookingHistory();
        for (Reservation r : bs.getConfirmed()) {
            history.add(r);
        }

        // UC10: Cancellation
        CancellationService cs = new CancellationService();

        // cancel first booking
        if (!history.getAll().isEmpty()) {
            String id = history.getAll().get(0).reservationId;
            cs.cancel(id, history.getAll(), inv);
        }

        cs.showRollbackStack();

        // Final state
        inv.displayInventory();
        new ReportService().show(history.getAll());
    }
}