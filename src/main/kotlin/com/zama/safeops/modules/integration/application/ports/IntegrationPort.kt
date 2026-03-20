/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.integration.application.ports

import com.zama.safeops.modules.analytics.domain.model.ReportDefinition
import com.zama.safeops.modules.integration.domain.model.Integration
import com.zama.safeops.modules.integration.domain.model.SyncJob
import com.zama.safeops.modules.integration.domain.model.WebhookEvent
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId

/**
 * Port for integration persistence.
 */
interface IntegrationPort {
    fun save(integration: Integration): Integration
    fun findById(id: String): Integration?
    fun findByTenant(tenantId: TenantId): List<Integration>
    fun findAllActive(): List<Integration>
    fun delete(id: String)
    fun saveJob(job: SyncJob): SyncJob
    fun findJobById(jobId: String): SyncJob?
    fun findJobsByIntegration(integrationId: String, limit: Int): List<SyncJob>
    fun saveWebhookEvent(event: WebhookEvent)
    fun findAllScheduledReports(): List<ReportDefinition>
}
