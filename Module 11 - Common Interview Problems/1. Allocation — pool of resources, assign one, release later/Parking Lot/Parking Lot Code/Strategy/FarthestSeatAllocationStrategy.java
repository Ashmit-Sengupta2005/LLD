package Strategy;

import java.util.List;
import java.util.Optional;

import Domain.Floor;
import Domain.ParkingSlot;
import Domain.Vehicle;

public class FarthestSeatAllocationStrategy implements SeatAllocationStrategy {
    public Optional<ParkingSlot> findOptimalSlot(Vehicle vehicle, List<Floor> floors) {
        // Assuming list is sorted as per floor no
        int sz = floors.size();
        for (int i = sz - 1; i >= 0; i--) {
            Floor currentFloor = floors.get(i);
            Optional<ParkingSlot> slot = currentFloor.findFirstFreeSlot(vehicle);
            if (slot.isPresent())
                return slot;
        }
        return Optional.empty();
    }
}
