# Additional Swagger/API Documentation Fixes

## Changes Made

### 1. application.yml

Enhanced SpringDoc OpenAPI configuration:

- Added `config-url` and `url` properties for Swagger UI
- Added default media types for proper content negotiation

### 2. WebMvcConfig.kt

Excluded Swagger and API docs paths from interceptors:

- Rate limiting interceptor
- Logging interceptor
- RBAC interceptor

This prevents these paths from being blocked by authentication/authorization checks.

### 3. RateLimitConfig.kt

Added Swagger paths to the PUBLIC bucket type:

- `/swagger-ui`
- `/v3/api-docs`

This ensures these endpoints aren't rate-limited aggressively.

## Files Modified

1. `src/main/resources/application.yml`
2. `src/main/kotlin/com/zama/safeops/config/WebMvcConfig.kt`
3. `src/main/kotlin/com/zama/safeops/config/RateLimitConfig.kt`

## Deployment

These changes need to be committed and pushed to trigger a new Render deployment.
