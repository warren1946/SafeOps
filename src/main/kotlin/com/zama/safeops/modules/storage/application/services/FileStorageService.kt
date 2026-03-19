/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.storage.application.services

import com.zama.safeops.modules.storage.application.ports.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * Service for file storage operations with validation.
 */
@Service
class FileStorageService(
    private val storagePort: StoragePort,
    private val fileValidator: FileValidator
) {

    fun storeFile(
        tenantId: TenantId,
        file: MultipartFile,
        category: FileCategory,
        metadata: Map<String, String> = emptyMap()
    ): StoredFile {
        fileValidator.validate(
            fileName = file.originalFilename ?: "unknown",
            contentType = file.contentType ?: "application/octet-stream",
            size = file.size,
            category = category
        )

        val enrichedMetadata = metadata + mapOf(
            "originalFileName" to (file.originalFilename ?: "unknown"),
            "contentType" to (file.contentType ?: "application/octet-stream"),
            "category" to category.name,
            "uploadedBy" to (metadata["uploadedBy"] ?: "system")
        )

        return storagePort.store(
            tenantId = tenantId,
            fileName = file.originalFilename ?: "file",
            contentType = file.contentType ?: "application/octet-stream",
            inputStream = file.inputStream,
            metadata = enrichedMetadata
        )
    }

    fun storeInspectionPhoto(
        tenantId: TenantId,
        inspectionId: Long,
        file: MultipartFile,
        uploadedBy: Long
    ): StoredFile {
        return storeFile(
            tenantId = tenantId,
            file = file,
            category = FileCategory.INSPECTION_PHOTO,
            metadata = mapOf(
                "inspectionId" to inspectionId.toString(),
                "uploadedBy" to uploadedBy.toString()
            )
        )
    }

    fun storeHazardEvidence(
        tenantId: TenantId,
        hazardId: Long,
        file: MultipartFile,
        uploadedBy: Long
    ): StoredFile {
        return storeFile(
            tenantId = tenantId,
            file = file,
            category = FileCategory.HAZARD_EVIDENCE,
            metadata = mapOf(
                "hazardId" to hazardId.toString(),
                "uploadedBy" to uploadedBy.toString()
            )
        )
    }

    fun storeProfilePicture(
        tenantId: TenantId,
        userId: Long,
        file: MultipartFile
    ): StoredFile {
        return storeFile(
            tenantId = tenantId,
            file = file,
            category = FileCategory.PROFILE_PICTURE,
            metadata = mapOf("userId" to userId.toString())
        )
    }

    fun retrieveFile(fileKey: String): RetrievedFile? {
        return storagePort.retrieve(fileKey)
    }

    fun getFileUrl(fileKey: String, expirySeconds: Int = 3600): String {
        return storagePort.generateUrl(fileKey, expirySeconds)
    }

    fun deleteFile(fileKey: String): Boolean {
        return storagePort.delete(fileKey)
    }

    fun listInspectionPhotos(tenantId: TenantId, inspectionId: Long): List<StoredFile> {
        return storagePort.listFiles(tenantId)
            .filter { it.metadata["category"] == FileCategory.INSPECTION_PHOTO.name }
            .filter { it.metadata["inspectionId"] == inspectionId.toString() }
    }

    fun listHazardEvidence(tenantId: TenantId, hazardId: Long): List<StoredFile> {
        return storagePort.listFiles(tenantId)
            .filter { it.metadata["category"] == FileCategory.HAZARD_EVIDENCE.name }
            .filter { it.metadata["hazardId"] == hazardId.toString() }
    }
}
