# Input Sanitization & Validation Implementation

**Date:** 2026-04-09  
**Scope:** Comprehensive input sanitization, payload size limits, and injection attack prevention

---

## 🔒 Security Layers Implemented

### 1. Request-Level Validation Filter (`InputValidationFilter`)

**Order:** `@Order(Ordered.HIGHEST_PRECEDENCE + 1)` - Runs early in the filter chain

**Validates:**

- ✅ **Payload Size:** Maximum 10MB (configurable)
- ✅ **JSON Nesting Depth:** Maximum 20 levels (prevents stack overflow)
- ✅ **Collection Sizes:** Maximum 1000 items per array
- ✅ **String Lengths:** Maximum 10,000 characters per field
- ✅ **Null Bytes:** Rejects all null byte injection attempts
- ✅ **SQL Injection:** Detects common SQL injection patterns
- ✅ **Command Injection:** Detects shell/command injection attempts
- ✅ **Path Traversal:** Blocks `../` and similar patterns
- ✅ **Query Parameters:** Validates length and content
- ✅ **HTTP Headers:** Validates length and blocks null bytes

**Configuration (application.yml):**

```yaml
app:
  security:
    validation:
      max-request-size: 10485760        # 10MB
      max-string-length: 10000
      max-collection-size: 1000
      max-nesting-depth: 20
      enable-xss-sanitization: true
      enable-sql-injection-check: true
      enable-command-injection-check: true
```

### 2. Server-Level Size Limits (`application.yml`)

```yaml
server:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  tomcat:
    max-http-form-post-size: 10MB
    max-swallow-size: 10MB
```

### 3. DTO-Level Validation

All request DTOs now include:

- ✅ **@NotBlank** / **@NotNull** - Required field validation
- ✅ **@Size** - Length constraints
- ✅ **@Min** / **@Max** - Numeric range validation
- ✅ **@Pattern** - Regex format validation
- ✅ **@Sanitized** - Injection detection
- ✅ **@StrictEmail** - Strict email format validation
- ✅ **Init blocks** - Additional programmatic validation

### 4. Custom Validation Annotations

#### @Sanitized

Checks for malicious content in string fields:

```kotlin
@field:Sanitized(
    maxLength = 5000,
    checkSql = true,
    checkCommand = true,
    checkPathTraversal = true,
    allowSpaces = true
)
```

#### @StrictEmail

Validates email format strictly:

```kotlin
@field:StrictEmail
val email: String
```

#### @SafeId

Validates safe identifier format:

```kotlin
@field:SafeId(allowSpaces = false)
val code: String
```

### 5. Global Exception Handler

Enhanced error handling for:

- ✅ Malformed JSON
- ✅ Invalid data types
- ✅ Missing required parameters
- ✅ Type mismatches
- ✅ Payload too large
- ✅ Unsupported media types
- ✅ Validation failures

---

## 📋 Validation Rules by Entity

### Authentication (AuthDtos.kt)

| Field            | Constraints                                                         |
|------------------|---------------------------------------------------------------------|
| **email**        | Not blank, valid format, max 254 chars, strict validation           |
| **password**     | 8-128 chars, must contain uppercase, lowercase, digit, special char |
| **refreshToken** | Max 4096 chars                                                      |
| **roles**        | Max 10 roles, uppercase with underscores only                       |

### Hazards (HazardDtos.kt)

| Field           | Constraints                                         |
|-----------------|-----------------------------------------------------|
| **title**       | Not blank, max 200 chars, sanitized, allows spaces  |
| **description** | Not blank, max 5000 chars, sanitized, allows spaces |
| **locationId**  | Positive number if provided                         |
| **search**      | Max 200 chars, SQL injection check                  |

### Inspections (InspectionsDtos.kt)

| Field           | Constraints                         |
|-----------------|-------------------------------------|
| **title**       | Not blank, max 200 chars, sanitized |
| **targetId**    | Required, positive                  |
| **inspectorId** | Required, positive                  |
| **templateId**  | Required, positive                  |
| **comment**     | Max 5000 chars, sanitized           |
| **date range**  | From date cannot be after to date   |

### Safety Events (SafetyDtos.kt)

| Field            | Constraints                          |
|------------------|--------------------------------------|
| **description**  | Not blank, max 5000 chars, sanitized |
| **message**      | Not blank, max 2000 chars, sanitized |
| **locationId**   | Required, positive                   |
| **reporterId**   | Required, positive                   |
| **period dates** | YYYY-MM-DD format, start ≤ end       |

