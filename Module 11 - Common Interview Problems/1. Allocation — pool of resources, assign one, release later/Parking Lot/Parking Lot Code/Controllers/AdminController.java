package Controllers;

import java.util.List;

import Domain.Floor;
import Domain.PricingRule;
import Enum.SlotType;
import Enum.VehicleType;
import Service.ParkingLotService;
import Strategy.SeatAllocationStrategy;

public class AdminController {
    private final ParkingLotService lot;

    public AdminController(ParkingLotService lot) {
        this.lot = lot;
    } // injected

    public void addFloor(int floorNo) {
        lot.addFloor(new Floor(floorNo));
    }

    public void addSlot(int floorNo, SlotType slotType, String label) {
        lot.addSlotToFloor(floorNo, slotType, label);
    }

    public void addSlots(int floorNo, SlotType slotType, int count) {
        for (int i = 1; i <= count; i++) {
            lot.addSlotToFloor(floorNo, slotType, "F" + floorNo + "-" + slotType + "-" + i);
        }
    }

    public void setAllocationStrategy(SeatAllocationStrategy strategy) {
        lot.setAllocationStrategy(strategy);
    }

    public void setHourlyRate(VehicleType vehicleType, long ratePerHourInPaise) {
        lot.addPricingRule(PricingRule.hourly(vehicleType, ratePerHourInPaise));
    }

    public void setFlatRate(VehicleType vehicleType, long flatRateInPaise) {
        lot.addPricingRule(PricingRule.flat(vehicleType, flatRateInPaise));
    }

    public void printOccupancy() {
        for (Floor floor : lot.getFloors()) {
            System.out.println(floor);      // Floor.toString() prints "Floor 1 (3/5 free)"
        }
    }
}
