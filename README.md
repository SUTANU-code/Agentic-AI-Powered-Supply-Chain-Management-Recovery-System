# SupplyChain AI — Backend

Final-year project: Agentic AI-powered supply-chain management and recovery system.
Spring Boot + PostgreSQL now, React frontend next, Python + LangGraph agentic layer later.

No Lombok - every entity/DTO is plain, explicit Java (constructors + getters/setters
written out), so nothing depends on annotation-processing being set up correctly in
your IDE.

## What was actually broken before, and what changed

If you inherited this project and every API call was returning `401 Unauthorized`
with no way to log in, here's why: `pom.xml` already pulled in
`spring-boot-starter-security` (for the JWT auth planned in the README below), but
there was no `SecurityConfig` class anywhere. The moment that starter is on the
classpath, Spring Security auto-locks *every* endpoint behind session login — with
no way to authenticate a plain JSON API caller. That's now fixed: `config/SecurityConfig.java`
defines the real rules, and `user/AuthController.java` gives you somewhere to
actually register/log in and get a token. See "Authentication" below.

A second bug also existed independently of that: `Order` and `OrderItem` reference
each other (`Order.items` ↔ `OrderItem.order`), which caused infinite recursion
(`StackOverflowError`) when Jackson tried to serialize an `Order` to JSON. Fixed with
`@JsonIgnore` on `OrderItem.order` — see the comment in that file.

## Structure (feature-based packaging)

```
com.supplychain.ai
 ├── config/       → SecurityConfig, JWT filter/service, CORS, OpenAPI, error handlers
 ├── common/       → shared exceptions, ApiError, GlobalExceptionHandler
 ├── user/         → auth (register/login/me) + user management
 ├── customer/
 ├── product/
 ├── warehouse/
 ├── inventory/
 ├── supplier/
 ├── order/        → Order + OrderItem
 ├── shipment/
 ├── incident/
 └── SupplyChainAiApplication.java
```

Each feature package contains its own entity, repository, service, and controller —
kept together because they change together.

## Prerequisites

- Java 25+
- Maven 3.9+ (or use the included `./mvnw` wrapper if present)
- PostgreSQL running locally (or a free-tier cloud instance — Supabase/Neon/Railway),
  **or** Docker, if you'd rather not install Postgres yourself (see below)

## Option A: run with Docker (fastest)

```bash
cp .env.example .env
# edit .env and set a real JWT_SECRET (openssl rand -base64 64)
docker compose up --build
```

This starts Postgres and the app together. The app waits for Postgres to report
healthy before starting. API is at `http://localhost:8080`.

## Option B: run locally with Maven

1. Create the database:
   ```sql
   CREATE DATABASE supplychain_ai;
   ```
2. Either export environment variables, or just accept the defaults baked into
   `application.properties` (username `postgres`, password `postgres`, DB on
   `localhost:5432`):
   ```bash
   export DB_USERNAME=postgres
   export DB_PASSWORD=your_real_password
   export JWT_SECRET=$(openssl rand -base64 64)
   ```
3. Run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Confirm it starts on `http://localhost:8080` with no errors, and check
   `http://localhost:8080/actuator/health` returns `{"status":"UP"}`.

## Authentication

Everything under `/api/**` requires a valid JWT **except** `/api/auth/register`
and `/api/auth/login`. Get a token, then send it as
`Authorization: Bearer <token>` on every other request.

```bash
# 1. Register (first user - make them an ADMIN so they can list other users later)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada Admin","email":"[email protected]","password":"password123","role":"ADMIN"}'

# Response includes a token:
# {"token":"eyJhbGciOi...","tokenType":"Bearer","expiresInMs":86400000,"user":{...}}

# 2. Log in (subsequent times)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"[email protected]","password":"password123"}'

# 3. Use the token on any other endpoint
TOKEN="paste-the-token-here"
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"

# 4. Who am I?
curl http://localhost:8080/api/auth/me -H "Authorization: Bearer $TOKEN"
```

`GET /api/users` is ADMIN-only (returns id/name/email/role for every registered
user — never password hashes). Every other endpoint just needs to be logged in
as any role.

## API docs (Swagger UI)

With the app running: `http://localhost:8080/swagger-ui.html`. Click **Authorize**
and paste `Bearer <your token>` to try protected endpoints directly from the browser.

