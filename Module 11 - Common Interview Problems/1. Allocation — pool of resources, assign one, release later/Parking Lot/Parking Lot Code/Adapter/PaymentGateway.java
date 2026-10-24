package Adapter;

import Domain.Ticket;

/**
 * Our contract. Every gateway, whatever its real API looks like,
 * is reduced to this one method.
 */
public interface PaymentGateway {
    boolean pay(Ticket ticket, long amountInPaise);
}
