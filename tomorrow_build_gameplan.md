# ✅ Desk Management API — **Tomorrow’s Build Game Plan**

**Goal**: Ship a clean, demo-ready Spring Boot service in one day.  
**Stack**: Java 21 · Spring Boot 3.3 · Maven/Gradle · JUnit5 · AssertJ · (Optional) Testcontainers  
**Principles**: Do the simplest thing that works, prove correctness with tests, leave seams for scale.

---

## 0) Prep (15–20 min max)
- Create repo, branch `feature/day1-mvp`.
- Project skeleton: Spring Initializr (Web, Validation, Actuator).
- Set package: `com.liatrio.deskmanager`.
- Add `README.md` with quick start and endpoint list (stub now, fill later).
- Create Postman/HTTP collection (empty requests; add as you implement).

> **Command hint**: `./mvnw spring-boot:run` or `./gradlew bootRun`

---

## 1) Architecture Shape (No Code Yet — 5 min)
```
com.liatrio.deskmanager
├─ api (controllers)
├─ core
│  ├─ model (Desk, Status, SpotType, ErrorResponse)
│  ├─ service (DeskService)
│  ├─ ports (DeskRepository)
│  └─ exceptions (NotFound, Conflict, BadRequest)
├─ infra
│  ├─ store (InMemoryDeskRepository -> ConcurrentHashMap)
│  ├─ config (DataSeeder, feature toggles)
│  └─ web (GlobalExceptionHandler, validation)
└─ app (SpringBootApplication)
```
**Decisions**:
- **UUID generated in code**, not via API.
- **HashMap** for lookup speed (in-memory), wrapped by **ConcurrentHashMap** for thread-safe access.
- **Atomic transitions** via `compute` (per-key).

---

## 2) Timeboxed Schedule

### Hour 1 — Models, Repo, Error Handling
- `Desk` model: `id`, `floor`, `spotNumber`, `status`, `spotType`, `lastUpdated` (UTC ISO-8601).
- Enums: `Status { AVAILABLE, OCCUPIED }`, `SpotType { STANDARD, EXECUTIVE, STANDING, … }`.
- `DeskRepository` (port) + `InMemoryDeskRepository` (ConcurrentHashMap).
- Global exception handler + `ErrorResponse` contract.
- Validation annotations (floor ≥ 0, spotNumber non-empty).

**Checkpoint**: Compiles, app boots.

---

### Hour 2 — Service Logic & Core Endpoints
- `DeskService`:
  - Create: generate UUID, set timestamps.
  - Get/List with filters (floor, status, type).
  - Update info (no status changes).
  - **Check-in / Check-out** with `compute(id, ...)` atomicity + 409 on invalid transition.
- `DeskController` endpoints:
  - `POST /desks`
  - `GET /desks`
  - `GET /desks/{id}`
  - `PUT /desks/{id}`
  - `POST /desks/{id}/checkin`
  - `POST /desks/{id}/checkout`

**Checkpoint**: Manual curl/Postman for create/get/checkin/checkout works.

---

### Hour 3 — Usage Stats, Health, Test Data
- `/desks/usage?floor=` → `{ total, occupied, percent }` (computed on demand).
- Actuator or simple `GET /health` (readiness + desk count).
- **Seeder** with toggles: `app.seed.enabled`, `app.seed.count`.
- **Random desk generator** (under `app.test.endpoints.enabled`) → `POST /testdata/random?count=N`.

**Checkpoint**: Lists/usage look alive thanks to seed + generator.

---

### Hour 4 — Tests (Minimum But Meaningful)
**Unit (service)**
- Create → Get → List (filters).
- Update info (no status change).
- Transitions: AVAILABLE→OCCUPIED (OK), double check-in → 409, checkout from AVAILABLE → 409.

**Integration (web)**
- `@SpringBootTest` + `MockMvc`: 200/400/404/409 happy/unhappy paths.
- Concurrency smoke: fire two check-ins simultaneously → assert one **200** and one **409**.

**Coverage target**: prioritize service layer (≥80% there is fine).

---

### Hour 5 — DX Polish
- README quick start updated (curl examples, feature toggles).
- Postman/HTTP collection filled in **demo order**:
  1) POST /desks (x2)  
  2) GET /desks/{id}  
  3) GET /desks?floor=&status=&type=  
  4) PUT /desks/{id}  
  5) POST /desks/{id}/checkin (repeat to show 409)  
  6) POST /desks/{id}/checkout (repeat to show 409)  
  7) GET /desks/usage  
  8) GET /desks/usage?floor=2  
  9) POST /testdata/random?count=10 (if toggle on)
- Basic logging: request summaries and transition outcomes.

**Optional (time permitting)**
- Dockerfile and local build `docker build -t desk-api:dev .`
- Sonar/CodeQL scan locally or simple GitHub Action (build + test).

---

## 3) Feature Toggles & Profiles
- `app.seed.enabled=true` (dev), `false` (prod).
- `app.seed.count=25` (dev default).
- `app.test.endpoints.enabled=true` (dev), `false` (prod).
- Profiles: `dev` (verbose logs), `prod` (concise).

---

## 4) Demo Script (5–7 min)
1. **Create** two desks → show server UUID/timestamps.
2. **Retrieve** one by id → verify fields.
3. **List** with filters → floor/status/type.
4. **Update** non-status fields via `PUT`.
5. **Transitions**: check-in then repeat (see **409**); checkout then repeat (see **409**).
6. **Usage** overall and by floor.
7. **Health** and (if allowed) **random generator** to show fast test data.
8. **Concurrency story**: per-key atomic compute; single-JVM scope; Redis/DB next steps.

---

## 5) Acceptance Checklist (DoD)
- [ ] All MVP endpoints implemented and validated.
- [ ] UUID generated server-side.
- [ ] Atomic transitions verified (test proves 200 + 409).
- [ ] Consistent error contract (400/404/409).
- [ ] Seeder + generator behind toggles; docs updated.
- [ ] Health endpoint present.
- [ ] Unit + integration tests pass locally.
- [ ] README + Postman/HTTP collection ready for share.
- [ ] (Optional) Dockerfile builds locally.

---

## 6) Risks & Fallbacks
- **Time crunch** → Ship endpoints + tests first; usage/generator/health are secondary.
- **Flaky concurrent test** → Keep tiny & deterministic; validate with 2 threads only.
- **IDE/config issues** → Use Spring Initializr + Boot defaults, no custom plugins today.

---

## 7) Copy/Paste Helpers

**Run (Maven)**
```bash
./mvnw spring-boot:run
```

**Run (Gradle)**
```bash
./gradlew bootRun
```

**Seed toggle (application.yml)**
```yaml
app:
  seed:
    enabled: true
    count: 25
  test:
    endpoints:
      enabled: true
spring:
  main:
    banner-mode: "off"
```

**Quick curl sanity**
```bash
# create
curl -s -X POST localhost:8080/desks -H "Content-Type: application/json"   -d '{"floor":1,"spotNumber":"A-1","spotType":"STANDARD"}' | jq

# checkin (should 200), repeat (should 409)
curl -i -X POST localhost:8080/desks/{id}/checkin
curl -i -X POST localhost:8080/desks/{id}/checkin

# usage
curl -s localhost:8080/desks/usage | jq
```

---

## 8) Next Steps (post-day1)
- Persistence (Postgres + JPA) **or** Redis for transitions.
- Idempotency via `Idempotency-Key` header caching results.
- Reservations with overlap detection (409 on conflict).
- Metrics (Micrometer), tracing, rate limiting, auth/RBAC.
