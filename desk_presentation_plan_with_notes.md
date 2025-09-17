# 🗂 Desk Management API — Interview Presentation Plan

## 0️⃣ Setup (20–30 sec)
- **Open**: Postman (or cURL) and the repo root.
- **Intro line**:  
  > “I’ll start with the problem, show core flows end-to-end, then cover correctness, concurrency, and stretch goals.”
    We chose java springboot for speed to build MVP, ability to quickly handle data and multithreading
---

## 1️⃣ Problem & Constraints (45 sec)
- **Problem**: “A simple web API to manage desks and track check-ins/outs across buildings.”
- **Constraints honored**:  
  - No authentication  
  - In-memory storage  
  - 60-minute build time  
  - Clarity & correctness first
- **Definition of done**: CRUD for desks, validated state transitions, consistent errors, and fast demo feedback.

---

## 2️⃣ High-Level Design (60–75 sec)
- **Domain model**:  
  `Desk` → `id`, `floor`, `spotNumber`, `status`, `lastUpdated`, `spotType`
- **Endpoints grouped by intent**:  
  **Inventory**:  
  - `POST /desks`  
  - `GET /desks`  
  - `GET /desks/{id}`  
  - `PUT /desks/{id}`  

  **Tracking**:  
  - `POST /desks/{id}/checkin`  
  - `POST /desks/{id}/checkout`  

  **Ops/Reporting (stretch)**:  
  - `GET /desks/usage?floor=`
- **Storage & Concurrency**:  
  - Chose **HashMap** for lookup speed over HashSet/Arrays (too slow to iterate).  
  - `ConcurrentHashMap` + per-key atomic updates (`compute`, `putIfAbsent`) to avoid race conditions (single-JVM scope).
- **UUID Handling**: Generated in code instead of requiring it in API calls.

---

## 3️⃣ Additional Features & Technical Decisions
- **Seeder & Configuration**: Pre-populate realistic desks with configurable `app.seed.enabled` & `app.seed.count`.
- **Toggle-Ready Store Config**: Easily switch between seeded data and empty store.
- **Random Desk Generator API**: For quick testing scenarios.
- **Detailed Unit Tests**: Covering happy/unhappy paths and concurrency cases.
- **Logging**: Application-level logging for request tracing and debugging.
- **SonarQube (or similar)**: Code quality & vulnerability scanning.
- **Health Check Endpoint**: For quick application readiness checks.

---

## 4️⃣ Live Demo Order (5–7 minutes)

### **A) Create → Retrieve → List** (happy path first)
1. **POST /desks** → Create two desks (different floors, different `spotType`)  
   _Narration_: “IDs & timestamps are server-generated; invalid fields return 400.”
2. **GET /desks/{id}** → Show `lastUpdated` and `status`.
3. **GET /desks?floor=&status=&type=** → Filter by floor/status/type.  
   _Narration_: “Filters are optional & composable; empty filters list all.”

---

### **B) Update (non-status)**
4. **PUT /desks/{id}** → Change `spotNumber` or `spotType` (not `status`).  
   _Narration_: “`PUT` is intentionally limited to non-status info to keep transitions explicit.”

---

### **C) State Transitions (core correctness)**
5. **POST /desks/{id}/checkin**  
   - AVAILABLE → **200**  
   - Repeat → **409 CONFLICT**  
   _Narration_: “Per-key atomic update ensures only one thread wins a race.”
6. **POST /desks/{id}/checkout**  
   - OCCUPIED → **200**  
   - Repeat → **409**  
   _Narration_: “Simple state machine: AVAILABLE ↔ OCCUPIED.”

---

### **D) Usage Reporting (stretch)**
7. **GET /desks/usage** & **/desks/usage?floor=2** → Show `{ total, occupied, percent }`.  
   _Narration_: “Computed on demand from the map—fine for this scope.”

---

## 5️⃣ Error-Handling Quick Hits (60–90 sec)
- **404 NOT_FOUND** → Unknown desk id (`GET /desks/{fake}`)  
- **400 BAD_REQUEST** → Invalid payload (negative floor)  
- **409 CONFLICT** → Invalid transitions (double check-in/checkout)  
_Narration_: Consistent error format & clear messages.

---

## 6️⃣ Concurrency & Correctness (60–90 sec)
- **Problem**: Two users try to check-in the same desk.
- **Solution**: Per-key atomic mutation (`compute`) ensures check-then-set is one step.
- **Scope**: Single process. For multi-instance → Redis (WATCH/MULTI or Lua) or RDBMS with transactions.

---

## 7️⃣ Testing Strategy (45–60 sec)
- **Happy paths**: create → get → list → update → check-in/out.  
- **Unhappy paths**: 404 / 400 / 409.  
- **Optional**: Concurrent test → exactly one check-in succeeds.

---

## 8️⃣ Stretch Goals & Future Work (60–90 sec)
- Spot types (already implemented) with filtering.
- Reservations: book windows + overlap checks → 409 on conflicts.
- **Next steps** for production:
  - Redis/RDBMS persistence
  - Auth/RBAC
  - Metrics & tracing
  - Rate limits & caching
  - Event log (`desk_events`)
  - Multithreading & scalability

---

## 9️⃣ 1-Minute Whiteboard Summary
- **Scope**: CRUD, transitions, usage stats  
- **Model**: Desk(id, floor, spotNumber, status, lastUpdated, spotType)  
- **Concurrency**: Per-key atomic updates in `ConcurrentHashMap`  
- **Errors**: 400 / 404 / 409 consistent  
- **Demo Path**: POST → GET → FILTER → PUT → CHECKIN/checkout (409s) → USAGE
