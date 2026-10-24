package Strategy;

import java.util.Optional;
import java.util.List;
import Domain.Floor;
import Domain.ParkingSlot;
import Domain.Vehicle;

public interface SeatAllocationStrategy {
    Optional<ParkingSlot> findOptimalSlot(Vehicle vehicle, List<Floor> floors);
}
