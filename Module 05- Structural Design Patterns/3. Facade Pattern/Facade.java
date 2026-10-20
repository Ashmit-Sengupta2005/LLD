/*
 * FACADE PATTERN
 *
 * Problem: Booking a movie ticket is not ONE action, it is a WORKFLOW of 5 steps
 * across 5 different services - pay, reserve seat, generate ticket, add points,
 * send confirmation.
 *
 * If the client does this itself, then the client must know:
 *   1. WHICH services exist (all 5 of them)
 *   2. In WHAT ORDER to call them (pay before reserving, ticket only after reserve)
 *   3. The BUSINESS RULES in between (how many loyalty points for this amount?)
 *
 * That is a lot of knowledge for a client whose only job is "book me a ticket".
 * And every new client (web app, mobile app, admin panel) has to repeat it.
 *
 * Facade = one class that owns this workflow, and exposes ONE simple method.
 * The client calls bookMovieTicket(...) and knows nothing else.
 */

// ========== SUBSYSTEM ==========
// Each service does ONE job and knows nothing about the others or about the facade.
// They are completely independent and still usable on their own.

class PaymentService {
    public void makePayment(String accountId, double amount) {
        System.out.println("Payment of Rs." + amount + " successful for account " + accountId);
    }
}

class SeatReservationService {
    public void reserveSeat(String movieId, String seatNumber) {
        System.out.println("Seat " + seatNumber + " reserved for movie " + movieId);
    }
}

class TicketService {
    public void generateTicket(String movieId, String seatNumber) {
        System.out.println("Ticket generated for movie " + movieId + ", Seat: " + seatNumber);
    }
}

class LoyaltyPointsService {
    public void addPoints(String accountId, int points) {
        System.out.println(points + " loyalty points added to account " + accountId);
    }
}

class NotificationService {
    public void sendBookingConfirmation(String userEmail) {
        System.out.println("Booking confirmation sent to " + userEmail);
    }
}

// ========== FACADE ==========
// Holds references to every subsystem service and owns the ORDER + the RULES.

class MovieBookingFacade {
    private PaymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketService ticketService;
    private LoyaltyPointsService loyaltyPointsService;
    private NotificationService notificationService;

    public MovieBookingFacade() {
        this.paymentService = new PaymentService();
        this.seatReservationService = new SeatReservationService();
        this.ticketService = new TicketService();
        this.loyaltyPointsService = new LoyaltyPointsService();
        this.notificationService = new NotificationService();
    }

    // The ONE method the client needs. Everything below is hidden complexity.
    public void bookMovieTicket(String accountId, String movieId, String seatNumber,
            String userEmail, double amount) {

        paymentService.makePayment(accountId, amount);
        seatReservationService.reserveSeat(movieId, seatNumber);
        ticketService.generateTicket(movieId, seatNumber);

        // Workflow logic lives HERE, not in the client.
        // Rule: 10% of the ticket amount becomes loyalty points.
        // If this rule changes tomorrow, only this one line changes.
        int points = (int) (amount * 0.10);
        loyaltyPointsService.addPoints(accountId, points);

        notificationService.sendBookingConfirmation(userEmail);
        System.out.println("Movie ticket booking completed successfully!");
    }
}

// ========== CLIENT ==========

public class Facade {
    public static void main(String[] args) {

        // ----- WITHOUT facade (the bad way) -----
        // Client knows all 5 classes, their order, and the points rule.
        System.out.println("=== Without Facade (client does everything) ===");
        PaymentService paymentService = new PaymentService();
        paymentService.makePayment("user123", 500);
        SeatReservationService seatReservationService = new SeatReservationService();
        seatReservationService.reserveSeat("movie456", "A10");
        TicketService ticketService = new TicketService();
        ticketService.generateTicket("movie456", "A10");
        LoyaltyPointsService loyaltyPointsService = new LoyaltyPointsService();
        loyaltyPointsService.addPoints("user123", 50);
        NotificationService notificationService = new NotificationService();
        notificationService.sendBookingConfirmation("user@example.com");

        System.out.println();

        // ----- WITH facade (the good way) -----
        // Client knows ONE class and ONE method.
        System.out.println("=== With Facade (client knows one method) ===");
        MovieBookingFacade bookingFacade = new MovieBookingFacade();
        bookingFacade.bookMovieTicket("user123", "movie456", "A10", "user@example.com", 500);
    }
}
