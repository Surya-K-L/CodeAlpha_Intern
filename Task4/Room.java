import java.util.Objects;
import java.util.*;
public class Room {
    private String id;           
    private String category;     
    private double pricePerNight;

    public Room(String id, String category, double pricePerNight) {
        this.id = id;
        this.category = category;
        this.pricePerNight = pricePerNight;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public double getPricePerNight() { return pricePerNight; }

    public String toString() {
        return id + " | " + category + " | ₹" + pricePerNight + " / night";
    }

    public String toCSV() {
        return id + "," + category + "," + pricePerNight;
    }

    public static Room fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return null;
        String id = parts[0].trim();
        String category = parts[1].trim();
        double price = Double.parseDouble(parts[2].trim());
        return new Room(id, category, price);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }
}
