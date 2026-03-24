/**
 * BookMyStayApp - Entry point of the Hotel Booking Management System
 * Demonstrates Room Types using Abstraction & Inheritance
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

// 🔹 Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // 🔥 Keep your UC1 welcome message ALSO
        System.out.println("===================================");
        System.out.println(" Welcome to BookMyStayApp ");
        System.out.println(" Hotel Booking System v1.0 ");
        System.out.println("===================================");

        System.out.println("\n===== Room Availability =====");

        // Create Room Objects
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        // Static availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display
        r1.displayDetails();
        System.out.println("Available: " + singleAvailable);
        System.out.println("-----------------------");

        r2.displayDetails();
        System.out.println("Available: " + doubleAvailable);
        System.out.println("-----------------------");

        r3.displayDetails();
        System.out.println("Available: " + suiteAvailable);
    }
}