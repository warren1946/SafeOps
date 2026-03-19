# SafeOps Architecture Guide

## Overview

SafeOps is a **multi-tenant, white-label capable Mining Safety Management Platform** built with **Spring Boot 4**, **Kotlin**, and **Hexagonal Architecture**. It supports multiple mines/organizations with complete data isolation while allowing
per-tenant customization.

---

## System Architecture

### High-Level Design

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SafeOps Platform                                │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌──────────────────┐  │
│  │   Web App   │  │  Mobile App │  │ WhatsApp Bot│  │  Admin Dashboard │  │
│  │  (Compose)  │  │  (Compose)  │  │             │  │   (Compose MP)   │  │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └────────┬─────────┘  │
│         └─────────────────┴─────────────────┴─────────────────┘             │
│                                     │                                        │
│                         ┌───────────▼────────────┐                          │
│                         │     API Gateway        │                          │
│                         │  (Tenant Resolution)   │                          │
│                         └───────────┬────────────┘                          │
│                                     │                                        │
│  ┌──────────────────────────────────▼────────────────────────────────────┐  │
│  │                        Backend Services                                │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │  │
│  │  │  Auth    │ │ Inspection│ │  Hazard  │ │  Safety  │ │ Template │   │  │
│  │  │  Module  │ │  Module  │ │  Module  │ │  Module  │ │  Module  │   │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘   │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │  │
│  │  │  Tenant  │ │ Notification│ │  Storage │ │ Reporting│ │   Audit  │   │  │
│  │  │  Module  │ │  Module   │ │  Module  │ │  Module  │ │  Module  │   │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘   │  │
│  │  ┌──────────────────────────────────────────────────────────────┐   │  │
│  │  │                    Shared Kernel                              │   │  │
│  │  │  (Events, Multi-tenancy, i18n, Exceptions, Common Utils)     │   │  │
│  │  └──────────────────────────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                     │                                        │
│  ┌──────────────────────────────────▼────────────────────────────────────┐  │
│  │                    Infrastructure Layer                                │  │
│  │  PostgreSQL  │  Flyway  │  Redis (Cache)  │  S3/Local (Files)        │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Module Structure

Each module follows **Hexagonal Architecture** (Clean Architecture):

```
module/
├── api/                           # Primary Adapters (Driving)
│   ├── controllers/              # REST Controllers
│   ├── dto/                      # Request/Response DTOs
│   └── mappers/                  # DTO <-> Domain mappers
│
├── application/                   # Application Layer
│   ├── ports/                    # Interfaces (driven adapters)
│   ├── services/                 # Use Cases / Application Services
│   └── statemachine/             # State machines (if applicable)
│
├── domain/                        # Domain Layer (Core Business Logic)
│   ├── model/                    # Domain Entities
│   ├── valueobjects/             # Value Objects
│   ├── events/                   # Domain Events
│   ├── exceptions/               # Domain Exceptions
│   └── specifications/           # Business Rules
│
└── infrastructure/                # Secondary Adapters (Driven)
    ├── persistence/              # JPA Entities, Repositories
    ├── adapters/                 # External service integrations
    └── config/                   # Module-specific configuration
```

---

## Core Modules

### 1. Shared Kernel (`modules/shared/kernel`)

Foundation for the entire platform.

| Component         | Purpose                              |
|-------------------|--------------------------------------|
| `TenantAware`     | Interface for tenant-scoped entities |
| `TenantContext`   | Thread-local tenant resolution       |
| `DomainEvent`     | Base class for domain events         |
| `EventPublisher`  | Event bus abstraction                |
| `TenantJpaFilter` | Automatic tenant isolation           |

### 2. Tenant Module (`modules/tenant`)

Multi-tenancy and white-label support.

**Features:**

- Tenant provisioning and lifecycle management
- Per-tenant configuration (features, branding, languages)
- Subscription plan management (BASIC, PROFESSIONAL, ENTERPRISE, CUSTOM)
- WhatsApp Business API configuration per tenant
- White-label branding (colors, logos, app name)

**Key Entities:**

- `Tenant` - Main tenant aggregate
- `TenantConfiguration` - Settings and feature flags
- `TenantBranding` - White-label customization

### 3. Auth Module (`modules/auth`)

Authentication and authorization.

**Features:**

- JWT-based stateless authentication
- Role-based access control (RBAC)
- Tenant-scoped user management
- Token refresh mechanism

**Roles:**

- `SUPER_ADMIN` - Platform administrator
- `ADMIN` - Tenant administrator
- `SUPERVISOR` - Mine supervisor
- `OFFICER` - Safety officer (field)

### 4. Core Module (`modules/core`)

Location hierarchy management.

**Hierarchy:**

```
Mine → Site → Shaft → Area
```

Each level is tenant-scoped and supports multiple entries per tenant.

