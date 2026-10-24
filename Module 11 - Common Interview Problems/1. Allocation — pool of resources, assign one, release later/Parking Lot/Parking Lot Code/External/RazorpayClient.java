package External;

/** Pretend third-party SDK - NOT our code. Note the method name and arguments. */
public class RazorpayClient {
    public boolean createPayment(String orderId, int amountInRupees) {
        System.out.println("[Razorpay SDK] createPayment(" + orderId + ", Rs." + amountInRupees + ")");
        return true;
    }
}
