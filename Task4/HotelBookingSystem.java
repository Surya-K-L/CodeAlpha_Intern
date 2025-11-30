import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class HotelBookingSystem {
    private static final String ROOMS_FILE = "rooms.csv";
    private static final String BOOKINGS_FILE = "bookings.csv";
    private static final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
    private static Scanner scanner = new Scanner(System.in);

    private List<Room> rooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    public static void main(String[] args) {
        HotelBookingSystem app = new HotelBookingSystem();
        app.loadData();
        app.run();
    }

    private void loadData() {
        loadRooms();
        loadBookings();
    }

    private void loadRooms() {
        File f = new File(ROOMS_FILE);
        if (!f.exists()) {
            System.out.println("Rooms file not found. Creating default rooms...");
            createDefaultRooms();
            saveRooms();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Room r = Room.fromCSV(line);
                if (r != null) rooms.add(r);
            }
            System.out.println("Loaded " + rooms.size() + " rooms.");
        } catch (IOException e) {
            System.out.println("Failed to read rooms file: " + e.getMessage());
            createDefaultRooms();
        }
    }

    private void createDefaultRooms() {
        rooms.clear();
        rooms.add(new Room("R101", "Standard", 1500));
        rooms.add(new Room("R102", "Standard", 1500));
        rooms.add(new Room("R103", "Standard", 1500));
        rooms.add(new Room("R201", "Deluxe", 2500));
        rooms.add(new Room("R202", "Deluxe", 2500));
        rooms.add(new Room("R203", "Deluxe", 2600));
        rooms.add(new Room("R301", "Suite", 4000));
        rooms.add(new Room("R302", "Suite", 4200));
        rooms.add(new Room("R303", "Suite", 4500));
    }

    private void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) {
                pw.println(r.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Failed to save rooms: " + e.getMessage());
        }
    }

    private void loadBookings() {
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) {
            System.out.println("No bookings file found (starting fresh).");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Booking b = Booking.fromCSV(line);
                if (b != null) bookings.add(b);
            }
            System.out.println("Loaded " + bookings.size() + " bookings.");
        } catch (IOException e) {
            System.out.println("Failed to read bookings file: " + e.getMessage());
        }
    }

    private void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) {
                pw.println(b.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Failed to save bookings: " + e.getMessage());
        }
    }
    
    private void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": listAllRooms(); break;
                case "2": searchAndBook(); break;
                case "3": cancelBooking(); break;
                case "4": viewBookingDetails(); break;
                case "5": listAllBookings(); break;
                case "6": saveBookings(); System.out.println("Data saved. Exiting."); return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== HOTEL BOOKING SYSTEM =====");
        System.out.println("1) List all rooms");
        System.out.println("2) Search rooms and make reservation");
        System.out.println("3) Cancel a reservation");
        System.out.println("4) View booking details");
        System.out.println("5) List all bookings");
        System.out.println("6) Save & Exit");
        System.out.print("Choose option: ");
    }

    private void listAllRooms() {
        System.out.println("\nRooms:");
        for (Room r : rooms) {
            System.out.println(" - " + r);
        }
    }
    
    private void searchAndBook() {
        System.out.print("Enter category to search (Standard/Deluxe/Suite or ALL): ");
        String category = scanner.nextLine().trim();
        if (category.equalsIgnoreCase("ALL") || category.isEmpty()) category = "ALL";

        LocalDate start = readDate("Enter check-in date (yyyy-MM-dd): ");
        LocalDate end = readDate("Enter check-out date (yyyy-MM-dd): ");
        if (end.isBefore(start) || end.equals(start)) {
            System.out.println("Invalid dates. Check-out must be after check-in.");
            return;
        }

        List<Room> available = findAvailableRooms(category, start, end.minusDays(1)); 
        if (available.isEmpty()) {
            System.out.println("No rooms available for given criteria.");
            return;
        }

        System.out.println("\nAvailable rooms:");
        for (int i = 0; i < available.size(); i++) {
            Room r = available.get(i);
            System.out.println((i+1) + ") " + r + "  (Total for stay: ₹" + calculateTotalPrice(r, start, end) + ")");
        }

        System.out.print("Choose room number to book (or 0 to cancel): ");
        int idx = readInt();
        if (idx <= 0 || idx > available.size()) {
            System.out.println("Booking cancelled by user.");
            return;
        }
        Room chosen = available.get(idx - 1);

        System.out.print("Guest name: ");
        String gname = scanner.nextLine().trim();
        System.out.print("Guest phone: ");
        String gphone = scanner.nextLine().trim();

        double total = calculateTotalPrice(chosen, start, end);
        Booking b = Booking.createNew(chosen.getId(), gname, gphone, start, end.minusDays(1), total);
        System.out.println("\nBooking summary:");
        System.out.println(b);
        System.out.print("\nProceed to payment of ₹" + total + "? (yes/no): ");
        String pay = scanner.nextLine().trim();
        if (!pay.equalsIgnoreCase("yes")) {
            System.out.println("Payment cancelled. Booking not completed.");
            return;
        }

        boolean paid = PaymentSimulator.processPayment(total);
        if (paid) {
            b.setStatus("BOOKED");
            bookings.add(b);
            saveBookings();
            System.out.println("Payment successful. Booking confirmed! Your Booking ID: " + b.getBookingId());
        } else {
            b.setStatus("PAYMENT_FAILED");
            bookings.add(b);
            saveBookings();
            System.out.println("Payment failed. Booking created with status PAYMENT_FAILED. Try again later.");
        }
    }

    private List<Room> findAvailableRooms(String category, LocalDate startInclusive, LocalDate endInclusive) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (!category.equalsIgnoreCase("ALL") && !r.getCategory().equalsIgnoreCase(category)) continue;
            boolean ok = true;
            for (Booking b : bookings) {
                if (!b.getRoomId().equals(r.getId())) continue;
                if (b.getStatus().equalsIgnoreCase("CANCELLED") || b.getStatus().equalsIgnoreCase("PAYMENT_FAILED"))
                    continue;
                LocalDate bStart = b.getStartDate();
                LocalDate bEnd = b.getEndDate();
                if (!(endInclusive.isBefore(bStart) || startInclusive.isAfter(bEnd))) {
                    ok = false;
                    break;
                }
            }
            if (ok) result.add(r);
        }
        return result;
    }

    private double calculateTotalPrice(Room r, LocalDate start, LocalDate endExclusive) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(start, endExclusive);
        if (nights <= 0) nights = 1;
        return nights * r.getPricePerNight();
    }

    private void cancelBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        String id = scanner.nextLine().trim();
        Booking found = null;
        for (Booking b : bookings) {
            if (b.getBookingId().equals(id)) {
                found = b;
                break;
            }
        }
        if (found == null) {
            System.out.println("Booking not found.");
            return;
        }
        System.out.println("Booking found:\n" + found);
        if (found.getStatus().equalsIgnoreCase("CANCELLED")) {
            System.out.println("Booking is already cancelled.");
            return;
        }
        System.out.print("Confirm cancellation? (yes/no): ");
        String c = scanner.nextLine().trim();
        if (!c.equalsIgnoreCase("yes")) {
            System.out.println("Cancellation aborted.");
            return;
        }
        found.setStatus("CANCELLED");
        saveBookings();
        System.out.println("Booking cancelled successfully.");
    }

    private void viewBookingDetails() {
        System.out.print("Search by (1) Booking ID or (2) Guest name: ");
        String opt = scanner.nextLine().trim();
        if (opt.equals("1")) {
            System.out.print("Enter Booking ID: ");
            String id = scanner.nextLine().trim();
            Booking b = findBookingById(id);
            if (b == null) System.out.println("Not found.");
            else System.out.println("\n" + b);
        } else {
            System.out.print("Enter guest name (partial allowed): ");
            String name = scanner.nextLine().trim().toLowerCase();
            List<Booking> found = new ArrayList<>();
            for (Booking b : bookings) {
                if (b.getGuestName().toLowerCase().contains(name)) found.add(b);
            }
            if (found.isEmpty()) System.out.println("No bookings found for that name.");
            else {
                System.out.println("Results:");
                for (Booking b : found) {
                    System.out.println("--------------------");
                    System.out.println(b);
                }
            }
        }
    }

    private Booking findBookingById(String id) {
        for (Booking b : bookings) if (b.getBookingId().equals(id)) return b;
        return null;
    }

    private void listAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("\nAll bookings:");
        for (Booking b : bookings) {
            System.out.println("--------------------");
            System.out.println(b);
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return LocalDate.parse(s, fmt);
            } catch (Exception e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd (example: 2025-12-01).");
            }
        }
    }

    private int readInt() {
        while (true) {
            try {
                String s = scanner.nextLine().trim();
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid integer: ");
            }
        }
    }
}
