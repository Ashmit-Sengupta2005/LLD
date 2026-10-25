package Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Adapter.PaymentGateway;
import DTO.EntryResult;
import DTO.ExitResult;
import Domain.Floor;
import Domain.ParkingSlot;
import Domain.PricingRule;
import Domain.Receipt;
import Domain.Ticket;
import Domain.Vehicle;
import Enum.Gateway;
import Enum.PaymentStatus;
import Enum.RuleType;
import Factory.PaymentFactory;
import Factory.PricingStrategyFactory;
import Repository.PricingRuleRepository;
import Repository.ReceiptRepository;
import Repository.TicketRepository;
import Strategy.PricingStrategy;
import Strategy.SeatAllocationStrategy;

public class ParkingLotService {
    private static class Holder {
        private static final ParkingLotService parkingLotServiceObject = new ParkingLotService();
    }

    private ParkingLotService() {
    }

    public static ParkingLotService getInstance() {
        return Holder.parkingLotServiceObject;
    }

    // Attributes of Parking Lot
    private final List<Floor> floors = new ArrayList<>();
    private final TicketRepository ticketRepo = new TicketRepository();
    private final PricingRuleRepository pricingRuleRepo = new PricingRuleRepository();
    private final ReceiptRepository receiptRepo = new ReceiptRepository();
    private SeatAllocationStrategy currentSeatAllocationStrategy;

    // ---- setup, called from Main / AdminController ----
    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    public void setAllocationStrategy(SeatAllocationStrategy strategy) {
        this.currentSeatAllocationStrategy = strategy;
    }

    public void addPricingRule(PricingRule rule) {
        pricingRuleRepo.savePricingRule(rule);
    }

    public List<Floor> getFloors() {
        return floors;
    }

    private Ticket generateTicket(Vehicle vehicle, ParkingSlot parkingSlot, PricingRule rule) {
        Ticket newTicket = new Ticket(vehicle, parkingSlot, rule, Instant.now());
        return newTicket;
    }

    private void saveTicket(Ticket ticket) {
        ticketRepo.saveTicket(ticket);
    }

    public void removeTicket(Ticket ticket) {
        ticketRepo.removeTicket(ticket.getTicketId());
    }

    public Receipt generateReceipt(Ticket ticket, Instant exitTime, long amountInPaise,
            PaymentStatus paymentStatus, Gateway paymentGateway) {
        Receipt receipt = new Receipt(ticket, exitTime, amountInPaise, paymentStatus, paymentGateway);
        return receipt;
    }

    public void saveReceipt(Receipt receipt) {
        receiptRepo.saveReceipt(receipt);

    }

    public EntryResult EnterParkingLot(Vehicle vehicle, RuleType ruleType) {
        /*
         * Basically when vehicle enters we will use a parking slot strategy to
         * find a parking slot then generate the ticket (basically a function call from
         * here or else the it will be impossible to generateTicket() as we don’t store
         * the parkingSlot
         * once generated even though it violates the SRP principle and after getting
         * the ticket we
         * return the Entry Result)
         */
        Optional<ParkingSlot> allottedParkingSlot = currentSeatAllocationStrategy.findOptimalSlot(vehicle, floors);
        if (allottedParkingSlot.isEmpty()) {
            return EntryResult.failure("Parking Slot Booking Failed");
        }
        PricingRule selectedPricingRule = pricingRuleRepo.viewPricingRule(vehicle.getVehicleType(), ruleType);
        if (selectedPricingRule == null) {
            return EntryResult.failure("No such Pricing Rule exists");
        }
        allottedParkingSlot.get().occupy();
        Ticket newTicket = generateTicket(vehicle, allottedParkingSlot.get(), selectedPricingRule);
        saveTicket(newTicket);
        return EntryResult.success(newTicket);

    }

    public ExitResult ExitParkingSlot(Ticket ticket, Instant exitTime, Gateway gateway) {
        if (ticket == null || !ticket.isActive())
            return ExitResult.failure("Invalid ticket");
        PricingRule pricingRule = ticket.getPricingRule();
        PricingStrategy currentPricingStrategy = PricingStrategyFactory.getInstance(pricingRule);
        long calculateAmountInPaise = currentPricingStrategy.calculateAmountInPaise(ticket, exitTime);
        PaymentGateway PaymentGateway = PaymentFactory.getInstance(gateway);
        boolean isPaymentSuccessful = PaymentGateway.pay(ticket, calculateAmountInPaise);
        if (!isPaymentSuccessful)
            return ExitResult.failure("Payment Failed");
        ParkingSlot allottedParkingSlot = ticket.getParkingSlot();
        allottedParkingSlot.release();
        ticket.deactivate();
        removeTicket(ticket);
        Receipt receipt = generateReceipt(ticket, exitTime, calculateAmountInPaise, PaymentStatus.SUCCESS, gateway);
        saveReceipt(receipt);
        return ExitResult.success(receipt);
    }
}
