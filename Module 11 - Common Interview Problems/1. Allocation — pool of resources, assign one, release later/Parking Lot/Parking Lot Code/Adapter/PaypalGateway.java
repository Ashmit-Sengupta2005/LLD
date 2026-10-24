package Adapter;

import Domain.Ticket;

public class PaypalGateway implements PaymentGateway {

    @Override
    public boolean pay(Ticket ticket, long amountInPaise) {
        System.out.println("[PayPal] paid Rs." + amountInPaise / 100.0
                           + " for ticket " + ticket.getTicketId());
        return true;
    }
}
