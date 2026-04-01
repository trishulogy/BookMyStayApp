import java.util.*;

/**
 * BookMyStayApp - Entry point of the Hotel Booking Management System
 * Demonstrates Room Types, Inventory, Search, Booking Requests, and Allocation
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
    SingleRoom() {
        super("Single Room", 1, 1000);
    }
}

class DoubleRoom extends Room {
    DoubleRoom() {
        super("Double Room", 2, 2000);
    }
}

class SuiteRoom extends Room {
    SuiteRoom() {
        super("Suite Room", 3, 5000);
    }
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

    int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    void reduceAvailability(String roomType) {
        int current = getAvailability(roomType);
        if (current > 0) {
            availability.put(roomType, current - 1);
        }
    }

    void displayInventory() {
        System.out.println("\n===== Updated Inventory =====");
        for (String type : availability.keySet()) {
            System.out.println(type + " -> Available: " + availability.get(type));
        }
    }
}

// 🔹 UC4: Search
class SearchService {
    void searchAvailableRooms(Room[] rooms, RoomInventory inventory) {

        System.out.println("\n===== Available Rooms =====");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.type);

            if (available > 0) {
                room.displayDetails();
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

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// 🔹 UC5: Booking Queue
class BookingQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    void addRequest(Reservation r) {
        queue.add(r);
        System.out.println("Request added for " + r.guestName);
    }

    Reservation getNextRequest() {
        return queue.poll(); // FIFO
    }

    boolean hasRequests() {
        return !queue.isEmpty();
    }
}

// 🔹 UC6: Booking Service (Allocation)
class BookingService {

    private Set<String> assignedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> roomAllocations = new HashMap<>();
    private int idCounter = 1;

    void processBookings(BookingQueue queue, RoomInventory inventory) {

        System.out.println("\n===== Processing Bookings =====");

        while (queue.hasRequests()) {

            Reservation r = queue.getNextRequest();

            int available = inventory.getAvailability(r.roomType);

            if (available > 0) {

                // Generate unique room ID
                String roomId = r.roomType.replace(" ", "").toUpperCase() + idCounter++;

                // Ensure uniqueness
                if (!assignedRoomIds.contains(roomId)) {

                    assignedRoomIds.add(roomId);

                    // Map room type → allocated IDs
                    roomAllocations.putIfAbsent(r.roomType, new HashSet<>());
                    roomAllocations.get(r.roomType).add(roomId);

                    // Update inventory
                    inventory.reduceAvailability(r.roomType);

                    System.out.println("Booking Confirmed!");
                    System.out.println("Guest: " + r.guestName);
                    System.out.println("Room Type: " + r.roomType);
                    System.out.println("Room ID: " + roomId);
                    System.out.println("-----------------------");

                }

            } else {
                System.out.println("Booking Failed for " + r.guestName + " (No rooms available)");
            }
        }
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
        SearchService search = new SearchService();
        search.searchAvailableRooms(rooms, inventory);

        // UC5
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Suite Room")); // extra test

        // UC6
        BookingService bookingService = new BookingService();
        bookingService.processBookings(queue, inventory);

        // Show updated inventory
        inventory.displayInventory();
    }
}