import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Booking {
    private String bookingId;   
    private String roomId;
    private String guestName;
    private String guestPhone;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String status;      

    private static final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

    public Booking(String bookingId, String roomId, String guestName, String guestPhone,
                   LocalDate startDate, LocalDate endDate, double totalPrice, String status) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public static Booking createNew(String roomId, String guestName, String guestPhone,
                                    LocalDate startDate, LocalDate endDate, double totalPrice) {
        return new Booking(UUID.randomUUID().toString(), roomId, guestName, guestPhone,
                startDate, endDate, totalPrice, "PENDING_PAYMENT");
    }

    public String getBookingId() { return bookingId; }
    public String getRoomId() { return roomId; }
    public String getGuestName() { return guestName; }
    public String getGuestPhone() { return guestPhone; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    
    public String toString() {
        return "BookingID: " + bookingId + "\nRoom: " + roomId + "\nGuest: " + guestName +
               "\nPhone: " + guestPhone + "\nFrom: " + startDate + " To: " + endDate +
               "\nTotal: ₹" + totalPrice + "\nStatus: " + status;
    }

    public String toCSV() {
        return String.join(",",
                escape(bookingId),
                escape(roomId),
                escape(guestName),
                escape(guestPhone),
                startDate.format(fmt),
                endDate.format(fmt),
                String.valueOf(totalPrice),
                escape(status)
        );
    }

    public static Booking fromCSV(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 8) return null;
        String bookingId = unescape(p[0]);
        String roomId = unescape(p[1]);
        String guestName = unescape(p[2]);
        String guestPhone = unescape(p[3]);
        LocalDate start = LocalDate.parse(p[4], fmt);
        LocalDate end = LocalDate.parse(p[5], fmt);
        double total = Double.parseDouble(p[6]);
        String status = unescape(p[7]);
        return new Booking(bookingId, roomId, guestName, guestPhone, start, end, total, status);
    }

    private static String escape(String s) {
        return s.replace(",", "&#44;");
    }
    private static String unescape(String s) {
        return s.replace("&#44;", ",");
    }
}
