# SupplyChainAgent

**Agentic AI-Powered Supply Chain Management & Recovery System**

SupplyChainAgent is a full-stack supply-chain management platform designed to monitor orders, inventory, shipments, suppliers, warehouses, and operational incidents from a centralized system.

The project is being developed as a **final-year BCA project**, with a roadmap toward an agentic AI layer that can detect supply-chain problems, analyze available operational data, recommend recovery actions, and eventually execute approved actions through the backend APIs.

---

## 🚀 Project Overview

Modern supply chains involve multiple connected operations:

* Customer orders
* Product and inventory management
* Warehouse operations
* Supplier management
* Shipment tracking
* Delivery deadlines
* Operational incidents

A delay in one area can affect several others. SupplyChainAgent provides a structured backend and dashboard for managing these operations and establishes an API-based foundation for future **AI agents**.

The planned AI layer will use **Python + LangGraph** and communicate with the Spring Boot backend through REST APIs.

### Example Future Agent Workflow

```text
Order / Inventory / Shipment Data
              ↓
        Incident Detection
              ↓
       AI Agent Analysis
              ↓
     Identify Possible Causes
              ↓
       Generate Recovery Plan
              ↓
     Execute Approved Actions
              ↓
       Record Agent Activity
```

The goal is not simply to add a chatbot, but to build an operational system where AI agents can interact with real application data and tools.

---

# 🏗️ Repository Structure

```text
SupplyChainAgent/
│
├── supplychain-backend/                 ← Spring Boot REST API
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/supplychain/ai/
│   │   │   │       ├── config/          ← Security, JWT, CORS, OpenAPI
│   │   │   │       ├── common/          ← Exceptions, API errors
│   │   │   │       ├── user/            ← Authentication & users
│   │   │   │       ├── customer/        ← Customer management
│   │   │   │       ├── product/         ← Product management
│   │   │   │       ├── warehouse/       ← Warehouse management
│   │   │   │       ├── inventory/       ← Inventory operations
│   │   │   │       ├── supplier/        ← Supplier management
│   │   │   │       ├── order/            ← Orders & order items
│   │   │   │       ├── shipment/         ← Shipment tracking
│   │   │   │       ├── incident/         ← Incident management & logs
│   │   │   │       └── SupplyChainAiApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │
│   ├── pom.xml
│   └── .env                             ← Local environment variables
│
├── supply-chain-frontend/               ← React + Vite dashboard
│   │
│   ├── src/
│   │   ├── api/                         ← Centralized API client
│   │   ├── components/                  ← Reusable UI components
│   │   ├── context/                     ← Authentication & application state
│   │   └── pages/                       ← Application pages
│   │
│   ├── package.json
│   └── vite.config.js                   ← Development proxy
│
├── .gitignore
└── README.md
```

---

# 🛠️ Technology Stack

## Backend

| Technology        | Purpose                            |
| ----------------- | ---------------------------------- |
| Java 25+          | Backend development                |
| Spring Boot       | REST API and application framework |
| Spring Security   | Authentication and authorization   |
| JWT               | Stateless API authentication       |
| Spring Data JPA   | Database access                    |
| Hibernate         | ORM                                |
| PostgreSQL        | Relational database                |
| Maven             | Dependency and build management    |
| Springdoc OpenAPI | Swagger API documentation          |

The backend intentionally uses **plain Java entities and DTOs without Lombok**. Constructors, getters, setters, and other required methods are written explicitly so the project does not depend on IDE annotation-processing configuration.

## Frontend

| Technology   | Purpose                            |
| ------------ | ---------------------------------- |
| React        | User interface                     |
| Vite         | Frontend development/build tooling |
| React Router | Client-side routing                |
| JavaScript   | Frontend logic                     |
| CSS          | UI styling                         |

## Planned AI Layer

| Technology  | Purpose                                            |
| ----------- | -------------------------------------------------- |
| Python      | AI service                                         |
| LangGraph   | Agent workflow orchestration                       |
| LLM         | Reasoning and decision support                     |
| REST API    | Communication with Spring Boot                     |
| Agent Tools | Inventory, shipment, order and incident operations |

