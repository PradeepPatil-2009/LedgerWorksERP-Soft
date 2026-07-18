# LedgerWorks ERP — Licensing & Windows Distribution Options

Decision document. Nothing here is implemented yet — this captures the options and trade-offs so we can pick a path later.

Goal: ship the app to Windows customers as an easy-to-run package, protected by a license **key** that encodes a validity period (days / months / years), with security strong enough that customers cannot forge, extend, or trivially bypass it.

---

## 0. Reality check (important)

**"No one can ever decrypt or bypass it" is not achievable for software that runs fully offline on the customer's own PC.** Any code that runs on their CPU can, in principle, be patched by a determined expert. This is true for every product on earth (all major software gets cracked eventually).

Two extra facts specific to us:
- The app is **Java/Spring Boot → bytecode decompiles almost perfectly** with free tools unless we obfuscate or compile to native.
- The realistic and very achievable goal is: **casual users and normal SMB customers cannot bypass it, keys cannot be forged or extended, and cracking costs more effort than just buying a license.**

Anyone promising "uncrackable" offline licensing is overselling.

---

## 1. Core security model — how the key works

Use **asymmetric signing (NOT encryption)**. This is the industry standard and the strongest part of the scheme.

| Who | Holds | Can |
|-----|-------|-----|
| **You (vendor)** | the **PRIVATE key** — never ships | generate/sign license keys |
| **The shipped app** | the **PUBLIC key** — embedded in the exe | only *verify* keys, never create them |

- A license key is a small signed payload: `{ customer, issuedAt, expiryDate, machineId, features }`.
- Signed with **Ed25519** (compact, modern) or **RSA-2048**.
- Because the private key never leaves you, **customers can never forge a key or extend the expiry** — even if they fully decompile the exe. That is the guarantee that actually holds.
- The only residual weakness is that an expert could patch out the `verify()` call in the binary → mitigated by the hardening layers below.

**Validity period (days/months/years)** = the signed payload carries `expiryDate`; the app refuses to start when `now > expiryDate`.
**Clock-rollback protection** = store a "last-seen date" in an obfuscated file/registry; reject if the system clock moves backwards.

---

## 2. Security layers (defense in depth — pick how far to go)

| # | Layer | Protects against | Effort | Verdict |
|---|-------|------------------|--------|---------|
| 1 | **Ed25519 signed license keys** | Forging / extending keys | Low | **Must have** — the core |
| 2 | **Hardware binding (node-lock)** | Copying one key to many PCs (machineId = disk serial + MAC + CPU) | Low | **Recommended** |
| 3 | **Clock-rollback guard** | Cheating expiry by setting the clock back | Low | Recommended |
| 4 | **Obfuscation (ProGuard)** | Casual decompiling / patching the check | Medium | Recommended if staying on the JVM |
| 5 | **GraalVM native exe** | Serious reverse-engineering (no bytecode) | Medium–High | Optional, phase 2 |
| 6 | **Online activation** | Almost everything; also enables revocation | Medium + you host a server | **Not chosen** (offline was selected) |

Selected direction so far: **fully offline signed keys** (layers 1–4, optionally 5 later).

---

## 3. Database when distributed — the real blocker

Today the app connects to `jdbc:mysql://localhost:3306` (root / empty password). A customer's fresh Windows PC has **no MySQL**, so the app won't boot. Options:

### Option A — Embedded H2, file mode  ⭐ recommended for single-file distribution
- DB is a **file next to the exe** (`./data/ledgerworks.mv.db`), created automatically on first run. Zero install, zero config.
- The app **already runs on H2 for tests** (28/28 pass), so entities/queries are already compatible.
- Backup = copy one file (ties into the existing backup module).
- Can **AES-encrypt** the file (`;CIPHER=AES`) so data/license state isn't readable in a text editor.
- Distribution profile:
  ```properties
  # application-dist.properties
  spring.datasource.url=jdbc:h2:file:./data/ledgerworks;AUTO_SERVER=TRUE
  spring.datasource.username=sa
  spring.datasource.password=
  spring.datasource.driver-class-name=org.h2.Driver
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  spring.jpa.hibernate.ddl-auto=update
  ```
- Caveat: a few MySQL-only SQL bits need H2 equivalents (already hit + fixed one: `DATE_FORMAT` via `FUNCTION(...)`). Running `mvn test` on H2 is the compatibility proof.

### Option B — Bundle MySQL with an installer
- Keep MySQL exactly as-is; an installer sets up MySQL as a Windows service.
- Heavier download, more that can break on the customer's machine, not a single loose file.
- Only worth it if we depend on MySQL-specific behavior we don't want to port.

### Option C — SQLite
- Similar to H2 (single file, zero-install) but needs a community Hibernate dialect + driver. H2 is the more natural fit for a Spring/Hibernate app, so **prefer H2 unless there's a reason**.

---

## 4. Packaging the Windows executable

| Option | Output | Pros | Cons |
|--------|--------|------|------|
| **jpackage** (JDK tool) ⭐ start here | Windows app-image (folder) or `.exe`/`.msi` installer, bundles a private JRE | Reliable, works with H2 out of the box, official | Bytecode inside → decompilable (mitigate with ProGuard) |
| **GraalVM native-image** | One true native `.exe`, no JVM | Hard to reverse (no bytecode), smallest attack surface, genuine single file | More build effort: Spring reflection + H2 need native config; phase-2 work |
| **Launch4j / exe wrapper** | `.exe` that calls a JRE | Simple | Weakest; just a wrapper around bytecode |

Note: whichever we pick, the **React frontend ships as static resources served by Spring Boot** (already the model), and the app opens the browser to `localhost` on start.

---

## 5. Recommended path (fast → hardened)

**Phase 1 (get a working, sellable product):**
1. Ed25519 **signed keys** + **hardware binding** + **clock-rollback guard** (layers 1–3).
2. **Embedded H2** file DB (`dist` profile).
3. **jpackage** installer bundling a private JRE + **ProGuard** obfuscation.

**Phase 2 (optional max hardening):**
4. Recompile with **GraalVM native** → single tamper-resistant exe.
5. (If ever online) add **activation + revocation**.

The unforgeable signed key is the real protection at every phase — decompiling the binary still never lets a customer mint or extend a key.

---

## 6. Components to build (when we decide to proceed)

1. **`license-keygen`** — standalone CLI (you keep the private key):
   `java -jar keygen.jar --customer "Acme" --days 365 --machine <machineId>` → prints a signed key.
2. **In-app `LicenseService`** — verifies the key with the embedded public key on startup; enforces expiry + machine binding + rollback guard; blocks the app (or runs read-only) when invalid/expired; shows a clear "license expired / invalid" screen.
3. **`dist` profile** — H2 file DB so the shipped build is self-contained.
4. **Packaging scripts** — jpackage (+ ProGuard); GraalVM native later.

All of the above are self-contained and do **not** touch the accounting code.

---

## 7. Open decisions

- [ ] Packaging: single native `.exe` (GraalVM) **or** installer bundling JRE (jpackage)?  → recommend jpackage first, GraalVM later.
- [ ] DB: embedded H2 (recommended) confirmed?  Encrypt the H2 file (yes/no)?
- [ ] Machine binding: lock each key to one PC (recommended), or allow N installs per key?
- [ ] Expiry behavior on lapse: hard block, or degrade to read-only?
- [ ] Grace period after expiry (e.g. 7 days) — yes/no?
- [ ] Trial mode (e.g. 14-day auto license on first run) — yes/no?
