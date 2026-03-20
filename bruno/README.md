# SafeOps API Testing with Bruno

This directory contains a [Bruno](https://www.usebruno.com/) collection for testing the SafeOps API.

## Setup

### 1. Install Bruno

Download and install Bruno from: https://www.usebruno.com/downloads

### 2. Open the Collection

1. Open Bruno
2. Click "Open Collection"
3. Select the `bruno/SafeOps` folder

### 3. Select Environment

Choose your environment from the dropdown:

| Environment    | URL                            | Description            |
|----------------|--------------------------------|------------------------|
| **local**      | http://localhost:8080          | Development server     |
| **staging**    | https://staging-api.safeops.io | Staging server         |
| **production** | https://safeops-1.onrender.com | Live production server |

## Quick Start

### Authentication Flow

1. **Register a user** (first time only):
    - Run `Auth/Register`
    - Or use pre-seeded admin account

2. **Login**:
    - Run `Auth/Login`
    - Tokens are automatically saved to environment variables

3. **Test protected endpoints**:
    - All subsequent requests use the saved access token automatically

### Multi-Tenancy Testing

The collection includes `X-Tenant-ID` and `X-Tenant-Slug` headers. To test different tenants:

1. Create a new environment or modify existing:
   ```
   tenantId: 2
   tenantSlug: acme-mining
   ```

2. All requests will now target that tenant

## Test Workflows

### 1. Complete Inspection Workflow

```
1. Auth/Login (as OFFICER)
2. Core/List Mines
3. Core/Create Area (if needed)
4. Inspections/Create Inspection
5. Inspections/Get Inspection (view questions)
6. Inspections/Submit Inspection
7. Auth/Login (as SUPERVISOR)
8. Inspections/Get Inspection (review)
```

### 2. WhatsApp Inspection Flow

Test the WhatsApp bot without actual WhatsApp:

```
1. WhatsApp/Webhook - Start Inspection
   → Check logs for "Starting new inspection" response

2. WhatsApp/Webhook - Answer Questions
   → Simulate user responses

3. WhatsApp/Webhook - With Photo
   → Simulate photo uploads
```

### 3. White-Label Tenant Setup (Super Admin)

```
1. Auth/Login (as SUPER_ADMIN)
2. Tenants/Provision Tenant
3. Tenants/Update Configuration (enable features)
4. Tenants/Update Branding (customize)
5. Switch to new tenant environment
6. Create users, mines, etc. in new tenant
```

## Request Structure

### Authentication

All protected endpoints require Bearer token (set automatically after login):

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### Headers

Every request includes:

```
Content-Type: application/json
X-Tenant-ID: 1
X-Tenant-Slug: default
```

### Response Format

**Success:**

```json
{
  "success": true,
  "message": "Operation completed",
  "data": { ... }
}
```

**Error:**

```json
{
  "success": false,
  "error": {
    "code": "INSPECTION_NOT_FOUND",
    "message": "Inspection not found"
  }
}
```

## Available Endpoints

### Auth

| Method | Endpoint             | Description          |
|--------|----------------------|----------------------|
| POST   | `/api/auth/login`    | Authenticate user    |
| POST   | `/api/auth/register` | Create new user      |
| POST   | `/api/auth/refresh`  | Refresh access token |

### Tenants (Super Admin)

| Method | Endpoint                                | Description          |
|--------|-----------------------------------------|----------------------|
| GET    | `/api/admin/tenants`                    | List all tenants     |
| POST   | `/api/admin/tenants`                    | Provision new tenant |
| PUT    | `/api/admin/tenants/{id}/configuration` | Update settings      |
| PUT    | `/api/admin/tenants/{id}/branding`      | Update branding      |
| PUT    | `/api/admin/tenants/{id}/whatsapp`      | Configure WhatsApp   |

### Core (Location Management)

| Method | Endpoint           | Description  |
|--------|--------------------|--------------|
| GET    | `/api/core/mines`  | List mines   |
| POST   | `/api/core/mines`  | Create mine  |
| GET    | `/api/core/sites`  | List sites   |
| POST   | `/api/core/sites`  | Create site  |
| GET    | `/api/core/shafts` | List shafts  |
| POST   | `/api/core/shafts` | Create shaft |
| GET    | `/api/core/areas`  | List areas   |
| POST   | `/api/core/areas`  | Create area  |

### Inspections

| Method | Endpoint                       | Description          |
|--------|--------------------------------|----------------------|
| GET    | `/api/inspections`             | List inspections     |
| POST   | `/api/inspections`             | Create inspection    |
| GET    | `/api/inspections/{id}`        | Get inspection       |
| POST   | `/api/inspections/{id}/submit` | Submit inspection    |
| GET    | `/api/inspections/{id}/items`  | Get inspection items |

### WhatsApp

| Method | Endpoint                | Description              |
|--------|-------------------------|--------------------------|
| POST   | `/api/whatsapp/webhook` | Incoming message webhook |

## Testing Tips

### 1. Chain Requests

Use Bruno's scripting to chain requests:

```javascript
// In post-response script
const json = res.getBody();
if (json.data?.id) {
  bru.setVar('inspectionId', json.data.id);
}
```

Then reference in URL: `{{baseUrl}}/api/inspections/{{inspectionId}}`

### 2. Test Different Roles

Create environment variables for different users:

```
adminEmail: admin@safeops.io
adminPassword: admin123
officerEmail: officer@safeops.io
officerPassword: officer123
```

### 3. Validate Responses

Add assertions in post-response scripts:

```javascript
// Assert status code
assert(res.getStatus() === 200);

// Assert response structure
const json = res.getBody();
assert(json.success === true);
assert(json.data.accessToken !== undefined);
```

### 4. Performance Testing

Use Bruno CLI for load testing:

```bash
# Install CLI
npm install -g @usebruno/cli

# Run collection
bru run --env local
```

## Troubleshooting

### 401 Unauthorized

- Run `Auth/Login` first
- Check if token expired (run `Auth/Refresh Token`)

### 403 Forbidden

- User doesn't have required role
- Try logging in as different user

### 404 Not Found

- Entity doesn't exist in current tenant
- Verify `tenantId` environment variable

### Connection Refused

- Backend not running
- Check `baseUrl` in environment

## Environment Variables Reference

| Variable       | Description                  | Example                                                     |
|----------------|------------------------------|-------------------------------------------------------------|
| `baseUrl`      | API base URL                 | `http://localhost:8080` or `https://safeops-1.onrender.com` |
| `tenantId`     | Current tenant ID            | `1`                                                         |
| `tenantSlug`   | Current tenant slug          | `default`                                                   |
| `accessToken`  | JWT access token (auto-set)  | `eyJhbG...`                                                 |
| `refreshToken` | JWT refresh token (auto-set) | `eyJhbG...`                                                 |

## 🚀 Testing Production (Render)

The service is live at: **https://safeops-1.onrender.com**

### Quick Test Steps:

1. **Select `production` environment** in Bruno dropdown
2. **Run Auth/Register** to create a test user (if no users exist)
3. **Run Auth/Login** - tokens will be auto-saved
4. **Test Core endpoints**:
    - `Core/Create Mine` → Creates a new mine
    - `Core/List Mines` → Lists all mines
5. **Test Inspections**:
    - `Inspections/Create Inspection` → Creates inspection
    - `Inspections/List Inspections` → Lists inspections

### ⚠️ Production Notes:

- First request may be **slow** (Render free tier spins down after inactivity)
- Database is **fresh** (no seeded data) - you'll need to create mines/sites first
- WhatsApp webhooks won't work (no WhatsApp Business API configured)
- File uploads go to local storage (not persisted across restarts)

## Additional Resources

- [Bruno Documentation](https://docs.usebruno.com/)
- [SafeOps API Documentation](../ARCHITECTURE.md)
- [Postman to Bruno Migration](https://docs.usebruno.com/get-started/import-collections)
