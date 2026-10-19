package org.example.domain.usecase

import org.example.domain.model.Document
import org.example.domain.model.DocumentId
import org.example.domain.model.MembershipStatus
import org.example.domain.model.OrganizationStatus
import org.example.domain.port.DocumentRepository
import org.example.domain.port.MembershipRepository
import org.example.domain.port.OrganizationRepository
import org.example.domain.port.TenantContext

class ListDocuments(
    private val documentRepository: DocumentRepository,
    private val membershipRepository: MembershipRepository,
    private val organizationRepository: OrganizationRepository
) {
    fun execute(tenantContext: TenantContext): List<Document> {
        val membership = membershipRepository.findById(tenantContext.organizationId, tenantContext.membershipId)
        if (membership == null || membership.status != MembershipStatus.ACTIVE) {
            return emptyList()
        }
        if (membership.organizationId != tenantContext.organizationId) {
            return emptyList()
        }
        if (membership.userId != tenantContext.userId) {
            return emptyList()
        }

        val organization = organizationRepository.findById(tenantContext.organizationId)
        if (organization == null || organization.status != OrganizationStatus.ACTIVE) {
            return emptyList()
        }

        return documentRepository.findByOrganization(tenantContext.organizationId)
    }
}
