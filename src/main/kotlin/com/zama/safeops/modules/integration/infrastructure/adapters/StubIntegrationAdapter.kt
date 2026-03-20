/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.integration.infrastructure.adapters

import com.zama.safeops.modules.analytics.domain.model.ReportDefinition
import com.zama.safeops.modules.integration.application.ports.IntegrationPort
import com.zama.safeops.modules.integration.domain.model.Integration
import com.zama.safeops.modules.integration.domain.model.SyncJob
import com.zama.safeops.modules.integration.domain.model.WebhookEvent
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component

/**
 * Stub implementation of IntegrationPort.
 */
@Component
class StubIntegrationAdapter : IntegrationPort {

    override fun save(integration: Integration): Integration = integration

    override fun findById(id: String): Integration? = null

    override fun findByTenant(tenantId: TenantId): List<Integration> = emptyList()

    override fun findAllActive(): List<Integration> = emptyList()

    override fun delete(id: String) {}

    override fun saveJob(job: SyncJob): SyncJob = job

    override fun findJobById(jobId: String): SyncJob? = null

    override fun findJobsByIntegration(integrationId: String, limit: Int): List<SyncJob> = emptyList()

    override fun saveWebhookEvent(event: WebhookEvent) {}

    override fun findAllScheduledReports(): List<ReportDefinition> = emptyList()
}
