/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.storage.application.ports

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.io.InputStream

/**
 * Port for file storage operations.
 * Abstracts the underlying storage mechanism (S3, local filesystem, etc.)
 */
interface StoragePort {

    /**
     * Store a file and return its storage key.
     */
    fun store(
        tenantId: TenantId,
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        metadata: Map<String, String> = emptyMap()
    ): StoredFile

    /**
     * Store bytes directly.
     */
    fun storeBytes(
        tenantId: TenantId,
        fileName: String,
        contentType: String,
        bytes: ByteArray,
        metadata: Map<String, String> = emptyMap()
    ): StoredFile

    /**
     * Retrieve a file by its key.
     */
    fun retrieve(fileKey: String): RetrievedFile?

    /**
     * Generate a pre-signed URL for temporary access.
     */
    fun generateUrl(fileKey: String, expirySeconds: Int = 3600): String

    /**
     * Delete a file.
     */
    fun delete(fileKey: String): Boolean

    /**
     * Check if a file exists.
     */
    fun exists(fileKey: String): Boolean

    /**
     * List files in a tenant's namespace.
     */
    fun listFiles(tenantId: TenantId, prefix: String? = null): List<StoredFile>
}

data class StoredFile(
    val key: String,
    val originalFileName: String,
    val contentType: String,
    val size: Long,
    val url: String,
    val metadata: Map<String, String>,
    val uploadedAt: Long
)

data class RetrievedFile(
    val key: String,
    val originalFileName: String,
    val contentType: String,
    val size: Long,
    val inputStream: InputStream,
    val metadata: Map<String, String>
)

/**
 * File types supported by the system.
 */
enum class FileCategory {
    INSPECTION_PHOTO,
    HAZARD_EVIDENCE,
    PROFILE_PICTURE,
    REPORT_PDF,
    TEMPLATE_ATTACHMENT,
    EXPORT_DATA
}

/**
 * Validates files before storage.
 */
interface FileValidator {
    fun validate(fileName: String, contentType: String, size: Long, category: FileCategory)
}

class FileValidationException(message: String) : RuntimeException(message)
