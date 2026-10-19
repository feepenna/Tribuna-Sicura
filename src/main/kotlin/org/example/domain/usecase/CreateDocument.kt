package org.example.domain.usecase

import org.example.domain.model.Document
import org.example.domain.model.DocumentId
import org.example.domain.model.MembershipStatus
import org.example.domain.model.OrganizationStatus
import org.example.domain.model.Organization
import org.example.domain.port.DocumentRepository
import org.example.domain.port.MembershipRepository
import org.example.domain.port.OrganizationRepository
import org.example.domain.port.TenantContext

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
