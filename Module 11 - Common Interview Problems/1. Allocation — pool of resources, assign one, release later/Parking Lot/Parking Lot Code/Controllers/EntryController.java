package Controllers;

import DTO.EntryResult;
import Domain.Vehicle;
import Enum.RuleType;
import Enum.VehicleType;
import Service.ParkingLotService;

public class EntryController {
    private final ParkingLotService lot;

    public EntryController(ParkingLotService lot) {
        this.lot = lot;
    } // injected

    public EntryResult park(String plate, String vehicleType, String ruleType) {
        Vehicle vehicle = new Vehicle(plate, VehicleType.valueOf(vehicleType)); // convert
        return lot.EnterParkingLot(vehicle, RuleType.valueOf(ruleType)); // one call
    }
}