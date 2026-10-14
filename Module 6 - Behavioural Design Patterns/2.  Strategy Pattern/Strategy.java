/*
 * STRATEGY PATTERN
 *
 * One job, several ways to do it - and which way is decided at RUNTIME.
 *
 * Here: Uber matches a rider to a driver. But "matching" means different things
 * in different situations - nearest driver normally, FIFO queue at an airport,
 * surge-zone priority during peak hours.
 *
 * The naive way is one method full of if-else:
 *     if (type.equals("NEAREST"))        { ... }
 *     else if (type.equals("AIRPORT"))   { ... }
 *     else if (type.equals("SURGE"))     { ... }
 * Every new rule means editing this class again.
 *
 * Strategy: pull each branch out into its own class behind a common interface.
 * The service holds ONE strategy object and just calls match() on it.
 *
 * Adding a rule = write one new class. The service is never touched.
 */

// ========== STRATEGY ==========
// The common contract every matching algorithm must satisfy.
interface MatchingStrategy {
    void match(String riderLocation);
}

// ========== CONCRETE STRATEGIES ==========
// Each one was an if-else branch. Now it's an independent, testable class.

class NearestDriverStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("   Nearest driver to " + riderLocation);
        // distance-based matching logic would go here
    }
}

class AirportQueueStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("   FIFO airport queue for " + riderLocation);
        // first driver in line gets the ride
    }
}

class SurgePriorityStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("   Surge-zone priority near " + riderLocation);
        // prioritise high-surge zones / premium drivers
    }
}

// ========== CONTEXT ==========
// Holds a strategy and delegates to it. Contains NO matching logic itself.
class RideMatchingService {

    // Interface type - the service has no idea which algorithm this is.
    private MatchingStrategy strategy;

    // Constructor injection: pick the starting strategy
    public RideMatchingService(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    // Setter injection: swap the algorithm at RUNTIME
    public void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public void matchRider(String location) {
        strategy.match(location); // delegate - no if-else anywhere
    }
}

// ========== CLIENT ==========

public class Strategy {
    public static void main(String[] args) {

        // Airport pickup - use the queue
        RideMatchingService airportService = new RideMatchingService(new AirportQueueStrategy());
        System.out.println("Rider at Terminal 1:");
        airportService.matchRider("Terminal 1");

        // City ride - start with nearest driver
        RideMatchingService cityService = new RideMatchingService(new NearestDriverStrategy());
        System.out.println("\nRider at Downtown (normal hours):");
        cityService.matchRider("Downtown");

        // Peak hour hits - SAME object, different behaviour, no new service created
        System.out.println("\n[surge pricing kicks in]");
        cityService.setStrategy(new SurgePriorityStrategy());
        System.out.println("Rider at Downtown (peak hours):");
        cityService.matchRider("Downtown");

        // MatchingStrategy has one abstract method, so it's a functional
        // interface - a simple strategy can just be a lambda.
        System.out.println("\n[experimental strategy, no class written]");
        cityService.setStrategy(loc -> System.out.println("   Random driver near " + loc));
        cityService.matchRider("Downtown");
    }
}
