/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.exceptions

class AuthException(message: String) : RuntimeException(message)
class UserAlreadyExistsException(message: String) : RuntimeException(message)
class InvalidCredentialsException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)
class InvalidTokenException(message: String = "Invalid token") : RuntimeException(message)
class ExpiredTokenException(message: String = "Token has expired") : RuntimeException(message)