### Core Entities (CoreDtos.kt)

| Field                     | Constraints                                                       |
|---------------------------|-------------------------------------------------------------------|
| **name**                  | Not blank, max 100 chars, sanitized, allows spaces                |
| **code**                  | 3-20 chars, uppercase letters, numbers, underscores, hyphens only |
| **mineId/siteId/shaftId** | Required, positive                                                |

### Templates (TemplateRequest.kt)

| Field             | Constraints                         |
|-------------------|-------------------------------------|
| **name**          | Not blank, max 200 chars, sanitized |
| **description**   | Max 2000 chars, sanitized           |
| **category**      | Max 50 chars, sanitized             |
| **question text** | Not blank, max 500 chars, sanitized |
| **options**       | Max 50 options, each max 200 chars  |
| **orderIndex**    | 0-999                               |

---

## 🛡️ Injection Attack Prevention

### SQL Injection Detection

Detects patterns like:

- `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `DROP`, `CREATE`, `ALTER`
- `UNION`, `MERGE`, `TRUNCATE`, `EXEC`
- Comment sequences: `--`, `#`, `/* */`
- Boolean-based: `OR 1=1`, `AND 1=1`
- Time-based: `WAITFOR DELAY`
- File operations: `INTO OUTFILE`, `LOAD_FILE`
- `BULK INSERT`

### Command Injection Detection

Detects patterns like:

- Shell executions: `sh -c`, `bash -c`, `cmd /c`
- Dangerous commands: `rm`, `del`, `format`, `shutdown`
- Command chaining: `;`, `&&`, `\|`
- Command substitution: `` `command` ``, `$(command)`
- Network tools: `nc`, `netcat`, `wget`, `curl`

### Path Traversal Detection

Blocks:

- `../`
- `..\`
- `..`

### XSS Prevention

Detects:

- `<script>` tags
- JavaScript protocol: `javascript:`
- Event handlers: `onerror=`, `onclick=`, etc.
- `<iframe>`, `<object>`, `<embed>` tags

---

## 📊 Error Codes

| Code             | Description            |
|------------------|------------------------|
| `VALIDATION_001` | Bean validation failed |
| `VALIDATION_002` | Invalid input format   |
| `VALIDATION_003` | Payload too large      |

---

## 🔧 Usage Example

### Before (Insecure)

```kotlin
data class CreateHazardRequest(
    val title: String,  // No validation!
    val description: String
)
```

### After (Secure)

```kotlin
data class CreateHazardRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 200, message = "Title must not exceed 200 characters")
    @field:Sanitized(maxLength = 200, allowSpaces = true)
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = 5000, message = "Description must not exceed 5000 characters")
    @field:Sanitized(maxLength = 5000, allowSpaces = true)
    val description: String
)
```

---

## ✅ Testing

All existing tests pass with the new validation layer:

```bash
./gradlew test
```

---

## 📁 Files Modified/Created

| File                          | Purpose                            |
|-------------------------------|------------------------------------|
| `SecurityValidationConfig.kt` | Validation utilities and sanitizer |
| `InputValidationFilter.kt`    | Request-level validation filter    |
| `Sanitized.kt`                | Custom validation annotations      |
| `GlobalExceptionHandler.kt`   | Enhanced exception handling        |
| `ErrorCodes.kt`               | Added `PAYLOAD_TOO_LARGE`          |
| `AuthDtos.kt`                 | Enhanced validation                |
| `HazardDtos.kt`               | Enhanced validation                |
| `InspectionsDtos.kt`          | Enhanced validation                |
| `SafetyDtos.kt`               | Enhanced validation                |
| `CoreDtos.kt`                 | Enhanced validation                |
| `TemplateRequest.kt`          | Enhanced validation                |
| `application.yml`             | Size limits configuration          |

---

## 🎯 Security Checklist

- [x] Payload size limits (10MB)
- [x] JSON nesting depth limits (20 levels)
- [x] Collection size limits (1000 items)
- [x] String length limits per field
- [x] Null byte rejection
- [x] SQL injection detection
- [x] Command injection detection
- [x] Path traversal protection
- [x] XSS vector detection
- [x] Email format strict validation
- [x] Required field validation
- [x] Pattern/regex validation
- [x] Numeric range validation
- [x] Date range validation
- [x] Custom sanitization annotations
- [x] Comprehensive error messages
- [x] Request body caching for re-reading
- [x] Query parameter validation
- [x] HTTP header validation
