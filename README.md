# Budget-Management-Service
PV217 SOA Course Team Project - Masaryk University, Fall 2025

---

## ✈️ Overview
Personal finance tracking platform built with microservices architecture. Users can set budgets, record transactions, and monitor spending in real-time.

### 🛠️ Team
|Hyunsoo KIm</br>[@maeng-kim](https://github.com/@maeng-kim)|Lucia Moser</br>[@moserl](https://github.com/moserl)|
|:---:|:---:|
|`User Service`</br>`Infra`|`Budget Service`</br>`Transaction Service`|
### Course
PV217 Service Oriented Architecture
### Year
2025 Fall
---

## 📚 Description of the Project
The Budget-Management-System Fintrack is a microservices-based application designed for personal finance tracking.
Fintrack enables users to:
- Register and authenticate using JWT
- Create and manage budget categories
- Record income and expenses
- Monitor their personal spending
- Track their remaining Budgets
The system is divided into independent microservices, namely User, Budget and Transaction. Each of them is responsible for a separate domain and communicate through API. Furthermore, Prometheus collects health metrics from each service and Grafana visualizes data to monitor usage trends and system status. 
---

## 💭 Why Microservices?
Microservices solve several problems that arise in traditional monolithic applications:
- Scalability: Each service can scale independently depending on load
- Loose Coupling: Budget calculations and transaction recording are seperated, which reduces complexity.
- Easier Collaboration: adding new features does not affect existing code
- Better Fault Isolation: a failure in one service does not break the entire system
---

## ⭐ Benefits of using Microservices in this Project
- Seperation of concerns: User authentication is handled in User Service, Budget logic is isolated in Budget Service and financial entries/transactions are handled in Transaction Service.
- Fault Tolerance: if the Transaction service fails, authentication will still work.
- Event-Driven Updates: Budget updates happen automatically when Transactions arrive.
- Improved Developer Productivity: Team members can work on different services independently, and there are no merge conflicts as in large monolithic projects.
---

## ☔ Drawbacks of using Microservices 
- Possibility of security risks as the services communicate through API gateways, which can expose them to threats.
- Monitoring becomes more complex which requires usage of Prometheus and Grafana.
- Data consistency challenges as there is not just one single Database.
---

## 👓 System Components
### User Service (port 8080)
Handles authentication and user profiles.
**Key Endpoints** <br/>
```
POST /api/users
POST /api/users/login
POST /api/users/User
GET /api/users/{id}
GET /api/users/me
PUT /api/users/me
PUT /api/users/me/password
```
**Tech :**
Quarkus, JWT, PostgreSQL, Quarkus Panache

### Budget Service (port 8081)
Manages budget goals and tracks usage.
**Key Endpoints** <br/>
```
POST /api/budgets
GET /api/budgests
GET /api/budgets/{id}/status
PUT /api/budgets/{id}
DELETE /api/budgets/{id}
```
**Tech :** Quarkus Panache, PostgreSQL

### Transaction Service (port 8082)
Records income/expense entries.
**Key Endpoints** <br/>
```
POST /api/transactions
GET /api/transactions
GET /api/transactions/summary
DELETE /api/transactions/{id}
```
**Tech :** Quarkus, PostgreSQL

---

## 💪 Story/Scenario of Usage
### Example Flow : User buys coffee for €5
```
1. The User signs up and logs in to the application. Upon login, the User Service issues a JWT access token.
2. User sets a monthly budget for Drinks with a limit of €50 → POST /api/budgets 
3. The user sends a request to record the purchase of a coffee for €5 → POST /api/transactions with {"amount": 5, "category": "Drinks"}
4. The Transaction service validates the request and saves the transaction into its Database.
5. User checks budget service via GET /api/budgets/{id}/status.
6. The Budget Service calculates how much has been spent for the Drinks category.

```

---

## 🖥️ Tech Stack
| Layer | Technology | Version | Why? |
|-------|-----------|---------|------|
| Framework | Quarkus | 3.15.1 (LTS) | Fast startup, low memory, native compilation support |
| Database | PostgreSQL | 16.6 | Latest stable, excellent JSON support, proven reliability |
| ORM | Hibernate Panache | (Quarkus bundled) | Simplifies repository pattern, reduces boilerplate |
| Auth | JWT (SmallRye JWT) | 4.5.2 | Stateless authentication, microservices-friendly |
| Container | Docker | 27.3.1 | Standard containerization platform |
| Monitoring | Prometheus | 2.54.1 | Metrics collection and alerting |
| Visualization | Grafana | 11.2.0 | Dashboard and data visualization |
| API Docs | OpenAPI/Swagger | 3.1.0 | Interactive API documentation |

---

## ⛺️ Database Schema
We use PostgreSQL instance with separate schemas per service (practical for small team). <br/>
**user_Service.users :**
id, email, password_hash, full_name, currency, created_at <br/>
**budget_Service.budgets :**
id, user_id, category, amount, spent_amount, period, start_date, end_date <br/>
**transaction_Service.transactions :**
id, user_id, budget_id, type, amount, category, description, transaction_date <br/>

  *Production note: Ideally each service would have its own database instance.*

---

## 🏛️ Project Structure
```
budget-management-system/
├── user-service/
│   └── src/main/java/com/budget/user/
│       ├── model/          # User entity
│       ├── resource/       # REST endpoints  
│       └── service/        # Business logic
├── budget-service/
├── transaction-service/
├── docker-compose.yml      # Local dev environment
└── README.md
```

## 🏗️ Microservices Overview
```
┌──────────────────────────────────────────────────────────────────┐
│                    API Gateway / Load Balancer                   │
│                                                                  │
└────────────────┬─────────────────┬───────────────┬───────────────┘
                 │                 │               │
        ┌────────▼────────┐ ┌─────▼──────┐ ┌─────▼──────────┐
        │  User Service   │ │  Budget    │ │  Transaction   │
        │    :8080        │ │  Service   │ │    Service     │
        │                 │ │   :8081    │ │     :8082      │
        │ ┌─────────────┐ │ │ ┌────────┐ │ │ ┌────────────┐ │
        │ │   Domain    │ │ │ │ Domain │ │ │ │   Domain   │ │
        │ │  - User     │ │ │ │ Budget │ │ │ │Transaction │ │
        │ │  - Auth     │ │ │ │ Money  │ │ │ │   Money    │ │
        │ └─────────────┘ │ │ └────────┘ │ │ └────────────┘ │
        └────────┬────────┘ └──────┬─────┘ └────────┬───────┘
                 │                 │                │
        ┌────────▼────────┐ ┌─────▼──────┐ ┌────────▼────────┐
        │   PostgreSQL    │ │ PostgreSQL │ │   PostgreSQL    │
        │    userdb       │ │  budgetdb  │ │  transactiondb  │
        │    :5432        │ │   :5433    │ │     :5434       │
        └─────────────────┘ └────────────┘ └─────────────────┘
```

## 🍀 DDD Diagram
<img width="826" height="509" alt="스크린샷 2025-11-25 오후 11 58 59" src="https://github.com/user-attachments/assets/f80ad337-0010-47cf-b37b-8c729df8bafe" />

## 🔗 Deployment
### Containerization

- Each microservice is containerized using Docker
- Services run independently in isolated containers
- Docker Compose manages multi-container setup for local development

---

## 📚 What We Learned
- Panache drastically reduces boilerplate
- JWT simplifies auth in distributed systems

---

## 🎯 Conclusion
Budget Management System successfully implements microservices architecture for personal finance tracking. Users benefit from real-time spending visibility and instant budget alerts, enabling better financial control and preventing overspending.
The project demonstrates key microservices principles: loose coupling, and rapid development with Quarkus. While adding operational complexity, the architecture delivers tangible benefits in scalability, reliability, and maintainability.
