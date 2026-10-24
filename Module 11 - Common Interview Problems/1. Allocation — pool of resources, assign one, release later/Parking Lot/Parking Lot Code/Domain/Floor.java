package Domain;

import java.util.*;

public class Floor {
    private final UUID floorID;
    private final int floorNo;
    private final List<ParkingSlot> parkingSlots;

    public Floor(int floorNo) {
        this.floorID = UUID.randomUUID();
        this.floorNo = floorNo;
        this.parkingSlots = new ArrayList<>();
    }

    // getters
    public UUID getFloorID() {
        return this.floorID;
    }

    public int getFloorNo() {
        return this.floorNo;
    }

    public List<ParkingSlot> getParkingSlots() {
        return Collections.unmodifiableList(this.parkingSlots); // callers can read, not mutate
    }

    // behaviour
    public void addSlot(ParkingSlot slot) {
        this.parkingSlots.add(slot);
    }

    public int getTotalSlots() {
        return this.parkingSlots.size();
    }

    public long getFreeSlots() {
        long unoccupied = 0;
        for (ParkingSlot currentParkingSlot : parkingSlots) {
            if (currentParkingSlot.isOccupied() == false)
                unoccupied++;
        }
        return unoccupied;
    }

    public Optional<ParkingSlot> findFirstFreeSlot(Vehicle vehicle) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.canFit(vehicle)) {
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Floor " + floorNo + " (" + getFreeSlots() + "/" + getTotalSlots() + " free)";
    }
}
