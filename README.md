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

## 💭 Why Microservices?
Traditional monolithic budget apps struggle with:

- Scaling issues during month-end when all users check statements
- Tight coupling between transaction recording and budget calculation
- Difficulty adding new features without affecting existing code

Our solution:

- Independent services that scale based on load
- Async processing via Kafka prevents blocking
- Each service deployable independently

---

## 👓 System Components
### User Service (port 8080)
Handles authentication and user profiles.
**Key Endpoints** <br/>
```
POST /api/auth/signup
POST /api/auth/login
GET /api/users/me
PUT /api/users/me
```
**Tech :**
Quarkus, JWT, BCrypt, PostgreSQL

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
**Tech :** Quarkus Panache, PostgreSQL, Kafka Consumer

### Transaction Service (port 8082)
Records income/expense entries.
**Key Endpoints** <br/>
```
POST /api/transactions
GET /api/transactions
GET /api/transactions/summary
DELETE /api/transactions/{id}
```
**Tech :** Quarkus, PostgreSQL, Kafka Producer

---

## 💪 How It Works
### Example Flow : User buys coffee for $5
```
1. User → POST /api/transactions {"amount": 5, "category": "Food"}
2. Transaction Service → Saves to DB
3. Transaction Service → Publishes to Kafka topic
4. Budget Service → Receives event
5. Budget Service → Updates Food budget: spent += 5
6. User → GET /api/budgets/1/status → See updated balance
```

---

## 🖥️ Tech Stack
| Layer | Technology | Version | Why? |
|-------|-----------|---------|------|
| Framework | Quarkus | 3.15.1 (LTS) | Fast startup, low memory, native compilation support |
| Database | PostgreSQL | 16.6 | Latest stable, excellent JSON support, proven reliability |
| ORM | Hibernate Panache | (Quarkus bundled) | Simplifies repository pattern, reduces boilerplate |
| Messaging | Apache Kafka | 3.8.0 | Industry standard for event streaming |
| Auth | JWT (SmallRye JWT) | 4.5.2 | Stateless authentication, microservices-friendly |
| Container | Docker | 27.3.1 | Standard containerization platform |
| Orchestration | Kubernetes | 1.31 | Auto-scaling, self-healing, load balancing |
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
├── k8s/                    # Kubernetes configs
├── docker-compose.yml      # Local dev environment
└── README.md
```

## 🔗 Deployment
### Containerization

- Each microservice is containerized using Docker
- Services run independently in isolated containers
- Docker Compose manages multi-container setup for local development

### Kubernetes

- Kubernetes orchestrates all microservices for production
- Horizontal Pod Autoscaler (HPA) provides automatic scaling based on CPU usage
- Self-healing through liveness and readiness probes
- Load balancing across multiple pod replicas

### CI/CD

- Jenkins pipeline automates build, test, and deployment
- Automated Docker image building on every commit
- Integration with Docker registry for image storage
- Continuous deployment to Kubernetes cluster via kubectl

---

## 📚 What We Learned
- Kafka makes services truly independent
- Kubernetes health checks are crucial for reliability
- Panache drastically reduces boilerplate
- JWT simplifies auth in distributed systems

---

## 🎯 Conclusion
Budget Management System successfully implements microservices architecture for personal finance tracking. Users benefit from real-time spending visibility and instant budget alerts, enabling better financial control and preventing overspending.
The project demonstrates key microservices principles: independent scaling via Kubernetes, loose coupling through Kafka events, and rapid development with Quarkus. While adding operational complexity, the architecture delivers tangible benefits in scalability, reliability, and maintainability.
