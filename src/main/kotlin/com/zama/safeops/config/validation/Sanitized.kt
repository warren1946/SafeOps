/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config.validation

import com.zama.safeops.config.InputSanitizer
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * Annotation to mark a field that should be checked for XSS and injection attacks.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [SanitizedValidator::class])
annotation class Sanitized(
    val message: String = "Field contains potentially malicious content",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val checkSql: Boolean = true,
    val checkCommand: Boolean = true,
    val checkPathTraversal: Boolean = true,
    val checkXss: Boolean = true,
    val maxLength: Int = 10000,
    val allowSpaces: Boolean = false
)

class SanitizedValidator : ConstraintValidator<Sanitized, String?> {

    private var checkSql: Boolean = true
    private var checkCommand: Boolean = true
    private var checkPathTraversal: Boolean = true
    private var maxLength: Int = 10000

    override fun initialize(constraintAnnotation: Sanitized) {
        checkSql = constraintAnnotation.checkSql
        checkCommand = constraintAnnotation.checkCommand
        checkPathTraversal = constraintAnnotation.checkPathTraversal
        maxLength = constraintAnnotation.maxLength
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true

        // Check length
        if (value.length > maxLength) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Field exceeds maximum length of $maxLength characters")
                .addConstraintViolation()
            return false
        }

        // Check for null bytes
        if (InputSanitizer.containsNullBytes(value)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Field contains invalid characters")
                .addConstraintViolation()
            return false
        }

        // Check for SQL injection
        if (checkSql && InputSanitizer.containsSqlInjection(value)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Field contains potentially malicious content")
                .addConstraintViolation()
            return false
        }

        // Check for command injection
        if (checkCommand && InputSanitizer.containsCommandInjection(value)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Field contains potentially malicious content")
                .addConstraintViolation()
            return false
        }

        // Check for path traversal
        if (checkPathTraversal && InputSanitizer.containsPathTraversal(value)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Field contains invalid characters")
                .addConstraintViolation()
            return false
        }

        return true
    }
}

/**
 * Annotation to validate safe ID format (alphanumeric, dash, underscore only).
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [SafeIdValidator::class])
annotation class SafeId(
    val message: String = "ID contains invalid characters",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val allowSpaces: Boolean = false
)

class SafeIdValidator : ConstraintValidator<SafeId, String?> {

    private var allowSpaces: Boolean = false

    override fun initialize(constraintAnnotation: SafeId) {
        allowSpaces = constraintAnnotation.allowSpaces
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true
        return InputSanitizer.isAlphanumeric(value, allowSpaces = allowSpaces, allowDashUnderscore = true)
    }
}

/**
 * Annotation to validate email with strict format.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [StrictEmailValidator::class])
annotation class StrictEmail(
    val message: String = "Email format is invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class StrictEmailValidator : ConstraintValidator<StrictEmail, String?> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true
        return InputSanitizer.isValidEmail(value)
    }
}
