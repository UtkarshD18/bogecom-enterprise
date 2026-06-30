# Project Roadmap: bogEcom Enterprise Migration

This document outlines the strategic migration of the legacy MERN e-commerce application to a highly scalable, Modular Monolith architecture built on Spring Boot and Angular. 

## 📍 Current Phase
**Status:** In Progress
**Milestone:** `v0.1.0` - Foundation Release
**Objective:** Establish the robust, enterprise-grade Spring Boot core containing strictly pinned Maven dependencies, Docker integrations, Liquibase database management, Global Exception Handling, structured `ApiResponse` bindings, and comprehensive static analysis (Spotless, Checkstyle, PMD, SpotBugs).

---

## 🚀 Release Schedule

### ✅ Phase 1: Core Foundation & Security
*   **`v0.1.0` Foundation:** Bootstrapped repository, strict engineering standards, CI pipelines, and base frameworks.
*   **`v0.2.0` Authentication:** User registration, device-specific JWT sessions, secure cookies, strict password policies, and login flows.
*   **`v0.3.0` Products:** Catalog mapping, product variants, pricing models, category hierarchies, and search optimizations.

### 🛒 Phase 2: Checkout Experience
*   **`v0.4.0` Cart:** Database-backed session carts.
*   **`v0.5.0` Checkout:** Orchestration using `CheckoutFacade` spanning cart, stock, and calculations.
*   **`v0.6.0` Orders:** Finalized order persistence and status tracking.
*   **`v0.7.0` Payments:** Stripe integration and webhook reconciliation.

### 📦 Phase 3: Post-Purchase & Fulfillment
*   **`v0.8.0` Inventory:** Hard reservations using `@Version` optimistic locking to prevent overselling.
*   **`v0.9.0` Admin & CRM:** Administrative dashboards, role-based access control, and user activity auditing.
*   **`v1.0.0` Enterprise Release:** Full production readiness, testing coverage targets met, and final quality assurance.

---

## 🔮 Future Enterprise Enhancements (Post v1.0.0)
The architecture is purposefully designed as a **Modular Monolith** to enable future scaling transitions. Once `v1.0.0` is stable, the following upgrades are planned:

1.  **Distributed Caching:** Transition from Spring `@Cacheable` (ConcurrentHashMap) to Redis for cross-node cache synchronization.
2.  **Distributed Locking:** Transition checkout inventory reservations to Redis locks if optimistic locking produces high contention at scale.
3.  **Event Streaming:** Replace in-memory `ApplicationEventPublisher` with Apache Kafka / RabbitMQ to fully decouple cross-domain workflows (e.g., sending notification emails when an order is placed).
4.  **Microservice Extraction:** If domain scalability requirements diverge, physically extract isolated packages (e.g., `inventory/`) into standalone Spring Boot instances orchestrated by Spring Cloud Gateway.
