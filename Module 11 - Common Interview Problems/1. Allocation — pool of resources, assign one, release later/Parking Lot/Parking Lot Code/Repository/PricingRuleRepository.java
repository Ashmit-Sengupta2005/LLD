package Repository;

import java.util.*;
import Domain.PricingRule;
import Enum.RuleType;
import Enum.VehicleType;

public class PricingRuleRepository {
    private final Map<UUID, PricingRule> PricingRuleMap = new HashMap<>();

    public void savePricingRule(PricingRule newRule) {
        PricingRuleMap.put(newRule.getPricingRuleID(), newRule);
    }

    public void removePricingRule(UUID newRuleID) {
        PricingRuleMap.remove(newRuleID);
    }

    public PricingRule viewPricingRule(VehicleType vehicleType, RuleType ruleType) {
        for (PricingRule rule : PricingRuleMap.values()) {
            if (rule.getVehicleType() == vehicleType && rule.getRuleType() == ruleType) {
                return rule;
            }
        }
        return null; // no such combination, e.g. TRUCK has no HOURLY rate
    }
}
