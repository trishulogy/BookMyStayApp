import java.util.HashMap;

/**
 * BookMyStayApp - Entry point of the Hotel Booking Management System
 * Demonstrates Room Types and Centralized Inventory using HashMap
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
        System.out.println("Price: ₹" + price);
    }
}

// 🔹 Single Room
class SingleRoom extends Room {
    SingleRoom() {
        super("Single Room", 1, 1000);
    }
}

// 🔹 Double Room
class DoubleRoom extends Room {
    DoubleRoom() {
        super("Double Room", 2, 2000);
    }
}

// 🔹 Suite Room
class SuiteRoom extends Room {
    SuiteRoom() {
        super("Suite Room", 3, 5000);
    }
}

// 🔹 UC3: Inventory Class
class RoomInventory {

    private HashMap<String, Integer> availability;

    // Constructor
    RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    // Get availability
    int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    // Update availability
    void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    // Display inventory
    void displayInventory() {
        System.out.println("\n===== Current Room Inventory =====");

        for (String type : availability.keySet()) {
            System.out.println(type + " -> Available: " + availability.get(type));
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

        // UC2: Room Objects
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        System.out.println("\n===== Room Details =====");

        r1.displayDetails();
        System.out.println("-----------------------");

        r2.displayDetails();
        System.out.println("-----------------------");

        r3.displayDetails();

        // UC3: Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();
    }
}