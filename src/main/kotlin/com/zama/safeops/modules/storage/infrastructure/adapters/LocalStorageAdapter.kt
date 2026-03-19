/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.storage.infrastructure.adapters

import com.zama.safeops.modules.storage.application.ports.RetrievedFile
import com.zama.safeops.modules.storage.application.ports.StoragePort
import com.zama.safeops.modules.storage.application.ports.StoredFile
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream

/**
 * Local filesystem implementation of storage port.
 * Useful for development and small deployments.
 */
@Component
class LocalStorageAdapter(
    @Value("\${app.storage.local.path:./uploads}") private val basePath: String,
    @Value("\${app.storage.local.base-url:http://localhost:8080/uploads}") private val baseUrl: String
) : StoragePort {

    private val storagePath: Path = Paths.get(basePath).toAbsolutePath().normalize()

    init {
        Files.createDirectories(storagePath)
    }

    override fun store(
        tenantId: TenantId,
        fileName: String,
        contentType: String,
        inputStream: InputStream,
        metadata: Map<String, String>
    ): StoredFile {
        val key = generateKey(tenantId, fileName)
        val targetPath = storagePath.resolve(key)

        Files.createDirectories(targetPath.parent)

        val size = Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

        // Store metadata
        val metadataPath = targetPath.resolveSibling("${targetPath.fileName}.meta")
        Files.write(metadataPath, metadata.entries.joinToString("\n") { "${it.key}=${it.value}" }.toByteArray())

        return StoredFile(
            key = key,
            originalFileName = fileName,
            contentType = contentType,
            size = size,
            url = "$baseUrl/$key",
            metadata = metadata,
            uploadedAt = System.currentTimeMillis()
        )
    }

    override fun storeBytes(
        tenantId: TenantId,
        fileName: String,
        contentType: String,
        bytes: ByteArray,
        metadata: Map<String, String>
    ): StoredFile {
        return store(tenantId, fileName, contentType, bytes.inputStream(), metadata)
    }

    override fun retrieve(fileKey: String): RetrievedFile? {
        val filePath = storagePath.resolve(fileKey)
        if (!filePath.exists()) return null

        val metadataPath = filePath.resolveSibling("${filePath.fileName}.meta")
        val metadata = if (metadataPath.exists()) {
            Files.readAllLines(metadataPath).associate {
                val parts = it.split("=", limit = 2)
                parts[0] to (parts.getOrNull(1) ?: "")
            }
        } else emptyMap()

        return RetrievedFile(
            key = fileKey,
            originalFileName = metadata["originalFileName"] ?: filePath.fileName.toString(),
            contentType = metadata["contentType"] ?: "application/octet-stream",
            size = filePath.fileSize(),
            inputStream = filePath.inputStream(),
            metadata = metadata
        )
    }

    override fun generateUrl(fileKey: String, expirySeconds: Int): String {
        // For local storage, URLs don't expire
        return "$baseUrl/$fileKey"
    }

    override fun delete(fileKey: String): Boolean {
        val filePath = storagePath.resolve(fileKey)
        val metadataPath = filePath.resolveSibling("${filePath.fileName}.meta")

        return try {
            Files.deleteIfExists(filePath)
            Files.deleteIfExists(metadataPath)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun exists(fileKey: String): Boolean {
        return storagePath.resolve(fileKey).exists()
    }

    override fun listFiles(tenantId: TenantId, prefix: String?): List<StoredFile> {
        val tenantPath = storagePath.resolve(tenantId.value.toString())
        if (!tenantPath.exists()) return emptyList()

        return Files.walk(tenantPath)
            .filter { Files.isRegularFile(it) && !it.toString().endsWith(".meta") }
            .map { path ->
                val key = tenantPath.relativize(path).toString()
                val metadataPath = path.resolveSibling("${path.fileName}.meta")
                val metadata = if (metadataPath.exists()) {
                    Files.readAllLines(metadataPath).associate {
                        val parts = it.split("=", limit = 2)
                        parts[0] to (parts.getOrNull(1) ?: "")
                    }
                } else emptyMap()

                StoredFile(
                    key = key,
                    originalFileName = metadata["originalFileName"] ?: path.fileName.toString(),
                    contentType = metadata["contentType"] ?: "application/octet-stream",
                    size = path.fileSize(),
                    url = "$baseUrl/$key",
                    metadata = metadata,
                    uploadedAt = Files.getLastModifiedTime(path).toMillis()
                )
            }
            .toList()
    }

    private fun generateKey(tenantId: TenantId, fileName: String): String {
        val extension = fileName.substringAfterLast(".", "")
        val uuid = UUID.randomUUID().toString()
        return "${tenantId.value}/${uuid}.${extension}"
    }
}
