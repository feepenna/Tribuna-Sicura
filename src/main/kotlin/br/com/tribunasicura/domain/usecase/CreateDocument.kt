package br.com.tribunasicura.domain.usecase

import br.com.tribunasicura.domain.model.Document
import br.com.tribunasicura.domain.model.DocumentId
import br.com.tribunasicura.domain.model.MembershipStatus
import br.com.tribunasicura.domain.model.OrganizationStatus
import br.com.tribunasicura.domain.model.Organization
import br.com.tribunasicura.domain.port.DocumentRepository
import br.com.tribunasicura.domain.port.MembershipRepository
import br.com.tribunasicura.domain.port.OrganizationRepository
import br.com.tribunasicura.domain.port.TenantContext

class CreateDocument(
    private val documentRepository: DocumentRepository,
    private val membershipRepository: MembershipRepository,
    private val organizationRepository: OrganizationRepository
) {
    data class Command(
        val documentId: DocumentId,
        val title: String
    )

    fun execute(tenantContext: TenantContext, command: Command): Document {
        val membership = membershipRepository.findById(tenantContext.organizationId, tenantContext.membershipId)
        require(membership != null) {
            "Membership not found for user ${tenantContext.userId} in organization ${tenantContext.organizationId}"
        }
        require(membership.status == MembershipStatus.ACTIVE) {
            "Membership must be ACTIVE to create documents; current status: ${membership.status}"
        }
        require(membership.organizationId == tenantContext.organizationId) {
            "Membership organization does not match context organization"
        }
        require(membership.userId == tenantContext.userId) {
            "Membership user does not match context user"
        }

        val organization = organizationRepository.findById(tenantContext.organizationId)
        require(organization != null) {
            "Organization not found: ${tenantContext.organizationId}"
        }
        require(organization.status == OrganizationStatus.ACTIVE) {
            "Organization must be ACTIVE to create documents; current status: ${organization.status}"
        }

        val document = Document(
            id = command.documentId,
            organizationId = tenantContext.organizationId,
            ownerUserId = tenantContext.userId,
            title = command.title
        )

        documentRepository.save(document)
        return document
    }
}
