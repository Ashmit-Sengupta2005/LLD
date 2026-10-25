# Controller Layer — How to Decide

## How many controllers?

> **One per actor** — not per entity, not per service.

Ask: *who is pressing the button?*

| Actor | Real device | Controller |
| --- | --- | --- |
| Driver arriving | entry gate machine | `EntryController` |
| Driver leaving | exit gate machine | `ExitController` |
| Staff | admin panel | `AdminController` |

**→ 3 controllers.** Three separate screens, three different sets of allowed actions.

**Why not one `ParkingLotController` with everything?** Then the entry gate could call `updatePricing()`. **Separate controllers are the permission boundary.**

---

## What goes in a controller?

Exactly four things:

1. **Receive** raw input (strings from a screen / JSON)
2. **Convert** it to types the service wants (`String` → enum, fields → object)
3. **Call ONE service method**
4. **Return** the result

```java
public class EntryController {
    private final ParkingLotService lot;

    public EntryController(ParkingLotService lot) { this.lot = lot; }   // injected

    public EntryResult park(String plate, String vehicleType, String ruleType) {
        Vehicle vehicle = new Vehicle(plate, VehicleType.valueOf(vehicleType));   // convert
        return lot.EnterParkingLot(vehicle, RuleType.valueOf(ruleType));          // one call
    }
}
```

---

## What does NOT go in a controller

| ❌ | Belongs in |
| --- | --- |
| `if (slot.isEmpty()) ...` — any decision | service |
| Two service calls in one method | service (that's a use case) |
| Fee maths, slot searching | strategy / domain |

> **The tell:** **more than one service call in a controller method** means the use case leaked out of the service.

> **The test:** *"If I added a mobile app, would I copy-paste this?"* Converting input — fine, every entry point does it. Copying an **if/else sequence** — that is business logic in the wrong layer.

---

## Which methods go where

**The method goes with the actor who triggers it.**

```java
EntryController : park(...)
ExitController  : unpark(...)
AdminController : addFloor(), updatePricing(), lostTicketOverride(), getOccupancy()
```

> Note **lost-ticket override sits in `AdminController`**, not the exit gate — a driver can't authorise it, staff can. That is the actor rule doing its job.

---

## Rules

- **Clock lives here:** `Instant.now()` is called by the controller and passed into the service
- **Service is injected** through the constructor, never created inside
- **Return the DTO** the service gave you — don't unwrap it
- **3 lines per method.** Longer means logic crept in
