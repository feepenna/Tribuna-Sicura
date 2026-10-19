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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import java.util.UUID

class CreateDocumentTest {

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
    fun `creates document when membership is active and organization is active`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())
        val membership = activeMembership(membershipId, orgId, userId)

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(membership)

        val docRepo = FakeDocumentRepository()

        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val doc = useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Legal Brief"))

        assertNotNull(doc)
        assertEquals(orgId, doc.organizationId)
        assertEquals(userId, doc.ownerUserId)
        assertEquals("Legal Brief", doc.title)
    }

    @Test
    fun `rejects when membership is SUSPENDED`() {
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
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Doc"))
        }
        assertTrue(ex.message!!.contains("Membership must be ACTIVE"))
    }

    @Test
    fun `rejects when membership is REMOVED`() {
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
                status = MembershipStatus.REMOVED
            )
        )

        val docRepo = FakeDocumentRepository()
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Doc"))
        }
        assertTrue(ex.message!!.contains("Membership must be ACTIVE"))
    }

    @Test
    fun `rejects when membership does not exist`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        // no membership saved

        val docRepo = FakeDocumentRepository()
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Doc"))
        }
        assertTrue(ex.message!!.contains("Membership not found"))
    }

    @Test
    fun `rejects when membership organization diverges from context organization`() {
        val orgIdA = newOrgId()
        val orgIdB = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        // Membership stored under org B's lookup bucket but claims org A (divergent data)
        val memRepo = FakeMembershipRepository()
        memRepo.forceSave(
            underOrganizationId = orgIdB,
            membership = br.com.tribunasicura.domain.model.Membership(
                id = membershipId,
                organizationId = orgIdA,
                userId = userId,
                role = MembershipRole.MEMBER,
                status = MembershipStatus.ACTIVE
            )
        )

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgIdB, "Org B")

        val docRepo = FakeDocumentRepository()
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgIdB, userId, membershipId) // context is org B

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Doc"))
        }
        assertTrue(ex.message!!.contains("Membership organization does not match context organization"))
    }

    @Test
    fun `rejects when organization is INACTIVE`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addInactive(orgId, "Inactive Org")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipId, orgId, userId))

        val docRepo = FakeDocumentRepository()
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Doc"))
        }
        assertTrue(ex.message!!.contains("Organization must be ACTIVE"))
    }

    @Test
    fun `rejects when title is blank`() {
        val orgId = newOrgId()
        val userId = newUserId()
        val membershipId = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        memRepo.save(activeMembership(membershipId, orgId, userId))

        val docRepo = FakeDocumentRepository()
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        val ctx = contextFor(orgId, userId, membershipId)

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "   "))
        }
        assertTrue(ex.message!!.contains("Document title cannot be blank"))
    }

    @Test
    fun `rejects when context userId differs from membership userId`() {
        val orgId = newOrgId()
        val userA = newUserId()
        val userB = newUserId()
        val membershipOfB = MembershipId(UUID.randomUUID())

        val orgRepo = FakeOrganizationRepository()
        orgRepo.addActive(orgId, "Org A")

        val memRepo = FakeMembershipRepository()
        // Membership de userB, encontrável pelo membershipId de B na org A
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
        val useCase = CreateDocument(docRepo, memRepo, orgRepo)
        // Contexto: userId de A, mas membershipId de B
        val ctx = contextFor(orgId, userA, membershipOfB)

        val ex = assertThrows<IllegalArgumentException> {
            useCase.execute(ctx, CreateDocument.Command(documentId = newDocumentId(), title = "Doc"))
        }
        assertTrue(ex.message!!.contains("userId") || ex.message!!.contains("Membership"))
    }
}
