// ========== Target Interface ==========
// Standard interface expected by the client (CheckoutService)
interface PaymentGateway {
    void pay(String orderId, double amount);
}

// ========== Compatible Implementation ==========
// PayU already conforms to the PaymentGateway interface
class PayUGateway implements PaymentGateway {
    @Override
    public void pay(String orderId, double amount) {
        System.out.println("Paid Rs." + amount + " using PayU for order: " + orderId);
    }
}

// ========== Adaptee ==========
// Existing class with an INCOMPATIBLE interface
// We cannot modify this — it's a third-party API
class RazorpayAPI {
    public void makePayment(String invoiceId, double amountInRupees) {
        System.out.println("Paid Rs." + amountInRupees + " using Razorpay for invoice: " + invoiceId);
    }
}

// ========== Adapter ==========
// Bridges the gap: implements Target, wraps and delegates to Adaptee
class RazorpayAdapter implements PaymentGateway {
    private RazorpayAPI razorpayAPI;

    public RazorpayAdapter() {
        this.razorpayAPI = new RazorpayAPI();
    }

    // Translates pay() → makePayment()
    @Override
    public void pay(String orderId, double amount) {
        razorpayAPI.makePayment(orderId, amount);
    }
}

// ========== Client ==========
// Uses only the PaymentGateway interface — fully decoupled
class CheckoutService {
    private PaymentGateway paymentGateway;

    // Constructor injection for dependency inversion
    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void checkout(String orderId, double amount) {
        paymentGateway.pay(orderId, amount);
    }
}

// ========== Main Method ==========
class Main {
    public static void main(String[] args) {
        // PayU works directly — it implements PaymentGateway
        System.out.println("=== Using PayU (direct implementation) ===");
        CheckoutService payuCheckout = new CheckoutService(new PayUGateway());
        payuCheckout.checkout("ORD-101", 1780);

        System.out.println();

        // Razorpay has an incompatible interface — Adapter makes it work
        System.out.println("=== Using Razorpay (via Adapter) ===");
        CheckoutService razorpayCheckout = new CheckoutService(new RazorpayAdapter());
        razorpayCheckout.checkout("ORD-102", 2499);
    }
}
