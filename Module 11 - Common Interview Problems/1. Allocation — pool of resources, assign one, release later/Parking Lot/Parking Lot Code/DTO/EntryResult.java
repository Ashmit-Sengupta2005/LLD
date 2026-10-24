package DTO;

import Domain.Ticket;

public class EntryResult {
    private boolean success;
    private Ticket ticket;
    private String message;

    public EntryResult(boolean success, Ticket ticket, String message) {
        this.success = success;
        this.ticket = ticket;
        this.message = message;
    }

    public static EntryResult success(Ticket ticket) {
        return new EntryResult(true, ticket, "Slot booked You may park there.");
    }

    public static EntryResult failure(String reason) {
        return new EntryResult(false, null, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public String getMessage() {
        return message;
    }
}
