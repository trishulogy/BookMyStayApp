import java.util.HashMap;

/**
 * BookMyStayApp - Entry point of the Hotel Booking Management System
 * Demonstrates Room Types, Inventory, and Search Functionality
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

// 🔹 UC3: Inventory Class
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

    void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    void displayInventory() {
        System.out.println("\n===== Current Room Inventory =====");
        for (String type : availability.keySet()) {
            System.out.println(type + " -> Available: " + availability.get(type));
        }
    }
}

// 🔹 UC4: Search Service (READ ONLY)
class SearchService {

    void searchAvailableRooms(Room[] rooms, RoomInventory inventory) {

        System.out.println("\n===== Available Rooms =====");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.type);

            // ✅ Only show if available > 0
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println("-----------------------");
            }
        }
    }
}

// 🔹 Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // 🔹 UC1: Welcome
        System.out.println("===================================");
        System.out.println(" Welcome to BookMyStayApp ");
        System.out.println(" Hotel Booking System v1.0 ");
        System.out.println("===================================");

        // 🔹 UC2: Room Objects
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        Room[] rooms = {r1, r2, r3};

        // 🔹 UC3: Inventory
        RoomInventory inventory = new RoomInventory();

        // 🔹 UC4: Search (READ ONLY)
        SearchService search = new SearchService();
        search.searchAvailableRooms(rooms, inventory);
    }
}