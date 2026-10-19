package org.example.domain.port

import org.example.domain.model.*

interface OrganizationRepository {
    fun findById(organizationId: OrganizationId): Organization?
    fun save(organization: Organization)
}

interface UserRepository {
    fun findById(userId: UserId): User?
    fun save(user: User)
}

interface MembershipRepository {
    fun findById(organizationId: OrganizationId, membershipId: MembershipId): Membership?
    fun findByUser(organizationId: OrganizationId, userId: UserId): Membership?
    fun save(membership: Membership)
}

interface DocumentRepository {
    fun findById(organizationId: OrganizationId, documentId: DocumentId): Document?
    fun findByOrganization(organizationId: OrganizationId): List<Document>
    fun save(document: Document)
}
