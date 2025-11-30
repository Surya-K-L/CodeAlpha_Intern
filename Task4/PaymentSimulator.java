import java.util.Random;
public class PaymentSimulator {
    private static final Random rnd = new Random();
    public static boolean processPayment(double amount) {
        double probability = rnd.nextDouble();
        return probability < 0.85;
    }
}
