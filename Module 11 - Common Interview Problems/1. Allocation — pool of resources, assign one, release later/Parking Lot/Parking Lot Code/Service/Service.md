# Service Layer — How to Decide

## How many services?

> **One service per use-case group, not per entity.**

Count the **actors' workflows**, not the tables.

| Parking Lot | |
| --- | --- |
| Driver parks / exits | `ParkingLotService` |
| Admin adds floors, changes rates | could be `AdminService` — or just extra methods here |

**→ 1 service.** Two if you want admin separated.

**❌ Don't make** `TicketService`, `SlotService`, `VehicleService`. One entity each, no use case of their own — they just forward calls and add a layer of nothing.

---

## What goes in a service?

**The steps of a use case, in order.** It calls others; it doesn't compute.

```java
EntryResult park(Vehicle v, RuleType ruleType, Instant entryTime) {
    rule   = pricingRuleRepo.viewPricingRule(...)   // repository
    slot   = allocationStrategy.findOptimalSlot(...) // strategy
    slot.occupy()                                    // domain
    ticket = new Ticket(...)                         // domain
    ticketRepo.saveTicket(ticket)                    // repository
    return EntryResult.success(ticket)               // dto
}
```

**If a method reads like *step 1, step 2, step 3* — it belongs here.**

---

## What does NOT go in a service

| Logic | Belongs in |
| --- | --- |
| `slotType == vehicleType` | `ParkingSlot.canFit()` — one object's own fields |
| `hours * rate` | `PricingStrategy` — it varies |
| `if (type == HOURLY) new HourlyPricing(...)` | `PricingStrategyFactory` |
| `map.put(id, ticket)` | repository |
| building the response message | DTO factory method |

> **The smell:** a service reaching into another object's fields (`slot.getSlotType() == v.getType()`) means that logic should move **into** that object.

---

## Rules

- **One method per use case**, named as the user would say it — `park`, `unpark`, not `processVehicleEntryRequest`
- **Guard clause per failure**, return early: no slot → `failure(...)`, bad ticket → `failure(...)`
- **Order matters** — pay *before* releasing the slot; never release on a failed payment
- **Depend on interfaces** (`SeatAllocationStrategy`), never on `NearestSeatAllocationStrategy`
- **Time comes in as a parameter**; `Instant.now()` is called by the controller

> **Size check:** ~20–25 lines per use-case method. Longer means logic leaked in that belongs in the domain or a strategy.
