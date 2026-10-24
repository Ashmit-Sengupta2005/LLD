package Strategy;

import Domain.Ticket;
import java.time.Instant;

public interface PricingStrategy {
    long calculateAmountInPaise(Ticket ticket, Instant ExitTime);
}
