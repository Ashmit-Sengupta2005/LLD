/*
 * CHAIN OF RESPONSIBILITY PATTERN
 *
 * Line handlers up. Each one either handles the request or passes it to the
 * next - so the sender never knows who will deal with it, or how many are there.
 *
 * Here: the support desk of an e-commerce platform routing tickets.
 *
 *     ticket -> VIP -> General -> Billing -> Technical -> Delivery -> Fallback
 *
 * The naive way is one method holding every team's claim:
 *     public void handleRequest(String type) {
 *         if (type.equals("general"))        ...
 *         else if (type.equals("refund"))    ...
 *         else if (type.equals("technical")) ...
 *         else                               System.out.println("No handler available");
 *     }
 * A new team means editing that method, the order is welded into the source,
 * and one class speaks on behalf of every team.
 *
 * Chain of Responsibility: each team becomes a handler that knows ONLY its own
 * rule plus a pointer to the next handler.
 *
 * Note the two things that make this a real chain and not a lookup table:
 *   - VipHandler matches on a PREDICATE (is this customer a VIP?), not on a
 *     type key - a Map<String, Handler> could not express that rule.
 *   - ORDER is load-bearing. Part 3 of main() moves VIP to the back and the
 *     exact same ticket comes out somewhere else.
 * If every handler just matched a type string, you should use a map instead.
 *
 * Compile & run:  javac ChainOfResponsibility.java  &&  java Main
 *
 * (Nothing here is public: the file is ChainOfResponsibility.java, so a public
 *  type would have to carry that name. Single-file demo, package-private is fine.)
 */

// ========== THE REQUEST ==========
// Travels down the chain. Handlers read it; none of them own it.

class Ticket {

    private final String id;
    private final String type;
    private final String customer;
    private final boolean vip;

    public Ticket(String id, String type, String customer, boolean vip) {
        this.id = id;
        this.type = type;
        this.customer = customer;
        this.vip = vip;
    }

    public String type() {
        return type;
    }

    public String customer() {
        return customer;
    }

    public boolean isVip() {
        return vip;
    }

    @Override
    public String toString() {
        return id + " (" + type + (vip ? ", VIP" : "") + ") from " + customer;
    }
}

// ========== HANDLER ==========
// Holds the link to the next handler AND the walking logic - written once here,
// instead of copy-pasted into every concrete handler.
//
// handle() is final: it is the fixed algorithm. canHandle()/process() are the
// holes subclasses fill. That is Template Method living inside this pattern.

abstract class SupportHandler {

    private SupportHandler next;

    // Returns the handler just linked, so chains read as one line:
    //     vip.setNext(general).setNext(billing).setNext(technical);
    public SupportHandler setNext(SupportHandler next) {
        this.next = next;
        return next;
    }

    public final void handle(Ticket ticket) {
        if (canHandle(ticket)) {
            process(ticket); // mine - the chain stops here
        } else if (next != null) {
            next.handle(ticket); // not mine - pass it along
        } else {
            // Falling off the end. A terminal handler (below) is the better fix.
            System.out.println("   !! Nobody handled " + ticket);
        }
    }

    protected abstract boolean canHandle(Ticket ticket); // my rule

    protected abstract void process(Ticket ticket); // my work
}

// ========== CONCRETE HANDLERS ==========
// Each one is pure business logic: a rule and a job. No plumbing at all.

// A PREDICATE handler - it claims any ticket from a VIP, whatever its type.
// This is the rule a Map<String, Handler> cannot express, and the reason this
// has to sit at the FRONT of the chain to mean anything.
class VipHandler extends SupportHandler {

    @Override
    protected boolean canHandle(Ticket t) {
        return t.isVip();
    }

    @Override
    protected void process(Ticket t) {
        System.out.println("   VipHandler: priority queue, dedicated agent for " + t.customer());
    }
}

class GeneralSupport extends SupportHandler {

    @Override
    protected boolean canHandle(Ticket t) {
        return t.type().equalsIgnoreCase("general");
    }

    @Override
    protected void process(Ticket t) {
        System.out.println("   GeneralSupport: answering a general query");
    }
}

class BillingSupport extends SupportHandler {

    @Override
    protected boolean canHandle(Ticket t) {
        return t.type().equalsIgnoreCase("refund");
    }

    @Override
    protected void process(Ticket t) {
        System.out.println("   BillingSupport: processing the refund");
    }
}

class TechnicalSupport extends SupportHandler {

    @Override
    protected boolean canHandle(Ticket t) {
        return t.type().equalsIgnoreCase("technical");
    }

    @Override
    protected void process(Ticket t) {
        System.out.println("   TechnicalSupport: debugging the app issue");
    }
}

class DeliverySupport extends SupportHandler {

    @Override
    protected boolean canHandle(Ticket t) {
        return t.type().equalsIgnoreCase("delivery");
    }

    @Override
    protected void process(Ticket t) {
        System.out.println("   DeliverySupport: tracking the shipment");
    }
}

// ========== TERMINAL HANDLER ==========
// Claims everything, so a request can never fall off the end unnoticed.
// Always wired last.

class FallbackHandler extends SupportHandler {

    @Override
    protected boolean canHandle(Ticket t) {
        return true; // the catch-all
    }

    @Override
    protected void process(Ticket t) {
        System.out.println("   FallbackHandler: no team owns this - escalating to a human");
    }
}

// ========== CLIENT ==========
// Builds the chain and hands every ticket to the HEAD only. It never learns
// which handler did the work.

class Main {

    public static void main(String[] args) {

        // ----- 1. Build the chain -----
        SupportHandler desk = new VipHandler();
        desk.setNext(new GeneralSupport())
                .setNext(new BillingSupport())
                .setNext(new TechnicalSupport())
                .setNext(new DeliverySupport())
                .setNext(new FallbackHandler()); // terminal - claims anything left

        System.out.println("Chain: VIP -> General -> Billing -> Technical -> Delivery -> Fallback\n");

        // ----- 2. Send tickets to the FRONT and let the chain sort it out -----
        send(desk, new Ticket("T-1", "refund", "Asha", false)); // walks to Billing
        send(desk, new Ticket("T-2", "technical", "Rahul", false)); // walks to Technical
        send(desk, new Ticket("T-3", "delivery", "Meera", false)); // walks the whole chain

        // The predicate handler wins before any type handler is even asked
        send(desk, new Ticket("T-4", "refund", "Nikhil", true)); // VIP, NOT Billing

        // Nothing matches - the terminal handler catches it instead of silence
        send(desk, new Ticket("T-5", "crypto-payment", "Dev", false));

        // ----- 3. Order is load-bearing: same ticket, chain rebuilt -----
        System.out.println("\n[rebuilt with VIP moved to the BACK]");
        SupportHandler reordered = new GeneralSupport();
        reordered.setNext(new BillingSupport())
                .setNext(new TechnicalSupport())
                .setNext(new DeliverySupport())
                .setNext(new VipHandler()) // now it never gets a chance
                .setNext(new FallbackHandler());

        send(reordered, new Ticket("T-4", "refund", "Nikhil", true)); // Billing, not VIP

        System.out.println("\nSame ticket, different handler - wiring the chain IS the logic.");
        System.out.println("The client called one object and never learned who answered.");
    }

    private static void send(SupportHandler chain, Ticket ticket) {
        System.out.println("-> " + ticket);
        chain.handle(ticket);
    }
}
