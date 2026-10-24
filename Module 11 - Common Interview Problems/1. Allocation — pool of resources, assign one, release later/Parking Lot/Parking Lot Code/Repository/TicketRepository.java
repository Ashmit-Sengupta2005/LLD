package Repository;

import java.util.*;

import Domain.Ticket;

public class TicketRepository {
    private final Map<UUID, Ticket> TicketMap = new HashMap<>();

    public void saveTicket(Ticket ticket) {
        TicketMap.put(ticket.getTicketId(), ticket);
    }

    public Ticket viewTicketByID(UUID TicketID) {
        return TicketMap.get(TicketID); // null if absent
    }

    public void removeTicket(UUID TicketID) {
        if (TicketMap.containsKey(TicketID))
            TicketMap.remove(TicketID);
    }
}