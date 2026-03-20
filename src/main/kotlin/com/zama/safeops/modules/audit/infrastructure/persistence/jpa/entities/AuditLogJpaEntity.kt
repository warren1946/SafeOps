/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.audit.application.ports.AuditAction
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "audit_logs",
    indexes = [
        Index(name = "idx_audit_tenant", columnList = "tenant_id"),
        Index(name = "idx_audit_user", columnList = "user_id"),
        Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
        Index(name = "idx_audit_action", columnList = "action"),
        Index(name = "idx_audit_timestamp", columnList = "timestamp"),
        Index(name = "idx_audit_tenant_time", columnList = "tenant_id, timestamp")
    ]
)
class AuditLogJpaEntity(

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    val tenantId: Long,

    @Column(name = "user_id")
    val userId: Long?,

    @Column(name = "user_email")
    val userEmail: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    val action: AuditAction,

    @Column(name = "entity_type", nullable = false, length = 100)
    val entityType: String,

    @Column(name = "entity_id", length = 100)
    val entityId: String?,

    @Column(name = "description", nullable = false, length = 1000)
    val description: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", columnDefinition = "jsonb")
    val oldValues: Map<String, Any?>?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    val newValues: Map<String, Any?>?,

    @Column(name = "ip_address", length = 45)
    val ipAddress: String?,

    @Column(name = "user_agent", length = 500)
    val userAgent: String?,

    @Column(name = "session_id", length = 100)
    val sessionId: String?,

    @Column(name = "success", nullable = false)
    val success: Boolean = true,

    @Column(name = "error_message", length = 2000)
    val errorMessage: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    val metadata: Map<String, String>,

    @Column(name = "timestamp", nullable = false)
    val timestamp: Instant = Instant.now()
) {

    /**
     * Check if this represents a data modification operation.
     */
    fun isModification(): Boolean = action in setOf(
        AuditAction.CREATE,
        AuditAction.UPDATE,
        AuditAction.DELETE
    )
}
