package External;

/** Pretend third-party SDK - NOT our code. Different method name, different units. */
public class StripeClient {
    public String charge(long amountInCents, String currency, String reference) {
        System.out.println("[Stripe SDK] charge(" + amountInCents + " " + currency + ", " + reference + ")");
        return "SUCCEEDED";                       // returns a status string, not a boolean
    }
}
