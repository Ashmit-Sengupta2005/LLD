import java.time.Instant;
import java.time.temporal.ChronoUnit;

import Controllers.AdminController;
import Controllers.EntryController;
import Controllers.ExitController;
import DTO.EntryResult;
import DTO.ExitResult;
import Enum.Gateway;
import Enum.SlotType;
import Enum.VehicleType;
import Service.ParkingLotService;
import Strategy.NearestSeatAllocationStrategy;

public class Main {
    public static void main(String[] args) {

        // ---------- 1. wire everything up ----------
        ParkingLotService lot = ParkingLotService.getInstance();

        AdminController admin = new AdminController(lot);
        EntryController entry = new EntryController(lot);
        ExitController exit = new ExitController(lot);

        // ---------- 2. admin builds the lot ----------
        admin.setAllocationStrategy(new NearestSeatAllocationStrategy());

        admin.addFloor(1);
        admin.addSlots(1, SlotType.CAR, 2);
        admin.addSlots(1, SlotType.BIKE, 2);

        admin.addFloor(2);
        admin.addSlots(2, SlotType.CAR, 2);
        admin.addSlots(2, SlotType.TRUCK, 1);

        // ---------- 3. admin sets the rates ----------
        admin.setHourlyRate(VehicleType.CAR, 2000); // Rs 20/hr
        admin.setHourlyRate(VehicleType.BIKE, 1000); // Rs 10/hr
        admin.setFlatRate(VehicleType.TRUCK, 15000); // Rs 150 flat

        System.out.println("--- lot ready ---");
        admin.printOccupancy();

        // ---------- 4. a car parks ----------
        System.out.println("\n--- car enters ---");
        EntryResult carEntry = entry.park("DL-01-AB-1234", "CAR", "HOURLY");
        System.out.println(carEntry.getMessage());
        System.out.println(carEntry.getTicket());
        admin.printOccupancy();

        // ---------- 5. three hours later it leaves ----------
        System.out.println("\n--- car exits after 3 hours ---");
        Instant threeHoursLater = carEntry.getTicket().getEntryTime().plus(3, ChronoUnit.HOURS);
        ExitResult carExit = lot.ExitParkingSlot(carEntry.getTicket(), threeHoursLater, Gateway.RAZORPAY);
        System.out.println(carExit.getMessage());
        System.out.println(carExit.getReceipt());
        admin.printOccupancy();

        // ---------- 6. a truck parks on the flat rate ----------
        System.out.println("\n--- truck enters, flat rate ---");
        EntryResult truckEntry = entry.park("HR-55-XY-9999", "TRUCK", "FLAT");
        System.out.println(truckEntry.getMessage());

        System.out.println("\n--- truck exits after 9 hours (flat, so still Rs 150) ---");
        Instant nineHoursLater = truckEntry.getTicket().getEntryTime().plus(9, ChronoUnit.HOURS);
        ExitResult truckExit = lot.ExitParkingSlot(truckEntry.getTicket(), nineHoursLater, Gateway.STRIPE);
        System.out.println(truckExit.getReceipt());

        // ---------- 7. failure cases ----------
        System.out.println("\n--- same ticket presented twice ---");
        System.out.println(exit.unpark(truckEntry.getTicket(), Gateway.STRIPE).getMessage());

        System.out.println("\n--- no EV rate configured ---");
        System.out.println(entry.park("KA-05-EV-0001", "EV", "HOURLY").getMessage());
    }
}
