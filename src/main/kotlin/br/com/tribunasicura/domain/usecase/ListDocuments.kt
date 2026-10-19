package br.com.tribunasicura.domain.usecase

import br.com.tribunasicura.domain.model.Document
import br.com.tribunasicura.domain.model.DocumentId
import br.com.tribunasicura.domain.model.MembershipStatus
import br.com.tribunasicura.domain.model.OrganizationStatus
import br.com.tribunasicura.domain.port.DocumentRepository
import br.com.tribunasicura.domain.port.MembershipRepository
import br.com.tribunasicura.domain.port.OrganizationRepository
import br.com.tribunasicura.domain.port.TenantContext

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
