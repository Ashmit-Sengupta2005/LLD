package Adapter;

import Domain.Ticket;
import External.StripeClient;

public class StripeGateway implements PaymentGateway {
    private final StripeClient client = new StripeClient();

    @Override
    public boolean pay(Ticket ticket, long amountInPaise) {
        // Stripe wants cents + currency, and answers with a String
        String status = client.charge(amountInPaise, "INR", ticket.getTicketId().toString());
        return "SUCCEEDED".equals(status);
    }
}
