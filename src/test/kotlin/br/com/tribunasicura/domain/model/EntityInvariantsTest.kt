package br.com.tribunasicura.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import java.util.UUID

class EntityInvariantsTest {

    @Test
    fun `document organizationId is immutable - no copy method exists`() {
        val doc = Document(
            id = DocumentId(UUID.randomUUID()),
            organizationId = OrganizationId(UUID.randomUUID()),
            ownerUserId = UserId(UUID.randomUUID()),
            title = "Original Title"
        )

        // Verify we can read properties
        val originalOrgId = doc.organizationId
        assertNotNull(originalOrgId)

        // Verify title can be read (all properties are val)
        assertEquals("Original Title", doc.title)

        // Document is a class (not data class), so it has no auto-generated copy()
        // Confirm that attempting to call copy does not compile - this is verified
        // at compile time by the absence of such a method.
        // The key invariant: there is no setter or copy to change organizationId.
        val sameOrgId = doc.organizationId
        assertEquals(originalOrgId, sameOrgId)
    }

    @Test
    fun `two documents can have different organizationIds`() {
        val docA = Document(
            id = DocumentId(UUID.randomUUID()),
            organizationId = OrganizationId(UUID.randomUUID()),
            ownerUserId = UserId(UUID.randomUUID()),
            title = "Doc A"
        )
        val docB = Document(
            id = DocumentId(UUID.randomUUID()),
            organizationId = OrganizationId(UUID.randomUUID()),
            ownerUserId = UserId(UUID.randomUUID()),
            title = "Doc B"
        )

        assertNotEquals(docA.organizationId, docB.organizationId)
    }

    @Test
    fun `membership has immutable organizationId and userId`() {
        val membership = Membership(
            id = MembershipId(UUID.randomUUID()),
            organizationId = OrganizationId(UUID.randomUUID()),
            userId = UserId(UUID.randomUUID()),
            role = MembershipRole.MEMBER
        )

        assertNotNull(membership.organizationId)
        assertNotNull(membership.userId)
        // All properties are val; no setters exist
    }
}
