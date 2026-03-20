# Quick Fix for Swagger/OpenAPI

## Issue

The `@Profile("!prod")` annotation was preventing the OpenAPI bean from being created in production.

## Fix

Removed the `@Profile("!prod")` annotation from `OpenApiConfig.kt`.

## File Modified

- `src/main/kotlin/com/zama/safeops/config/OpenApiConfig.kt`

## Git Commands

```bash
cd "D:\Workspace\Projects\Bright\code v2\safeops"
git add .
git commit -m "fix: Enable Swagger/OpenAPI in production

Remove @Profile("!prod") annotation that was disabling OpenAPI bean.
This fixes the 500 error on /v3/api-docs endpoint."
git push
```

## After Deployment

Test these URLs:

- https://safeops-1.onrender.com/v3/api-docs
- https://safeops-1.onrender.com/swagger-ui.html
