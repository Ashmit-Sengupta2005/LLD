package Domain;

import java.util.*;
import Enum.SlotType;

public class ParkingSlot {
    private final UUID ParkingSlotID;
    private final SlotType slotType;
    private final int floorId;
    private final String label;          // readable id for output, e.g. "F1-C12"
    private boolean isOccupied;

    public ParkingSlot(SlotType slotType, int floorId, String label) {
        this.ParkingSlotID = UUID.randomUUID();
        this.slotType = slotType;
        this.floorId = floorId;
        this.label = label;
        this.isOccupied = false;         // a new slot is always free
    }

    // getters
    public UUID getParkingSlotID() {
        return this.ParkingSlotID;
    }

    public SlotType getSlotType() {
        return this.slotType;
    }

    public int getFloorId() {
        return this.floorId;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isOccupied() {
        return this.isOccupied;
    }

    // behaviour - no setters, the slot enforces its own rules

    /** Free, and the right size for this vehicle. */
    public boolean canFit(Vehicle vehicle) {
        return !isOccupied && this.slotType.name().equals(vehicle.getVehicleType().name());
    }

    public void occupy() {
        if (isOccupied) {
            throw new IllegalStateException("Slot " + label + " is already occupied");
        }
        this.isOccupied = true;
    }

    public void release() {
        this.isOccupied = false;
    }

    @Override
    public String toString() {
        return label + " [" + slotType + (isOccupied ? ", occupied]" : ", free]");
    }
}
