package Factory;

import Adapter.*;
import Enum.Gateway;

public class PaymentFactory {
    public static PaymentGateway getInstance(Gateway gateway) {
        switch (gateway) {
            case RAZORPAY:
                return new RazorpayGateway();
            case STRIPE:
                return new StripeGateway();
            case PAYPAL:
                return new PaypalGateway();
            default:
                throw new IllegalArgumentException("No adapter for gateway: " + gateway);
        }
    }
}
