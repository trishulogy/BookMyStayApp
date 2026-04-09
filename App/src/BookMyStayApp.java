import java.io.*;
import java.util.*;

// 🔹 Custom Exception
class InvalidBookingException extends Exception {
    InvalidBookingException(String msg) { super(msg); }
}

// 🔹 Reservation (Serializable for UC12)
class Reservation implements Serializable {
    String guestName, roomType, reservationId, roomId;
    boolean isCancelled = false;

    Reservation(String g, String r) {
        guestName = g;
        roomType = r;
    }
}

// 🔹 Inventory (Serializable)
class RoomInventory implements Serializable {
    HashMap<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    synchronized int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    synchronized void reduce(String type) throws InvalidBookingException {
        if (getAvailability(type) <= 0)
            throw new InvalidBookingException("No rooms for " + type);

        availability.put(type, getAvailability(type) - 1);
    }

    synchronized void increase(String type) {
        availability.put(type, getAvailability(type) + 1);
    }

    void display() {
        System.out.println("\nInventory:");
        for (String k : availability.keySet())
            System.out.println(k + " -> " + availability.get(k));
    }
}

// 🔹 Shared Queue (Thread Safe Access)
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    synchronized void add(Reservation r) {
        queue.add(r);
    }

    synchronized Reservation get() {
        return queue.poll();
    }

    synchronized boolean has() {
        return !queue.isEmpty();
    }
}

// 🔹 Booking Service (Thread Safe)
class BookingService {

    private Set<String> usedIds = new HashSet<>();
    private List<Reservation> confirmed = new ArrayList<>();
    private int counter = 1;

    synchronized void processOne(Reservation r, RoomInventory inv) {
        try {
            if (r.guestName == null || r.guestName.isEmpty())
                throw new InvalidBookingException("Invalid name");

            if (!inv.availability.containsKey(r.roomType))
                throw new InvalidBookingException("Invalid room type");

            inv.reduce(r.roomType);

            String roomId = r.roomType.replace(" ", "").toUpperCase() + counter++;
            if (usedIds.contains(roomId))
                throw new InvalidBookingException("Duplicate allocation");

            usedIds.add(roomId);

            r.roomId = roomId;
            r.reservationId = "RES" + counter;

            confirmed.add(r);

            System.out.println(Thread.currentThread().getName() +
                    " booked " + r.guestName + " -> " + roomId);

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    List<Reservation> getConfirmed() {
        return confirmed;
    }
}

// 🔹 UC11: Concurrent Processor
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private BookingService service;
    private RoomInventory inventory;

    BookingProcessor(BookingQueue q, BookingService s, RoomInventory i) {
        queue = q;
        service = s;
        inventory = i;
    }

    public void run() {
        while (true) {
            Reservation r;

            synchronized (queue) {
                if (!queue.has()) break;
                r = queue.get();
            }

            if (r != null) {
                service.processOne(r, inventory);
            }
        }
    }
}

// 🔹 UC12: Persistence Service
class PersistenceService {

    private static final String FILE = "data.ser";

    // Save
    void save(RoomInventory inv, List<Reservation> list) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE))) {

            out.writeObject(inv);
            out.writeObject(list);

            System.out.println("\nData saved successfully.");

        } catch (Exception e) {
            System.out.println("Save failed.");
        }
    }

    // Load
    Object[] load() {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(FILE))) {

            RoomInventory inv = (RoomInventory) in.readObject();
            List<Reservation> list = (List<Reservation>) in.readObject();

            System.out.println("\nData loaded successfully.");

            return new Object[]{inv, list};

        } catch (Exception e) {
            System.out.println("\nNo previous data found. Starting fresh.");
            return null;
        }
    }
}

// 🔹 MAIN
public class BookMyStayApp {

    public static void main(String[] args) {

        PersistenceService ps = new PersistenceService();

        RoomInventory inventory;
        List<Reservation> history;

        // 🔹 UC12: Load
        Object[] data = ps.load();

        if (data != null) {
            inventory = (RoomInventory) data[0];
            history = (List<Reservation>) data[1];
        } else {
            inventory = new RoomInventory();
            history = new ArrayList<>();
        }

        // 🔹 UC11: Concurrent Simulation
        BookingQueue queue = new BookingQueue();

        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Double Room"));
        queue.add(new Reservation("Charlie", "Suite Room"));
        queue.add(new Reservation("David", "Single Room"));

        BookingService service = new BookingService();

        // Multiple threads
        Thread t1 = new BookingProcessor(queue, service, inventory);
        Thread t2 = new BookingProcessor(queue, service, inventory);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {}

        // Add to history
        history.addAll(service.getConfirmed());

        inventory.display();

        // 🔹 UC12: Save
        ps.save(inventory, history);
    }
}