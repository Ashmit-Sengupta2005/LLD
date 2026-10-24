package Factory;

import Domain.PricingRule;
import Strategy.*;

public class PricingStrategyFactory {

    public static PricingStrategy getInstance(PricingRule rule) {
        switch (rule.getRuleType()) {
            case HOURLY:
                return new HourlyPricing(rule);
            case FLAT:
                return new FlatPricing(rule);
            default:
                throw new IllegalArgumentException("Unknown rule type: " + rule.getRuleType());
        }
    }
}
