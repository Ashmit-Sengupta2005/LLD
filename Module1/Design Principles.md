# Design Principles

### 1. DRY (Don't Repeat Yourself)

Avoid writing the same logic multiple times. Instead, reuse code wherever possible.

**Importance:**

- Reduces code duplication.
- Makes maintenance easier.
- Changes need to be made in only one place.

---

### 2. KISS (Keep It Simple, Stupid)

Keep the code simple and avoid unnecessary complexity. Use the simplest solution that solves the problem.

**Importance:**

- Easier to read and debug.
- Easier to maintain.
- Saves development time.

**Example:**
> Use `n % 2 == 0` to check whether a number is even instead of writing complicated logic.

---

### 3. YAGNI (You Aren't Gonna Need It)

Don't implement features until they are actually needed. Avoid adding functionality just because you might need it in the future.

**Importance:**

- Avoids unnecessary code.
- Reduces complexity.
- Saves development time.

**Example:**
> In a note-taking app, implement creating and viewing notes first. Don't build tagging or cloud syncing unless required.
