package Domain;

import java.util.*;
import Enum.VehicleType;

public class Vehicle {
    private final UUID id;
    private final String licensePlate;
    private final VehicleType vehicleType;

    public Vehicle(String licensePlate, VehicleType vehicleType) {
        this.id = UUID.randomUUID();
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
    }

    // getters
    public UUID getVehicleId() {
        return this.id;
    }

    public String getLicensePlate() {
        return this.licensePlate;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    // no setters - a vehicle's plate and type never change after creation

    @Override
    public String toString() {
        return licensePlate + " (" + vehicleType + ")";
    }
}
