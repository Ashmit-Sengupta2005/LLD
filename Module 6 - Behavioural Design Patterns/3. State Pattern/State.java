/*
 * STATE PATTERN
 *
 * Give every state its OWN class, and let the object delegate its behaviour
 * to whichever state it is currently in - so behaviour changes with state,
 * with no if and no switch.
 *
 * Here: the lifecycle of a food-delivery order.
 *
 *     ORDER_PLACED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
 *          |              |
 *      CANCELLED      CANCELLED            (and nowhere else)
 *
 * The naive way keeps the state in a String and branches on it, in EVERY method:
 *     public void cancel() {
 *         if (state.equals("ORDER_PLACED") || state.equals("PREPARING")) { ... }
 *         else System.out.println("Cannot cancel the order now.");
 *     }
 *     public void next()  { switch (state) { case "ORDER_PLACED": ... } }
 *     public void track() { switch (state) { ...the same five cases again... } }
 * Every new action repeats the list of states; every new state means editing
 * every existing method; and "PREPARNG" compiles fine.
 *
 * State: each stage becomes a class that answers ALL the actions, but only for
 * itself, and names the state that comes next.
 *
 * Result: OrderContext has no if, no switch and no String state - and the only
 * way to reach DeliveredState is through OutForDeliveryState.next().
 *
 * Compile & run:  javac State.java  &&  java Main
 *
 * (Nothing here is public: the file is State.java, so a public type would have
 *  to be called State. Single-file demo, so package-private is fine.)
 */

// ========== STATE ==========
// The contract: every action the context can receive.
// Each method takes the context, so a state can transition it and read its data.

interface OrderState {

    void next(OrderContext context);

    void cancel(OrderContext context);

    void track(OrderContext context); // differs per state WITHOUT transitioning

    String getStateName();
}

// ========== CONCRETE STATES ==========
// One class per stage. Each holds the whole row of the states x actions grid:
// what next() does here, what cancel() does here, what track() says here.
//
// Note they have NO FIELDS - all the data lives in the context. That means one
// instance could be shared by every order in the system (Flyweight):
//     static final PreparingState INSTANCE = new PreparingState();
// We use `new` below to match the notes.

class OrderPlacedState implements OrderState {

    @Override
    public void next(OrderContext ctx) {
        ctx.setState(new PreparingState()); // this state names its successor
        System.out.println("   Restaurant accepted it. Now being prepared.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        ctx.setState(new CancelledState());
        System.out.println("   Order cancelled. No charge.");
    }

    @Override
    public void track(OrderContext ctx) {
        System.out.println("   Waiting for the restaurant to accept order " + ctx.getOrderId());
    }

    @Override
    public String getStateName() {
        return "ORDER_PLACED";
    }
}

class PreparingState implements OrderState {

    @Override
    public void next(OrderContext ctx) {
        ctx.setState(new OutForDeliveryState());
        System.out.println("   Picked up by a rider. Out for delivery.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        ctx.setState(new CancelledState());
        System.out.println("   Order cancelled. Refund issued for the food.");
    }

    @Override
    public void track(OrderContext ctx) {
        System.out.println("   The kitchen is preparing order " + ctx.getOrderId());
    }

    @Override
    public String getStateName() {
        return "PREPARING";
    }
}

class OutForDeliveryState implements OrderState {

    @Override
    public void next(OrderContext ctx) {
        ctx.setState(new DeliveredState());
        System.out.println("   Handed over to the customer. Delivered.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        // The refusal lives HERE, not in a shared method someone could forget
        System.out.println("   Cannot cancel - the rider is already on the way.");
    }

    @Override
    public void track(OrderContext ctx) {
        System.out.println("   Live map: rider is 2 km from you with order " + ctx.getOrderId());
    }

    @Override
    public String getStateName() {
        return "OUT_FOR_DELIVERY";
    }
}

class DeliveredState implements OrderState {

    @Override
    public void next(OrderContext ctx) {
        System.out.println("   Already delivered - this is the end of the line.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("   Cannot cancel a delivered order. Raise a refund request instead.");
    }

    @Override
    public void track(OrderContext ctx) {
        System.out.println("   Order " + ctx.getOrderId() + " was delivered at 8:42 PM");
    }

    @Override
    public String getStateName() {
        return "DELIVERED";
    }
}

class CancelledState implements OrderState {

    @Override
    public void next(OrderContext ctx) {
        System.out.println("   A cancelled order cannot move forward.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("   Order is already cancelled.");
    }

    @Override
    public void track(OrderContext ctx) {
        System.out.println("   Nothing to track - order " + ctx.getOrderId() + " was cancelled.");
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}

// ========== CONTEXT ==========
// Holds the current state and the order's DATA, and forwards every call.
// No if, no switch, no String state - and it does not know the lifecycle order.

class OrderContext {

    private final String orderId; // data lives here, behaviour lives in the states
    private OrderState currentState; // ⭐ THE KEY LINE - an interface, never a String

    public OrderContext(String orderId) {
        this.orderId = orderId;
        this.currentState = new OrderPlacedState(); // every order starts here
    }

    // Called by the states themselves - this is how a transition happens
    public void setState(OrderState state) {
        this.currentState = state;
    }

    // Pure delegation. `this` is passed so the state can transition the context.
    public void next() {
        currentState.next(this);
    }

    public void cancel() {
        currentState.cancel(this);
    }

    public void track() {
        currentState.track(this);
    }

    public String getCurrentState() {
        return currentState.getStateName();
    }

    public String getOrderId() {
        return orderId;
    }
}

// ========== CLIENT ==========
// Calls next() / cancel() / track() on the context and never asks which state
// is active first. It never touches a state class at all.

class Main {

    public static void main(String[] args) {

        // ----- Order 1: the happy path, all the way to delivered -----
        System.out.println("Order A-101 - happy path");
        OrderContext order = new OrderContext("A-101");

        show(order);
        order.track();

        order.next(); // ORDER_PLACED -> PREPARING
        show(order);
        order.track(); // same call, different behaviour - no branching anywhere

        order.next(); // PREPARING -> OUT_FOR_DELIVERY
        show(order);
        order.track();

        // Illegal action: the current state simply refuses, and nothing changes
        System.out.println("\n   [customer taps Cancel]");
        order.cancel();
        show(order); // still OUT_FOR_DELIVERY

        order.next(); // OUT_FOR_DELIVERY -> DELIVERED
        show(order);
        order.track();

        // Dead end: DeliveredState has no successor to name
        System.out.println("\n   [app tries to advance it again]");
        order.next();
        order.cancel();
        show(order);

        // ----- Order 2: cancelled early -----
        System.out.println("\n\nOrder B-202 - cancelled while being prepared");
        OrderContext other = new OrderContext("B-202");

        other.next(); // ORDER_PLACED -> PREPARING
        show(other);

        other.cancel(); // PREPARING -> CANCELLED
        show(other);

        other.next(); // refused - a cancelled order is terminal
        other.cancel(); // refused - already cancelled
        other.track();
        show(other);

        System.out.println("\nThe only way to reach DELIVERED is through OUT_FOR_DELIVERY.next()");
        System.out.println("OrderContext itself contains no if and no switch.");
    }

    private static void show(OrderContext order) {
        System.out.println("   >> state: " + order.getCurrentState());
    }
}