---

# ✨ Current Features

## 🔐 Authentication

The backend provides JWT-based authentication.

### Available operations

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

Authentication flow:

```text
Register / Login
       ↓
   JWT Token
       ↓
Authorization: Bearer <token>
       ↓
Protected API
```

Most `/api/**` endpoints require authentication.

The `/api/auth/register` and `/api/auth/login` endpoints are public.

---

## 👥 User Management

Administrators can retrieve registered users through:

```text
GET /api/users
```

This endpoint is restricted to users with the `ADMIN` role.

Password hashes are never returned in the API response.

---

# 📦 Supply Chain Modules

The backend is organized around the major supply-chain domains.

### Customer

```text
GET    /api/customers
POST   /api/customers
GET    /api/customers/{id}
PUT    /api/customers/{id}
DELETE /api/customers/{id}
```

### Product

```text
GET    /api/products
POST   /api/products
GET    /api/products/{id}
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Warehouse

```text
GET    /api/warehouses
POST   /api/warehouses
GET    /api/warehouses/{id}
PUT    /api/warehouses/{id}
DELETE /api/warehouses/{id}
```

### Supplier

```text
GET    /api/suppliers
POST   /api/suppliers
GET    /api/suppliers/{id}
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}

GET    /api/suppliers/offers/product/{productId}
POST   /api/suppliers/offers
```

### Inventory

Inventory supports operational actions that will later become tools available to AI agents.

```text
GET  /api/inventory
GET  /api/inventory/{id}
GET  /api/inventory/product/{productId}
GET  /api/inventory/warehouse/{warehouseId}

POST /api/inventory/restock
POST /api/inventory/reserve
POST /api/inventory/release
POST /api/inventory/dispatch
```

These operations provide the foundation for future **Inventory Agent** and **Execution Agent** capabilities.

---

# 🛒 Order Management

Orders can be created and tracked through:

```text
GET   /api/orders
GET   /api/orders/{id}
POST  /api/orders
PATCH /api/orders/{id}/status
```

Order creation supports multiple products:

```json
{
  "customerId": 1,
  "deadline": "2026-10-05T18:00:00",
  "items": [
    {
      "productId": 1,
      "quantity": 5
    },
    {
      "productId": 2,
      "quantity": 2
    }
  ]
}
```

---

# 🚚 Shipment Management

Shipment operations include:

```text
GET   /api/shipments
GET   /api/shipments/{id}
GET   /api/shipments/order/{orderId}
GET   /api/shipments/overdue

POST  /api/shipments

PATCH /api/shipments/{id}/dispatch
PATCH /api/shipments/{id}/deliver
PATCH /api/shipments/{id}/delay
```

The overdue-shipment endpoint is particularly important for the future **Logistics Agent**, which can monitor delayed shipments and initiate recovery workflows.

---

# 🚨 Incident Management

Incidents provide a way to record operational problems and their resolution history.

```text
GET   /api/incidents
GET   /api/incidents/{id}

POST  /api/incidents

PATCH /api/incidents/{id}/status

GET   /api/incidents/{id}/logs
POST  /api/incidents/{id}/logs
```

Each incident can contain:

* Incident type
* Description
* Related order
* Related warehouse
* Current status
* Agent/user activity logs

The incident log is intended to become the **audit trail for future AI-agent actions**.

---

# 🤖 Agentic AI Roadmap

The AI layer will be introduced after the core business system is stable.

The planned architecture is:

```text
                    React Dashboard
                           │
                           ▼
                  Spring Boot Backend
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
       Orders          Inventory        Shipments
          │                │                │
          └────────────────┼────────────────┘
                           │
                           ▼
                    Incident System
                           │
                           ▼
                  Python Agent Service
                           │
                       LangGraph
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
    Detection Agent   Analysis Agent   Recovery Agent
          │                │                │
          └────────────────┼────────────────┘
                           ▼
                    Backend API Tools
                           │
                           ▼
                 Approved Actions + Logs
