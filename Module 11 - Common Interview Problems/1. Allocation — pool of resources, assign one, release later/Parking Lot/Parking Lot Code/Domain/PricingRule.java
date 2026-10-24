package Domain;

import java.util.*;
import Enum.VehicleType;
import Enum.RuleType;

public class PricingRule {
    private final UUID PricingRuleID;
    private final VehicleType vehicleType;
    private final RuleType ruleType;            // HOURLY or FLAT
    private final long ratePerHourInPaise;      // used when HOURLY
    private final long flatRateInPaise;         // used when FLAT

    public PricingRule(VehicleType vehicleType, RuleType ruleType,
                       long ratePerHourInPaise, long flatRateInPaise) {
        this.PricingRuleID = UUID.randomUUID();
        this.vehicleType = vehicleType;
        this.ruleType = ruleType;
        this.ratePerHourInPaise = ratePerHourInPaise;
        this.flatRateInPaise = flatRateInPaise;
    }

    // convenience constructors so callers do not pass a meaningless zero
    public static PricingRule hourly(VehicleType type, long ratePerHourInPaise) {
        return new PricingRule(type, RuleType.HOURLY, ratePerHourInPaise, 0);
    }

    public static PricingRule flat(VehicleType type, long flatRateInPaise) {
        return new PricingRule(type, RuleType.FLAT, 0, flatRateInPaise);
    }

    // getters
    public UUID getPricingRuleID() {
        return this.PricingRuleID;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public RuleType getRuleType() {
        return this.ruleType;
    }

    public long getRatePerHourInPaise() {
        return this.ratePerHourInPaise;
    }

    public long getFlatRateInPaise() {
        return this.flatRateInPaise;
    }

    // a rule is reference data - no setters. A new rate is a new rule.

    @Override
    public String toString() {
        return vehicleType + " - " + ruleType + " - Rs. "
             + (ruleType == RuleType.HOURLY ? ratePerHourInPaise / 100.0 + "/hr"
                                            : flatRateInPaise / 100.0 + " flat");
    }
}
