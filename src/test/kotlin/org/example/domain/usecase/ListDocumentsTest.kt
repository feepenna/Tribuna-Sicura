package org.example.domain.usecase

import org.example.domain.model.Document
import org.example.domain.model.DocumentId
import org.example.domain.model.MembershipId
import org.example.domain.model.MembershipRole
import org.example.domain.model.MembershipStatus
import org.example.domain.model.OrganizationId
import org.example.domain.model.UserId
import org.example.domain.port.fakes.FakeDocumentRepository
import org.example.domain.port.fakes.FakeMembershipRepository
import org.example.domain.port.fakes.FakeOrganizationRepository
import org.example.domain.port.TenantContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.UUID

class ListDocumentsTest {

    private fun newOrgId() = OrganizationId(UUID.randomUUID())
    private fun newUserId() = UserId(UUID.randomUUID())
    private fun newDocumentId() = DocumentId(UUID.randomUUID())

    private fun contextFor(orgId: OrganizationId, userId: UserId, membershipId: MembershipId): TenantContext {
        return TenantContext(organizationId = orgId, userId = userId, membershipId = membershipId)
    }

    private fun activeMembership(id: MembershipId, orgId: OrganizationId, userId: UserId): org.example.domain.model.Membership {
        return org.example.domain.model.Membership(
            id = id,
            organizationId = orgId,
            userId = userId,
            role = MembershipRole.MEMBER,
            status = MembershipStatus.ACTIVE
        )
    }

    @Test
    fun `returns only documents of the context organization`() {
        val orgIdA = newOrgId()
        val orgIdB = newOrgId()
        val userId = newUserId()
        val membershipIdA = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgIdA, "Org A")
        orgRepo.addActive(orgIdB, "Org B")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipIdA, orgIdA, userId))

        val docRepo = FakeDocumentRepository()
        docRepo.save(Document(newDocumentId(), orgIdA, userId, "Doc A1"))
        docRepo.save(Document(newDocumentId(), orgIdA, userId, "Doc A2"))
        docRepo.save(Document(newDocumentId(), orgIdB, userId, "Doc B1"))

        val useCase = ListDocuments(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgIdA, userId, membershipIdA)

        val result = useCase.execute(ctx)
        assertEquals(2, result.size)
        assertTrue(result.all { it.organizationId == orgIdA })
    }

    @Test
    fun `returns empty list when membership is SUSPENDED`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(
            org.example.domain.model.Membership(
                id = membershipId,
                organizationId = orgId,
                userId = userId,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.SUSPENDED
            )
        )

        val docRepo = FakeDocumentRepository()
        val useCase = ListDocuments(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns empty list when membership is REMOVED`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(
            org.example.domain.model.Membership(
                id = membershipId,
                organizationId = orgId,
                userId = userId,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.REMOVED
            )
        )

        val docRepo = FakeDocumentRepository()
        val useCase = ListDocuments(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns empty list when organization is INACTIVE`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addInactive(orgId, "Inactive Org")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipId, orgId, userId))

        val docRepo = FakeDocumentRepository()
        val useCase = ListDocuments(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `user with memberships in two orgs sees only the current context org per call`() {
        val orgIdA = newOrgId()
        val orgIdB = newOrgId()
        val userId = newUserId()
        val membershipIdA = MembershipId(UUID.randomUUID())
        val membershipIdB = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgIdA, "Org A")
        orgRepo.addActive(orgIdB, "Org B")

        val memRepo = FakeMembershipRepository()
        memRepo.save(
            org.example.domain.model.Membership(
                id = membershipIdA,
                organizationId = orgIdA,
                userId = userId,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.ACTIVE
            )
        )
        memRepo.save(
            org.example.domain.model.Membership(
                id = membershipIdB,
                organizationId = orgIdB,
                userId = userId,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.ACTIVE
            )
        )

        val docRepo = FakeDocumentRepository()
        docRepo.save(Document(newDocumentId(), orgIdA, userId, "Doc A1"))
        docRepo.save(Document(newDocumentId(), orgIdA, userId, "Doc A2"))
        docRepo.save(Document(newDocumentId(), orgIdB, userId, "Doc B1"))

        val useCase = ListDocuments(docRepo, memRepo, orgRepo)

        // Context org A
        val ctxA = contextFor(orgIdA, userId, membershipIdA)
        val resultA = useCase.execute(ctxA)
        assertEquals(2, resultA.size)
        assertTrue(resultA.all { it.organizationId == orgIdA })

        // Context org B
        val ctxB = contextFor(orgIdB, userId, membershipIdB)
        val resultB = useCase.execute(ctxB)
        assertEquals(1, resultB.size)
        assertTrue(resultB.all { it.organizationId == orgIdB })
    }

    @Test
    fun `returns empty list when membership does not exist`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        // no membership saved

        val docRepo = FakeDocumentRepository()
        val useCase = ListDocuments(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns empty list when context userId differs from membership userId`() {
        val orgId = newOrgId()
        val userA = newUserId()
        val userB = newUserId()
        val membershipOfB = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(
            org.example.domain.model.Membership(
                id = membershipOfB,
                organizationId = orgId,
                userId = userB,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.ACTIVE
            )
        )

        val docRepo = FakeDocumentRepository()
        docRepo.save(Document(newDocumentId(), orgId, userA, "Doc of A"))

        val useCase = ListDocuments(docRepo, memRepo, orgRepo)
        // Contexto: userId de A, mas membershipId de B
        val ctx = contextFor(orgId, userA, membershipOfB)

        val result = useCase.execute(ctx)
        assertTrue(result.isEmpty())
    }
}