```

### Planned agents

#### 1. Monitoring / Detection Agent

Monitors operational data for conditions such as:

* Low inventory
* Overdue shipments
* Delivery delays
* Potential order fulfillment problems

#### 2. Analysis Agent

Investigates the detected incident by querying available backend tools.

For example:

```text
Shipment delayed
      ↓
Check shipment details
      ↓
Check order deadline
      ↓
Check warehouse
      ↓
Check inventory
      ↓
Check supplier availability
```

#### 3. Recovery Agent

Generates possible recovery actions based on available information.

Possible actions could include:

* Restocking inventory
* Selecting an alternative supplier
* Reallocating inventory
* Updating shipment status
* Creating an incident
* Recording the recovery process

Actions that modify business data should be controlled through explicit application rules and authorization.

#### 4. Agent Activity / Audit Trail

Every significant AI action will be recorded through:

```text
/api/incidents/{id}/logs
```

This allows users to understand:

```text
What happened?
       ↓
Why was the incident detected?
       ↓
What information did the agent inspect?
       ↓
What action was proposed?
       ↓
What action was executed?
       ↓
What was the result?
```

---

# 🗄️ Database

The project uses **PostgreSQL**.

Default local database configuration:

```text
Host:     localhost
Port:     5432
Database: supplychain_ai
```

The application reads credentials from environment variables rather than storing production credentials directly in source code.

---

# ⚙️ Environment Configuration

Create a `.env` file for local development.

Example:

```env
DB_USERNAME=postgres
DB_PASSWORD=your_database_password

JWT_SECRET=your_long_random_secret
JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

### Important

`.env` contains secrets and must **never be committed to Git**.

The backend configuration references these values through Spring environment placeholders:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}
```

> **Note:** When running Spring Boot directly with Maven or an IDE, a `.env` file is not automatically loaded by Spring Boot. The required variables must be provided through the process environment or the IDE's run configuration. Docker Compose can load `.env` automatically.

---

# ▶️ Running the Backend

## Option A — Local PostgreSQL

### 1. Create the database

Open PostgreSQL and run:

```sql
CREATE DATABASE supplychain_ai;
```

### 2. Configure environment variables

Set:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

For PowerShell:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your_long_random_secret"
```

### 3. Start the backend

From:

```text
SupplyChainAgent/supplychain-backend
```

run:

```powershell
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

# 🐳 Docker

The project can also be run using Docker Compose.

From the backend directory:

```bash
docker compose up --build
```

Docker Compose can use the `.env` file for environment configuration.

The application waits for PostgreSQL to become healthy before starting.

---

# 🌐 Running the Frontend

Navigate to:

```text
SupplyChainAgent/supply-chain-frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Vite will start the React development server, normally at:

```text
http://localhost:5173
```

The frontend communicates with the Spring Boot backend through the configured Vite development proxy.

---

# 🔑 Authentication Example

### Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "password123",
  "role": "ADMIN"
}
```

The response contains a JWT token.

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

### Authenticated Request

Include:

```http
Authorization: Bearer <your-token>
```

Example:

```http
GET /api/products
Authorization: Bearer <your-token>
```

---

# 📚 API Documentation

Once the backend is running, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

Use the **Authorize** button to provide:

```text
Bearer <your-token>
```

Swagger can then be used to test protected API endpoints directly from the browser.

---

# ❤️ Health Check

The application exposes:

```text
GET /actuator/health
```

A healthy application returns:

```json
{
  "status": "UP"
}
```

This endpoint is also used by the Docker health-check configuration.

---

# 🧪 Testing

Backend tests use an in-memory H2 database, so the test suite does not require a running PostgreSQL instance.

Run:

```bash
mvn test
```

or, if the Maven wrapper is included:

```bash
./mvnw test
```

The test suite currently includes a Spring application-context smoke test.

This helps detect configuration and dependency problems during development.

---

# 🏛️ Architecture

SupplyChainAgent follows a **feature-based backend architecture**.

Instead of separating every controller, service, repository, and entity into large global folders, each business domain keeps its related components together.

Example:

```text
supplier/
├── Supplier.java
├── SupplierRepository.java
├── SupplierService.java
└── SupplierController.java
```

This makes individual business modules easier to understand and maintain.

---

# 🔒 Security

The application uses:

* Spring Security
* JWT authentication
* Password hashing
* Role-based authorization
* Protected REST endpoints
* CORS configuration
* Environment-based secrets

Business APIs require authentication.

Currently, `GET /api/users` is restricted to `ADMIN` users while the other business endpoints are available to authenticated users.

Authorization can be made more granular as the application grows.

---

# ⚠️ Current Architecture Trade-offs

### Direct Entity Responses

Most controllers currently return JPA entities directly instead of converting everything into response DTOs.

This keeps the project relatively simple during development.

The `user` module uses dedicated response objects so password hashes are never exposed.

A future improvement is to introduce response DTOs across all domains.

### Open Session in View

The project currently uses:

```properties
spring.jpa.open-in-view=true
```

This supports lazy relationships during response serialization.

Once response DTOs are introduced throughout the application, this can be changed to:

```properties
spring.jpa.open-in-view=false
```

### Database Schema

Development currently uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This is convenient during development.

For a production deployment, the recommended approach is to use:

```text
ddl-auto=validate
+
Flyway/Liquibase migrations
```

---

# 🗺️ Development Roadmap

## Phase 1 — Core Backend

* [x] Project structure
* [x] PostgreSQL integration
* [x] Entity/repository/service/controller layers
* [x] Authentication
* [x] JWT security
* [x] User roles
* [x] Customer management
* [x] Product management
* [x] Warehouse management
* [x] Supplier management
* [x] Inventory operations
* [x] Order management
* [x] Shipment management
* [x] Incident management
* [x] Incident activity logs
* [x] Swagger documentation

## Phase 2 — React Dashboard

* [x] React + Vite setup
* [x] Authentication flow
* [x] Dashboard
* [x] Customer management
* [x] Product management
* [x] Warehouse management
* [x] Supplier management
* [x] Inventory management
* [x] Order management
* [x] Shipment management
* [x] Incident management

## Phase 3 — Operational Intelligence

* [ ] Automatic overdue-shipment detection
* [ ] Low-stock detection
* [ ] Automatic incident creation
* [ ] Incident monitoring
* [ ] Operational analytics

## Phase 4 — Agentic AI

* [ ] Python agent service
* [ ] LangGraph workflow
* [ ] Backend API tools
* [ ] Monitoring Agent
* [ ] Analysis Agent
* [ ] Recovery Agent
* [ ] Tool-based agent execution
* [ ] Agent activity logs
* [ ] Human approval for sensitive actions

## Phase 5 — Advanced Capabilities

* [ ] Multi-agent coordination
* [ ] Recovery strategy generation
* [ ] Supplier comparison
* [ ] Inventory redistribution recommendations
* [ ] Shipment recovery workflows
* [ ] Agent performance monitoring
* [ ] Improved auditability

---

# 🎯 Project Objective

The long-term objective of SupplyChainAgent is to combine a reliable supply-chain management platform with an agentic AI layer capable of working with real operational data and application tools.

Rather than replacing the existing business system, the AI layer is designed to operate **on top of the Spring Boot platform**, using controlled APIs to inspect information, reason about incidents, recommend actions, and—where permitted—execute recovery operations.

```text
Reliable Backend
      +
React Dashboard
      +
Operational Data
      +
Python + LangGraph
      +
Tool-Using AI Agents
      =
SupplyChainAgent
```

---

# 👨‍💻 Project

**SupplyChainAgent**
Final-Year Academic Project

**Backend:** Java + Spring Boot
**Frontend:** React + Vite
**Database:** PostgreSQL
**AI Layer:** Python + LangGraph
**Architecture:** REST API + Agentic AI

---

## 📄 License

This project is developed for academic and educational purposes.
