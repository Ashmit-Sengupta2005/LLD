# Repository Layer — How to Decide

## Which repositories to create?

> **One repository per entity you need to look up later.**

Walk your flow and ask: *"do I ever need to fetch this back?"*

| Entity | Fetched later? | Repository? |
| --- | --- | --- |
| `Ticket` | ✅ at exit, by ticket id | ✅ `TicketRepository` |
| `PricingRule` | ✅ at entry, by vehicle + rule type | ✅ `PricingRuleRepository` |
| `Vehicle` | ❌ reached through the ticket | ❌ |
| `ParkingSlot` | ❌ reached through `Floor` | ❌ |
| `Receipt` | ❌ handed to the driver and forgotten | ❌ (add one if receipts must be reprintable) |

**So: 2 repositories, not 6.** Don't create one per class out of habit.

---

## What goes inside?

**Only storage verbs** — `save`, `find`, `remove`. No business rules, no fee maths, no validation.

```java
public class TicketRepository {
    private final Map<UUID, Ticket> tickets = new HashMap<>();

    public void save(Ticket ticket)      { tickets.put(ticket.getTicketId(), ticket); }
    public Ticket findById(UUID id)      { return tickets.get(id); }   // null if absent
    public void remove(UUID id)          { tickets.remove(id); }
}
```

A `Map` **is** the database here. Swapping it for a real DB changes only this class — that is the whole point of the layer.

---

## Rules

- **Methods are nouns-with-verbs only:** `save`, `findById`, `findAll`, `remove`
- **No logic.** `isTicketValid()` belongs in `Ticket` or the service, never here
- **Key by what you search by** — tickets by `UUID`, pricing rules by `(VehicleType, RuleType)`
- **Return `null` or `Optional` for "not found"**, and let the **service** decide if that is an error

> **Say in an interview:** *"I'm using an in-memory `Map` as the repository. Moving to a real database means changing only this class — nothing above it knows."*
