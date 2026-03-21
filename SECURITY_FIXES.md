# Security Fixes - SafeOps API

## 🔒 Critical Security Issues Fixed

### 1. PRIVILEGE ESCALATION (CRITICAL) ✅ FIXED

**Issue:** Users could register with `SUPER_ADMIN` or `ADMIN` roles, gaining unauthorized elevated privileges.

**Fix:** Modified `UserService.register()` to:

- Filter out restricted roles (`SUPER_ADMIN`, `ADMIN`) during self-registration
- Assign default `OFFICER` role if no valid roles provided
- Log attempts to assign restricted roles

**File:** `src/main/kotlin/com/zama/safeops/modules/auth/application/services/UserService.kt`

---

### 2. BROKEN ACCESS CONTROL (HIGH) ✅ FIXED

**Issue:** `@PreAuthorize` annotations on admin endpoints weren't being enforced because method security wasn't enabled.

**Fix:** Added `@EnableMethodSecurity(prePostEnabled = true)` to `SecurityConfig` to enable method-level security annotations.

**File:** `src/main/kotlin/com/zama/safeops/config/SecurityConfig.kt`

---

### 3. MISSING SECURITY HEADERS (LOW) ✅ FIXED

**Issue:** Missing `Content-Security-Policy` and other security headers.

**Fix:** Created `SecurityHeadersConfig` to add:

- `Content-Security-Policy`
- `Referrer-Policy`
- `Permissions-Policy`
- Server header masking

**File:** `src/main/kotlin/com/zama/safeops/config/SecurityHeadersConfig.kt` (NEW)

---

## 📋 Files Modified

| File                                               | Change                          |
|----------------------------------------------------|---------------------------------|
| `config/SecurityConfig.kt`                         | Added `@EnableMethodSecurity`   |
| `config/SecurityHeadersConfig.kt`                  | NEW - Security headers filter   |
| `modules/auth/application/services/UserService.kt` | Role validation in registration |

## 🧪 Testing After Deployment

### Test 1: Privilege Escalation Prevention

```bash
# Try to register as SUPER_ADMIN (should fail/be ignored)
curl -X POST https://safeops-1.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123","roles":["SUPER_ADMIN"]}'

# Expected: User registered with OFFICER role (not SUPER_ADMIN)
```

### Test 2: Admin Endpoint Protection

```bash
# Login as OFFICER
curl -X POST https://safeops-1.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"officer@test.com","password":"password123"}'

# Try to access admin endpoint (should get 403)
curl -X GET https://safeops-1.onrender.com/api/admin/tenants \
  -H "Authorization: Bearer OFFICER_TOKEN"

# Expected: 403 Forbidden
```

### Test 3: Security Headers

```bash
curl -I https://safeops-1.onrender.com/health

# Expected headers:
# X-Content-Type-Options: nosniff
# X-Frame-Options: DENY
# Content-Security-Policy: default-src 'self' ...
```

## ✅ Build Status

BUILD SUCCESSFUL

## 🚀 Deployment

```bash
git add .
git commit -m "security: Fix critical privilege escalation and access control issues

- Add role validation to prevent SUPER_ADMIN/ADMIN self-registration
- Enable method security with @EnableMethodSecurity
- Add security headers filter for CSP and other headers
- Harden authentication against privilege escalation attacks

Security Fixes:
- CRITICAL: Privilege escalation via registration
- HIGH: Broken access control on admin endpoints  
- LOW: Missing security headers

Build: SUCCESSFUL"
git push
```
