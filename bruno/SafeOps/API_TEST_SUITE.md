# SafeOps API Test Suite

## Overview

Comprehensive Bruno API test collection covering authentication, authorization, all endpoints, and security scenarios.

## Test Categories

### 1. Authentication Scenarios (`Auth/Scenarios/`)

| Test                             | Description           | Expected Result     |
|----------------------------------|-----------------------|---------------------|
| Login Valid Credentials          | Valid email/password  | 200 OK + tokens     |
| Login Invalid Password           | Wrong password        | 401 Unauthorized    |
| Login Non-existent User          | Unknown email         | 401 Unauthorized    |
| Login Empty Email                | Missing email         | 400 Bad Request     |
| Access Protected Without Token   | No auth header        | 401 Unauthorized    |
| Access With Invalid Token Format | Malformed token       | 401 Unauthorized    |
| Access With Malformed JWT        | Invalid JWT structure | 401 Unauthorized    |
| Refresh Token Valid              | Valid refresh token   | 200 OK + new tokens |
| Refresh Token Invalid            | Invalid refresh token | 401 Unauthorized    |

### 2. Role-Based Tests (`Auth/Roles/`)

| Test                              | Description                  | Expected Result          |
|-----------------------------------|------------------------------|--------------------------|
| Register as SUPER_ADMIN           | Privilege escalation attempt | Role filtered to OFFICER |
| Register as ADMIN                 | Privilege escalation attempt | Role filtered to OFFICER |
| OFFICER Access Admin Endpoints    | Unauthorized access          | 403 Forbidden            |
| SUPERVISOR Access Admin Endpoints | Unauthorized access          | 403 Forbidden            |

### 3. Auth Endpoints

| Endpoint                   | Methods           | Auth Required |
|----------------------------|-------------------|---------------|
| POST /api/v1/auth/register | Register new user | No            |
| POST /api/v1/auth/login    | Login             | No            |
| POST /api/v1/auth/refresh  | Refresh token     | No            |
| GET /api/auth/users        | List users        | Yes (Admin)   |
| GET /api/auth/users/me     | Current user      | Yes           |

### 4. Core Module Endpoints

| Endpoint                 | Methods      | Auth Required | Roles             |
|--------------------------|--------------|---------------|-------------------|
| GET /api/core/mines      | List mines   | Yes           | ADMIN, SUPERVISOR |
| POST /api/core/mines     | Create mine  | Yes           | ADMIN, SUPERVISOR |
| GET /api/core/mines/{id} | Get mine     | Yes           | ADMIN, SUPERVISOR |
| GET /api/core/sites      | List sites   | Yes           | ADMIN, SUPERVISOR |
| POST /api/core/sites     | Create site  | Yes           | ADMIN, SUPERVISOR |
| GET /api/core/shafts     | List shafts  | Yes           | ADMIN, SUPERVISOR |
| POST /api/core/shafts    | Create shaft | Yes           | ADMIN, SUPERVISOR |
| GET /api/core/areas      | List areas   | Yes           | ADMIN, SUPERVISOR |
| POST /api/core/areas     | Create area  | Yes           | ADMIN, SUPERVISOR |

### 5. Dashboard Endpoints

| Endpoint                              | Methods            | Auth Required |
|---------------------------------------|--------------------|---------------|
| GET /api/dashboard/stats              | Dashboard stats    | Yes           |
| GET /api/dashboard/recent-inspections | Recent inspections | Yes           |

### 6. Inspections Endpoints

| Endpoint                     | Methods           | Auth Required | Roles                      |
|------------------------------|-------------------|---------------|----------------------------|
| GET /api/v1/inspections      | List inspections  | Yes           | All authenticated          |
| POST /api/v1/inspections     | Create inspection | Yes           | ADMIN, SUPERVISOR, OFFICER |
| GET /api/v1/inspections/{id} | Get inspection    | Yes           | All authenticated          |

### 7. Hazards Endpoints

| Endpoint                         | Methods       | Auth Required | Roles                      |
|----------------------------------|---------------|---------------|----------------------------|
| GET /api/hazards                 | List hazards  | Yes           | All authenticated          |
| POST /api/hazards                | Create hazard | Yes           | ADMIN, SUPERVISOR, OFFICER |
| GET /api/hazards?status={status} | Filtered list | Yes           | All authenticated          |

### 8. Safety Endpoints

| Endpoint                 | Methods       | Auth Required |
|--------------------------|---------------|---------------|
| GET /api/safety/events   | List events   | Yes           |
| POST /api/safety/events  | Create event  | Yes           |
| GET /api/safety/alerts   | List alerts   | Yes           |
| GET /api/safety/reports  | List reports  | Yes           |
| POST /api/safety/reports | Create report | Yes           |

### 9. Templates Endpoints

| Endpoint               | Methods              | Auth Required |
|------------------------|----------------------|---------------|
| GET /api/v1/templates  | List templates       | Yes           |
| POST /api/v1/templates | Create template      | Yes (Admin)   |
| GET /api/templates     | List templates (alt) | Yes           |

### 10. Tenants Endpoints (Admin Only)

| Endpoint                                  | Methods          | Auth Required | Roles              |
|-------------------------------------------|------------------|---------------|--------------------|
| GET /api/admin/tenants                    | List tenants     | Yes           | SUPER_ADMIN, ADMIN |
| POST /api/admin/tenants                   | Provision tenant | Yes           | SUPER_ADMIN        |
| PUT /api/admin/tenants/{id}/configuration | Update config    | Yes           | SUPER_ADMIN, ADMIN |
| PUT /api/admin/tenants/{id}/branding      | Update branding  | Yes           | SUPER_ADMIN, ADMIN |

### 11. WhatsApp Webhooks

| Endpoint                   | Methods | Auth Required |
|----------------------------|---------|---------------|
| POST /api/whatsapp/webhook | Webhook | No            |

### 12. Security Tests

| Test               | Description                  |
|--------------------|------------------------------|
| SQL Injection Test | Attempt SQL injection        |
| XSS Test           | Attempt cross-site scripting |

## Environment Variables

```
baseUrl: https://safeops-1.onrender.com
tenantId: 1
tenantSlug: default
accessToken: (auto-populated after login)
refreshToken: (auto-populated after login)
```

## Running Tests

### All Tests

```bash
cd bruno/SafeOps
bruno run
```

### Specific Folder

```bash
bruno run Auth/Scenarios
bruno run Auth/Roles
bruno run Security
```

### Single Test

```bash
bruno run "Auth/Scenarios/Login Valid Credentials"
```

## Test Execution Order

1. **Authentication Tests** - Verify login/logout functionality
2. **Role-Based Tests** - Verify RBAC enforcement
3. **Core Module Tests** - Test CRUD operations
4. **Security Tests** - Verify security protections

## Expected Results Summary

| Category       | Pass Criteria                                     |
|----------------|---------------------------------------------------|
| Authentication | All valid creds return 200, invalid return 401    |
| Authorization  | Role restrictions enforced (403 for unauthorized) |
| Core Endpoints | All CRUD operations work for authorized roles     |
| Security       | SQL injection, XSS attempts blocked/sanitized     |

## Security Coverage

- ✅ Privilege escalation prevented
- ✅ Role-based access enforced
- ✅ SQL injection protection
- ✅ XSS protection
- ✅ JWT token validation
- ✅ Rate limiting (429 responses)

## Notes

- Tests auto-save tokens after successful login
- Tests use environment variables for base URL
- Some tests require pre-existing data (e.g., mine ID = 1)
- Admin tests require SUPER_ADMIN or ADMIN role
