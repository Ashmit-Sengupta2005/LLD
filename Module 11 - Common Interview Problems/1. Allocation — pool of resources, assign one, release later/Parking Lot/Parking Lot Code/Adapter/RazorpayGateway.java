package Adapter;

import Domain.Ticket;
import External.RazorpayClient;

public class RazorpayGateway implements PaymentGateway {
    private final RazorpayClient client = new RazorpayClient();

    @Override
    public boolean pay(Ticket ticket, long amountInPaise) {
        // translate OUR call into THEIR API: different name, rupees instead of paise
        String orderId = ticket.getTicketId().toString();
        int amountInRupees = (int) (amountInPaise / 100);
        return client.createPayment(orderId, amountInRupees);
    }
}
