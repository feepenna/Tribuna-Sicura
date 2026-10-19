package br.com.tribunasicura.domain.usecase

import br.com.tribunasicura.domain.model.Document
import br.com.tribunasicura.domain.model.DocumentId
import br.com.tribunasicura.domain.model.MembershipId
import br.com.tribunasicura.domain.model.MembershipRole
import br.com.tribunasicura.domain.model.MembershipStatus
import br.com.tribunasicura.domain.model.OrganizationId
import br.com.tribunasicura.domain.model.UserId
import br.com.tribunasicura.domain.port.fakes.FakeDocumentRepository
import br.com.tribunasicura.domain.port.fakes.FakeMembershipRepository
import br.com.tribunasicura.domain.port.fakes.FakeOrganizationRepository
import br.com.tribunasicura.domain.port.TenantContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import java.util.UUID

class GetDocumentTest {

    private fun newOrgId() = OrganizationId(UUID.randomUUID())
    private fun newUserId() = UserId(UUID.randomUUID())
    private fun newDocumentId() = DocumentId(UUID.randomUUID())

    private fun contextFor(orgId: OrganizationId, userId: UserId, membershipId: MembershipId): TenantContext {
        return TenantContext(organizationId = orgId, userId = userId, membershipId = membershipId)
    }

    private fun activeMembership(id: MembershipId, orgId: OrganizationId, userId: UserId): br.com.tribunasicura.domain.model.Membership {
        return br.com.tribunasicura.domain.model.Membership(
            id = id,
            organizationId = orgId,
            userId = userId,
            role = MembershipRole.MEMBER,
            status = MembershipStatus.ACTIVE
        )
    }

    @Test
    fun `returns document when it exists in the context organization and membership is active`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())
        val docId = newDocumentId()

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipId, orgId, userId))

        val docRepo = FakeDocumentRepository()
        val expected = Document(
            id = docId,
            organizationId = orgId,
            ownerUserId = userId,
            title = "Contract"
        )
        docRepo.save(expected)

        val useCase = GetDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx, docId)
        assertNotNull(result)
        assertEquals(docId, result!!.id)
        assertEquals(orgId, result.organizationId)
    }

    @Test
    fun `returns null for nonexistent document id in same organization`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipId, orgId, userId))

        val docRepo = FakeDocumentRepository()
        val useCase = GetDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx, newDocumentId())
        assertNull(result)
    }

    @Test
    fun `returns null for document of another tenant (same result as nonexistent)`() {
        val orgIdA = newOrgId()
        val orgIdB = newOrgId()
        val userId = newUserId()
        val membershipIdA = MembershipId(UUID.randomUUID())
        val membershipIdB = MembershipId(UUID.randomUUID())
        val docId = newDocumentId()

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgIdA, "Org A")
        orgRepo.addActive(orgIdB, "Org B")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipIdA, orgIdA, userId))
        memRepo.save(activeMembership(membershipIdB, orgIdB, userId))

        val docRepo = FakeDocumentRepository()
        // Document belongs to org A
        docRepo.save(
            Document(
                id = docId,
                organizationId = orgIdA,
                ownerUserId = userId,
                title = "Org A Document"
            )
        )

        val useCase = GetDocument(docRepo, memRepo, orgRepo)

        // org B context querying org A's document -> null (membership org mismatch)
        val ctxB = contextFor(orgIdB, userId, membershipIdB)
        val resultFromOtherTenant = useCase.execute(ctxB, docId)
        assertNull(resultFromOtherTenant)

        // Nonexistent id in org A context also returns null
        val ctxA = contextFor(orgIdA, userId, membershipIdA)
        val resultNonexistent = useCase.execute(ctxA, newDocumentId())
        assertNull(resultNonexistent)

        // Both are null - identical result
        assertEquals(resultFromOtherTenant, resultNonexistent)
    }

    @Test
    fun `returns null when membership is SUSPENDED`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(
            br.com.tribunasicura.domain.model.Membership(
                id = membershipId,
                organizationId = orgId,
                userId = userId,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.SUSPENDED
            )
        )

        val docRepo = FakeDocumentRepository()
        val useCase = GetDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx, newDocumentId())
        assertNull(result)
    }

    @Test
    fun `returns null when organization is INACTIVE`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addInactive(orgId, "Inactive Org")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipId, orgId, userId))

        val docRepo = FakeDocumentRepository()
        val useCase = GetDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val result = useCase.execute(ctx, newDocumentId())
        assertNull(result)
    }

    @Test
    fun `returns null when context userId differs from membership userId`() {
        val orgId = newOrgId()
        val userA = newUserId()
        val userB = newUserId()
        val membershipOfB = MembershipId(UUID.randomUUID())
        val docId = newDocumentId()

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(
            br.com.tribunasicura.domain.model.Membership(
                id = membershipOfB,
                organizationId = orgId,
                userId = userB,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.ACTIVE
            )
        )

        val docRepo = FakeDocumentRepository()
        docRepo.save(
            Document(
                id = docId,
                organizationId = orgId,
                ownerUserId = userA,
                title = "Doc of A"
            )
        )

        val useCase = GetDocument(docRepo, memRepo, orgRepo)
        // Contexto: userId de A, mas membershipId de B
        val ctx = contextFor(orgId, userA, membershipOfB)

        val result = useCase.execute(ctx, docId)
        assertNull(result)
    }
}
