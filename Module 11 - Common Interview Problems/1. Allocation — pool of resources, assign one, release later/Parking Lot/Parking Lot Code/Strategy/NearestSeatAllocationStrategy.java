package Strategy;

import java.util.List;
import java.util.Optional;

import Domain.Floor;
import Domain.ParkingSlot;
import Domain.Vehicle;

public class NearestSeatAllocationStrategy implements SeatAllocationStrategy {
    // Assuming list is sorted as per floor no
    public Optional<ParkingSlot> findOptimalSlot(Vehicle vehicle, List<Floor> floors) {
        for (Floor floor : floors) { // floor 1 → last
            Optional<ParkingSlot> slot = floor.findFirstFreeSlot(vehicle);
            if (slot.isPresent())
                return slot;
        }
        return Optional.empty();
    }
}