## API endpoints

```
Auth         POST /api/auth/register  {name, email, password, role?}
             POST /api/auth/login     {email, password}
             GET  /api/auth/me
Users        GET  /api/users                              ← ADMIN only

Customer     GET/POST /api/customers, GET/PUT/DELETE /api/customers/{id}
Product      GET/POST /api/products, GET/PUT/DELETE /api/products/{id}
Warehouse    GET/POST /api/warehouses, GET/PUT/DELETE /api/warehouses/{id}
Supplier     GET/POST /api/suppliers, GET/PUT/DELETE /api/suppliers/{id}
             GET  /api/suppliers/offers/product/{productId}
             POST /api/suppliers/offers

Inventory    GET /api/inventory
             GET /api/inventory/{id}
             GET /api/inventory/product/{productId}     ← future Inventory Agent tool
             GET /api/inventory/warehouse/{warehouseId}
             POST /api/inventory/restock   {productId, warehouseId, quantity}
             POST /api/inventory/reserve   {productId, warehouseId, quantity}   ← future Execution Agent tool
             POST /api/inventory/release   {productId, warehouseId, quantity}
             POST /api/inventory/dispatch  {productId, warehouseId, quantity}

Order        GET  /api/orders (?status=)
             GET  /api/orders/{id}
             POST /api/orders   {customerId, deadline, items: [{productId, quantity}]}
             PATCH /api/orders/{id}/status?status=SHIPPED

Shipment     GET  /api/shipments (?status=)
             GET  /api/shipments/{id}
             GET  /api/shipments/order/{orderId}
             GET  /api/shipments/overdue                ← future Logistics Agent tool
             POST /api/shipments   {orderId, warehouseId, courier, expectedDelivery}
             PATCH /api/shipments/{id}/dispatch
             PATCH /api/shipments/{id}/deliver
             PATCH /api/shipments/{id}/delay

Incident     GET  /api/incidents (?status=|?type=)
             GET  /api/incidents/{id}
             POST /api/incidents   {type, relatedOrderId?, relatedWarehouseId?, description}
             PATCH /api/incidents/{id}/status?status=RESOLVED
             GET  /api/incidents/{id}/logs
             POST /api/incidents/{id}/logs   {agentName, action, message}

Health       GET /actuator/health   ← public, used by the Docker healthcheck
```

## Running tests

```bash
./mvnw test
```

Tests run against an in-memory H2 database (see `src/test/resources/application-test.properties`),
so `mvn test` never needs a real Postgres connection. There's currently one smoke
test (`SupplyChainAiApplicationTests`) that just verifies the full Spring context
starts cleanly — the single most common thing to break when wiring gets messy, and
exactly the kind of check that would have caught the missing SecurityConfig
immediately.

## Architecture notes / known trade-offs

- **Controllers return JPA entities directly** instead of mapping to response
  DTOs first (except in `user/`, which always returns `UserResponse` to avoid
  ever leaking a password hash). This is fine for a project this size, but it
  does mean `spring.jpa.open-in-view=true` is required (kept on intentionally,
  see the comment in `application.properties`) so lazy fields like
  `Order.customer` don't throw `LazyInitializationException` during JSON
  serialization. The next real improvement here would be introducing response
  DTOs for every domain and turning `open-in-view` off — better performance,
  no risk of accidentally leaking internal fields, no lazy-loading surprises.
- **Role-based access control is intentionally light**: any authenticated user
  (`ADMIN` or `STAFF`) can call every business endpoint; only `GET /api/users`
  is locked to `ADMIN` via `@PreAuthorize`. Tighten this per-domain if your
  use case needs it — `@EnableMethodSecurity` is already on, so it's just a
  matter of adding `@PreAuthorize("hasRole('ADMIN')")` where you want it.
- **`ddl-auto=update`** is fine for a class project; switch to `validate` (and
  add a migration tool like Flyway) before this ever holds real data.

## Roadmap

- Milestone 4: incident-trigger scheduler (auto-create incidents from overdue
  shipments / low stock)
- Milestone 6+: Python + LangGraph agent service, calling this API as tools
- Milestone 9: React dashboard, including the agent activity trail
  (`/api/incidents/{id}/logs`)
