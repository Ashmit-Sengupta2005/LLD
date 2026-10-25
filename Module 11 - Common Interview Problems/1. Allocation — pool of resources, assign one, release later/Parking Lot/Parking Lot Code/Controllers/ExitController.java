package Controllers;

import java.time.Instant;

import DTO.ExitResult;
import Domain.Ticket;
import Enum.Gateway;
import Service.ParkingLotService;

public class ExitController {
    private final ParkingLotService lot;

    public ExitController(ParkingLotService lot) {
        this.lot = lot;
    } // injected

    public ExitResult unpark(Ticket ticket, Gateway gateway) {
        return lot.ExitParkingSlot(ticket, Instant.now(), gateway);
    }
}
