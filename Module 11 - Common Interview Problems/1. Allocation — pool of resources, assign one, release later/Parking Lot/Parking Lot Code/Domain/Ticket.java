package Domain;

import java.time.Instant;
import java.util.*;
import Enum.TicketStatus;

public class Ticket {
    private final UUID ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot parkingSlot;
    private final PricingRule pricingRule;
    private final Instant entryTime;
    private TicketStatus ticketStatus;      // the only mutable field

    public Ticket(Vehicle vehicle, ParkingSlot parkingSlot, PricingRule pricingRule, Instant entryTime) {
        this.ticketId = UUID.randomUUID();
        this.vehicle = vehicle;
        this.parkingSlot = parkingSlot;
        this.pricingRule = pricingRule;
        this.entryTime = entryTime;
        this.ticketStatus = TicketStatus.ACTIVE;    // a new ticket is always active
    }

    // getters
    public UUID getTicketId() {
        return this.ticketId;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public ParkingSlot getParkingSlot() {
        return this.parkingSlot;
    }

    public PricingRule getPricingRule() {
        return this.pricingRule;
    }

    public Instant getEntryTime() {
        return this.entryTime;
    }

    public TicketStatus getTicketStatus() {
        return this.ticketStatus;
    }

    // behaviour instead of a setter - the ticket owns its own rule
    public boolean isActive() {
        return this.ticketStatus == TicketStatus.ACTIVE;
    }

    public void deactivate() {
        if (!isActive()) {
            throw new IllegalStateException("Ticket " + ticketId + " is already inactive");
        }
        this.ticketStatus = TicketStatus.INACTIVE;
    }

    @Override
    public String toString() {
        return "Ticket " + ticketId + " | " + vehicle + " | slot " + parkingSlot
             + " | in: " + entryTime + " | " + ticketStatus;
    }
}
