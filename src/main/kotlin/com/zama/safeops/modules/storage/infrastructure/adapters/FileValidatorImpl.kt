/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.storage.infrastructure.adapters

import com.zama.safeops.modules.storage.application.ports.FileCategory
import com.zama.safeops.modules.storage.application.ports.FileValidationException
import com.zama.safeops.modules.storage.application.ports.FileValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FileValidatorImpl(
    @Value("\${app.storage.max-file-size:10485760}") private val maxFileSize: Long = 10 * 1024 * 1024 // 10MB
) : FileValidator {

    private val allowedContentTypes = mapOf(
        FileCategory.INSPECTION_PHOTO to setOf("image/jpeg", "image/png", "image/webp"),
        FileCategory.HAZARD_EVIDENCE to setOf("image/jpeg", "image/png", "image/webp", "video/mp4"),
        FileCategory.PROFILE_PICTURE to setOf("image/jpeg", "image/png"),
        FileCategory.REPORT_PDF to setOf("application/pdf"),
        FileCategory.TEMPLATE_ATTACHMENT to setOf(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ),
        FileCategory.EXPORT_DATA to setOf("application/json", "text/csv", "application/zip")
    )

    private val maxSizes = mapOf(
        FileCategory.INSPECTION_PHOTO to 5 * 1024 * 1024L,      // 5MB
        FileCategory.HAZARD_EVIDENCE to 10 * 1024 * 1024L,     // 10MB
        FileCategory.PROFILE_PICTURE to 2 * 1024 * 1024L,      // 2MB
        FileCategory.REPORT_PDF to 20 * 1024 * 1024L,          // 20MB
        FileCategory.TEMPLATE_ATTACHMENT to 10 * 1024 * 1024L, // 10MB
        FileCategory.EXPORT_DATA to 50 * 1024 * 1024L          // 50MB
    )

    override fun validate(fileName: String, contentType: String, size: Long, category: FileCategory) {
        // Validate size
        val maxSize = maxSizes[category] ?: maxFileSize
        if (size > maxSize) {
            throw FileValidationException(
                "File size ${formatSize(size)} exceeds maximum allowed size ${formatSize(maxSize)} for category $category"
            )
        }

        // Validate content type
        val allowedTypes = allowedContentTypes[category]
        if (allowedTypes != null && contentType !in allowedTypes) {
            throw FileValidationException(
                "Content type '$contentType' is not allowed for category $category. Allowed types: ${allowedTypes.joinToString()}"
            )
        }

        // Validate file extension
        val extension = fileName.substringAfterLast(".", "").lowercase()
        val allowedExtensions = getAllowedExtensions(category)
        if (extension !in allowedExtensions) {
            throw FileValidationException(
                "File extension '.$extension' is not allowed for category $category"
            )
        }
    }

    private fun getAllowedExtensions(category: FileCategory): Set<String> = when (category) {
        FileCategory.INSPECTION_PHOTO -> setOf("jpg", "jpeg", "png", "webp")
        FileCategory.HAZARD_EVIDENCE -> setOf("jpg", "jpeg", "png", "webp", "mp4")
        FileCategory.PROFILE_PICTURE -> setOf("jpg", "jpeg", "png")
        FileCategory.REPORT_PDF -> setOf("pdf")
        FileCategory.TEMPLATE_ATTACHMENT -> setOf("pdf", "doc", "docx")
        FileCategory.EXPORT_DATA -> setOf("json", "csv", "zip")
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)}MB"
            bytes >= 1024 -> "${bytes / 1024}KB"
            else -> "${bytes}B"
        }
    }
}
