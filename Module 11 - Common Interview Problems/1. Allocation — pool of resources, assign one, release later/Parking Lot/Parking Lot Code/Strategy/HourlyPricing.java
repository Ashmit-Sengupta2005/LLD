package Strategy;

import java.time.Duration;
import java.time.Instant;

import Domain.PricingRule;
import Domain.Ticket;

public class HourlyPricing implements PricingStrategy {
    private final PricingRule pricingRule;

    public HourlyPricing(PricingRule rule) {
        this.pricingRule = rule;
    }

    public long calculateAmountInPaise(Ticket ticket, Instant exitTime) {
        long hourlyRate = pricingRule.getRatePerHourInPaise();
        Instant entryTime = ticket.getEntryTime();
        Duration timeElapsed = Duration.between(entryTime, exitTime);
        long hours = Math.max((long) Math.ceil((double) (timeElapsed.toMinutes() / 60.0)), 1);
        long finalPrice = hours * hourlyRate;
        return finalPrice;
    }
}
