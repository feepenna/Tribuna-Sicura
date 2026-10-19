package org.example.domain.usecase

import org.example.domain.model.Document
import org.example.domain.model.DocumentId
import org.example.domain.model.MembershipStatus
import org.example.domain.model.OrganizationStatus
import org.example.domain.port.DocumentRepository
import org.example.domain.port.MembershipRepository
import org.example.domain.port.OrganizationRepository
import org.example.domain.port.TenantContext

class GetDocument(
    private val documentRepository: DocumentRepository,
    private val membershipRepository: MembershipRepository,
    private val organizationRepository: OrganizationRepository
) {
    fun execute(tenantContext: TenantContext, documentId: DocumentId): Document? {
        val membership = membershipRepository.findById(tenantContext.organizationId, tenantContext.membershipId)
        if (membership == null || membership.status != MembershipStatus.ACTIVE) {
            return null
        }
        if (membership.organizationId != tenantContext.organizationId) {
            return null
        }
        if (membership.userId != tenantContext.userId) {
            return null
        }

        val organization = organizationRepository.findById(tenantContext.organizationId)
        if (organization == null || organization.status != OrganizationStatus.ACTIVE) {
            return null
        }

        return documentRepository.findById(tenantContext.organizationId, documentId)
    }
}