### 5. Inspection Module (`modules/inspections`)

Safety inspection workflow engine.

**Features:**

- Create inspections from templates
- Question-by-question completion
- Photo evidence upload
- Review and approval workflow
- Scoring and compliance tracking

**Status Flow:**

```
DRAFT → SUBMITTED → [APPROVED | REJECTED]
```

### 6. WhatsApp Module (`modules/whatsapp`)

WhatsApp Business API integration with conversation state machine.

**Features:**

- State machine-driven conversation flows
- Multi-language support (6 languages)
- Inspection workflow via chat
- Hazard reporting via chat
- Real-time session management

**Conversation States:**

```
IDLE → AUTHENTICATING → MAIN_MENU → 
  ├─ INSPECTION_SELECTING_LOCATION → INSPECTION_SELECTING_TEMPLATE →
  │  INSPECTION_IN_PROGRESS → INSPECTION_ANSWERING_QUESTION → ... → COMPLETED
  │
  └─ HAZARD_REPORTING_LOCATION → HAZARD_DESCRIBING → HAZARD_RATING_SEVERITY →
     HAZARD_ADDING_PHOTO → HAZARD_CONFIRMING → COMPLETED
```

### 7. Notification Module (`modules/notification`)

Multi-channel notification system.

**Channels:**

- Email (SMTP)
- SMS (Twilio/similar)
- Push notifications (Firebase)
- WhatsApp (Business API)
- In-app notifications

**Features:**

- Template-based messages
- Priority-based routing
- Retry mechanism
- Scheduled notifications

### 8. Storage Module (`modules/storage`)

File storage abstraction.

**Features:**

- S3 or local filesystem backend
- File validation (type, size)
- Category-based organization
- Pre-signed URL generation

**Categories:**

- `INSPECTION_PHOTO`
- `HAZARD_EVIDENCE`
- `PROFILE_PICTURE`
- `REPORT_PDF`

### 9. Reporting Module (`modules/reporting`)

Analytics and report generation.

**Features:**

- Dashboard statistics
- Compliance rate tracking
- Incident analysis
- PDF/Excel export
- Scheduled reports

### 10. Audit Module (`modules/audit`)

Audit logging for compliance.

**Features:**

- Automatic audit trail
- Data change tracking
- User action logging
- Compliance reporting

### 11. i18n Module (`modules/i18n`)

Internationalization and localization.

**Supported Languages:**

- English (en)
- Portuguese (pt) - Angola/Mozambique
- French (fr) - DRC/Cameroon
- Swahili (sw) - Tanzania/Kenya
- Afrikaans (af) - South Africa
- Zulu (zu) - South Africa

---

## Multi-Tenancy Strategy

### Data Isolation Approach

SafeOps uses **Shared Database, Separate Schema** approach:

- Single PostgreSQL database
- Tenant ID column on all tenant-scoped tables
- Application-level filtering via `TenantContext`

### Tenant Resolution

1. **API Requests:** Subdomain or header-based (`X-Tenant-ID`)
2. **WhatsApp:** Phone number → User → Tenant lookup
3. **Background Jobs:** Tenant ID passed explicitly

### Tenant Context Filter

```kotlin
@Component
class TenantContextFilter : OncePerRequestFilter() {
    override fun doFilterInternal(request, response, chain) {
        val tenantId = resolveTenant(request)
        TenantContext.setCurrentTenant(tenantId, tenantSlug)
        try {
            chain.doFilter(request, response)
        } finally {
            TenantContext.clear()
        }
    }
}
```

---

## White-Label Capabilities

### Customization Options per Tenant

| Feature             | BASIC    | PROFESSIONAL | ENTERPRISE | CUSTOM |
|---------------------|----------|--------------|------------|--------|
| Custom Branding     | ❌        | ✅            | ✅          | ✅      |
| Custom Domain       | ❌        | ❌            | ✅          | ✅      |
| API Access          | ❌        | ✅            | ✅          | ✅      |
| Advanced Reporting  | ❌        | ✅            | ✅          | ✅      |
| Custom Integrations | ❌        | ❌            | ✅          | ✅      |
| SLA                 | Standard | Priority     | 24/7       | Custom |

### Branding Configuration

```yaml
branding:
  primaryColor: "#1e3a5f"
  secondaryColor: "#e63946"
  logoUrl: "https://cdn.example.com/logo.png"
  appName: "Acme Mining Safety"
  supportEmail: "support@acmemining.com"
```

---

## Database Schema

### Key Tables

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    tenants      │     │     users       │     │    mines        │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ id (PK)         │◄────┤ tenant_id (FK)  │     │ tenant_id (FK)  │
│ slug (UQ)       │     │ id (PK)         │     │ id (PK)         │
│ name            │     │ email (UQ)      │     │ name            │
│ status          │     │ password        │     │ code            │
│ subscription_   │     │ enabled         │     └─────────────────┘
│   plan          │     └─────────────────┘
│ configuration   │
│ branding        │
└─────────────────┘

┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  inspections    │     │  inspection_    │     │    hazards      │
├─────────────────┤     │    items        │     ├─────────────────┤
│ tenant_id (FK)  │     ├─────────────────┤     │ tenant_id (FK)  │
│ id (PK)         │◄────┤ inspection_id   │     │ id (PK)         │
│ title           │     │ id (PK)         │     │ title           │
│ status          │     │ question        │     │ severity        │
│ score           │     │ answer          │     │ status          │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## API Design

### REST API Conventions

```
GET    /api/v1/{resource}              # List all
GET    /api/v1/{resource}/{id}         # Get one
POST   /api/v1/{resource}              # Create
PUT    /api/v1/{resource}/{id}         # Update
DELETE /api/v1/{resource}/{id}         # Delete

GET    /api/v1/{resource}/{id}/{sub}   # Sub-resources
POST   /api/v1/{resource}/search       # Advanced search
GET    /api/v1/{resource}/stats        # Statistics
```

### Response Format

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-03-19T10:30:00Z"
}
```

### Error Format

```json
{
  "success": false,
  "error": {
    "code": "INSPECTION_NOT_FOUND",
    "message": "Inspection with ID 123 not found",
    "details": { ... }
  },
  "timestamp": "2026-03-19T10:30:00Z"
}
```

---

## Security

### Authentication Flow

```
┌──────────┐     POST /api/auth/login      ┌──────────┐
│  Client  │ ─────────────────────────────►│  Server  │
│          │                               │          │
│          │◄──────────────────────────────┤          │
│          │  { accessToken, refreshToken }│          │
│          │                               │          │
│          │───── API Call + Bearer ──────►│          │
│          │     Authorization: Bearer     │  Verify  │
│          │◄──────────────────────────────┤   JWT    │
└──────────┘                               └──────────┘
```

### Security Measures

- JWT tokens with short expiry (15 min access, 14 day refresh)
- Password hashing with BCrypt
- CSRF disabled for stateless JWT
- Tenant isolation at application level
- Audit logging of all sensitive operations
- Rate limiting on authentication endpoints

---

## Scalability Considerations

### Horizontal Scaling

- Stateless application design
- Externalized sessions (no server affinity)
- Database connection pooling
- Read replicas for reporting queries

### Performance Optimizations

- Caffeine caching for tenant configurations
- Database query optimization with indexes
- Async processing for notifications
- File storage CDN integration

### Monitoring

- Spring Boot Actuator endpoints
- Micrometer metrics
- Distributed tracing (OpenTelemetry)
- Health checks for all external dependencies

---

## Development Guidelines

### Adding a New Module

1. Create module directory structure following hexagonal architecture
2. Define domain entities and value objects
3. Create application ports (interfaces)
4. Implement domain services
5. Create JPA entities and repositories
6. Implement REST controllers
7. Add Flyway migrations
8. Write tests (unit, integration)

### Code Style

- **Language:** Kotlin with idiomatic patterns
- **Architecture:** Hexagonal/Clean Architecture
- **Testing:** JUnit 5, MockK, TestContainers
- **Documentation:** KDoc for all public APIs

---

## Deployment

### Docker Architecture

```dockerfile
# Multi-stage build
FROM gradle:8.14-jdk21 AS build
# ... build steps

FROM eclipse-temurin:21-jre
# ... runtime
EXPOSE 8080
```

### Environment Configuration

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  
app:
  jwt:
    access-secret: ${APP_JWT_ACCESS_SECRET}
    refresh-secret: ${APP_JWT_REFRESH_SECRET}
  storage:
    type: ${STORAGE_TYPE:s3} # s3 or local
    s3:
      bucket: ${S3_BUCKET}
      region: ${S3_REGION}
```

---

## Future Enhancements

### Short-term

- [ ] Complete WhatsApp state machine implementation
- [ ] Add more comprehensive unit tests
- [ ] Implement reporting PDF/Excel generators
- [ ] Add real-time notifications via WebSocket

### Long-term

- [ ] Machine learning for hazard prediction
- [ ] IoT device integration (sensors)
- [ ] Offline-first mobile app with sync
- [ ] Blockchain for audit trail immutability
- [ ] Computer vision for photo analysis

---

## Conclusion

SafeOps is designed as a modern, scalable, and maintainable platform for mining safety management. Its modular architecture allows for easy extension and customization while maintaining clean separation of concerns.

The multi-tenant design enables white-label deployments for different mining organizations while ensuring complete data isolation. The WhatsApp integration brings safety management directly to field officers, making compliance easier and more
accessible.

For questions or contributions, please refer to the project documentation or contact the development team.
