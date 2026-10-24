package Strategy;

import java.time.Instant;

import Domain.PricingRule;
import Domain.Ticket;

/*Flat pricing = one fixed fee, regardless of how long you stayed. 
Park a truck for 10 minutes or 10 hours — ₹150 either way.
 Used where you don't want to meter: trucks, event parking, day passes */
public class FlatPricing implements PricingStrategy {
    private final PricingRule pricingRule;

    public FlatPricing(PricingRule rule) {
        this.pricingRule = rule;
    }

    public long calculateAmountInPaise(Ticket ticket, Instant ExitTime) {
        return pricingRule.getFlatRateInPaise(); // time dosen't matter
    }
}
