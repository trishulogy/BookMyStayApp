import java.util.*;

/**
 * BookMyStayApp - Entry point of the Hotel Booking Management System
 * Demonstrates Rooms, Inventory, Booking, Allocation, and Add-On Services
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

// 🔹 UC3: Inventory
class RoomInventory {
    private HashMap<String, Integer> availability;

    RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    void reduceAvailability(String type) {
        if (getAvailability(type) > 0) {
            availability.put(type, getAvailability(type) - 1);
        }
    }

    void displayInventory() {
        System.out.println("\n===== Updated Inventory =====");
        for (String t : availability.keySet()) {
            System.out.println(t + " -> Available: " + availability.get(t));
        }
    }
}

// 🔹 UC4: Search
class SearchService {
    void searchAvailableRooms(Room[] rooms, RoomInventory inventory) {
        System.out.println("\n===== Available Rooms =====");

        for (Room r : rooms) {
            int available = inventory.getAvailability(r.type);
            if (available > 0) {
                r.displayDetails();
                System.out.println("Available: " + available);
                System.out.println("-----------------------");
            }
        }
    }
}

// 🔹 UC5: Reservation
class Reservation {
    String guestName;
    String roomType;
    String reservationId;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// 🔹 UC5: Queue
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    void addRequest(Reservation r) {
        queue.add(r);
        System.out.println("Request added for " + r.guestName);
    }

    Reservation getNext() {
        return queue.poll();
    }

    boolean hasRequests() {
        return !queue.isEmpty();
    }
}

// 🔹 UC6: Booking Service
class BookingService {

    private Set<String> assignedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> allocations = new HashMap<>();
    private int counter = 1;

    // store reservationId mapping
    private List<Reservation> confirmedReservations = new ArrayList<>();

    List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }

    void processBookings(BookingQueue queue, RoomInventory inventory) {

        System.out.println("\n===== Processing Bookings =====");

        while (queue.hasRequests()) {

            Reservation r = queue.getNext();

            int available = inventory.getAvailability(r.roomType);

            if (available > 0) {

                String roomId = r.roomType.replace(" ", "").toUpperCase() + counter++;

                if (!assignedRoomIds.contains(roomId)) {

                    assignedRoomIds.add(roomId);

                    allocations.putIfAbsent(r.roomType, new HashSet<>());
                    allocations.get(r.roomType).add(roomId);

                    inventory.reduceAvailability(r.roomType);

                    // assign reservation ID
                    r.reservationId = "RES" + counter;

                    confirmedReservations.add(r);

                    System.out.println("Booking Confirmed!");
                    System.out.println("Guest: " + r.guestName);
                    System.out.println("Room: " + r.roomType);
                    System.out.println("Room ID: " + roomId);
                    System.out.println("Reservation ID: " + r.reservationId);
                    System.out.println("-----------------------");
                }

            } else {
                System.out.println("Booking Failed for " + r.guestName);
            }
        }
    }
}

// 🔹 UC7: Add-On Service
class AddOnService {
    String name;
    double price;

    AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }
}

// 🔹 UC7: Service Manager
class AddOnServiceManager {

    private HashMap<String, List<AddOnService>> servicesMap = new HashMap<>();

    void addService(String reservationId, AddOnService service) {
        servicesMap.putIfAbsent(reservationId, new ArrayList<>());
        servicesMap.get(reservationId).add(service);
    }

    void showServices(String reservationId) {
        System.out.println("\nServices for " + reservationId);

        List<AddOnService> list = servicesMap.get(reservationId);

        if (list == null) {
            System.out.println("No services selected.");
            return;
        }

        double total = 0;

        for (AddOnService s : list) {
            System.out.println("- " + s.name + " : Rs." + s.price);
            total += s.price;
        }

        System.out.println("Total Add-On Cost: Rs." + total);
    }
}

// 🔹 Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // UC1
        System.out.println("===================================");
        System.out.println(" Welcome to BookMyStayApp ");
        System.out.println(" Hotel Booking System v1.0 ");
        System.out.println("===================================");

        // UC2
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // UC3
        RoomInventory inventory = new RoomInventory();

        // UC4
        new SearchService().searchAvailableRooms(rooms, inventory);

        // UC5
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));

        // UC6
        BookingService bookingService = new BookingService();
        bookingService.processBookings(queue, inventory);

        inventory.displayInventory();

        // UC7
        AddOnServiceManager manager = new AddOnServiceManager();

        List<Reservation> confirmed = bookingService.getConfirmedReservations();

        if (!confirmed.isEmpty()) {

            Reservation r = confirmed.get(0);

            manager.addService(r.reservationId, new AddOnService("Breakfast", 200));
            manager.addService(r.reservationId, new AddOnService("Spa", 500));

            manager.showServices(r.reservationId);
        }
    }
}