package Domain;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import Enum.PaymentStatus;
import Enum.PaymentGateway;

public class Receipt {
    private final UUID receiptId;
    private final Ticket ticket;
    private final Instant exitTime;
    private final long amountInPaise;
    private final PaymentStatus paymentStatus;
    private final PaymentGateway paymentGateway;

    public Receipt(Ticket ticket, Instant exitTime, long amountInPaise,
                   PaymentStatus paymentStatus, PaymentGateway paymentGateway) {
        this.receiptId = UUID.randomUUID();
        this.ticket = ticket;
        this.exitTime = exitTime;
        this.amountInPaise = amountInPaise;
        this.paymentStatus = paymentStatus;
        this.paymentGateway = paymentGateway;
    }

    // getters
    public UUID getReceiptId() {
        return this.receiptId;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public Instant getEntryTime() {
        return this.ticket.getEntryTime();      // no need to store it twice
    }

    public Instant getExitTime() {
        return this.exitTime;
    }

    public long getAmountInPaise() {
        return this.amountInPaise;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public PaymentGateway getPaymentGateway() {
        return this.paymentGateway;
    }

    // derived values - computed, not stored
    public Duration getParkedFor() {
        return Duration.between(getEntryTime(), exitTime);
    }

    public double getAmountInRupees() {
        return this.amountInPaise / 100.0;      // only for display
    }

    // a receipt is a permanent record - no setters

    @Override
    public String toString() {
        return "Receipt " + receiptId
             + " | " + ticket.getVehicle()
             + " | parked " + getParkedFor().toHours() + "h " + (getParkedFor().toMinutesPart()) + "m"
             + " | Rs. " + getAmountInRupees()
             + " | " + paymentStatus + " via " + paymentGateway;
    }
}
