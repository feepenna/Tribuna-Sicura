package br.com.tribunasicura.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import java.util.UUID

class DomainModelsTest {

    @Test
    fun `organizationId value class accepts valid UUID`() {
        val id = OrganizationId(UUID.randomUUID())
        assertNotNull(id.value)
    }

    @Test
    fun `userId value class accepts valid UUID`() {
        val id = UserId(UUID.randomUUID())
        assertNotNull(id.value)
    }

    @Test
    fun `membershipId value class accepts valid UUID`() {
        val id = MembershipId(UUID.randomUUID())
        assertNotNull(id.value)
    }

    @Test
    fun `documentId value class accepts valid UUID`() {
        val id = DocumentId(UUID.randomUUID())
        assertNotNull(id.value)
    }

    @Test
    fun `email normalizes to lowercase and trims whitespace`() {
        val email = Email("  User@Example.COM  ")
        assertEquals("user@example.com", email.normalize())
    }

    @Test
    fun `email rejects blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            Email("   ")
        }
        assertTrue(ex.message!!.contains("Email cannot be blank"))
    }

    @Test
    fun `email rejects missing at sign`() {
        val ex = assertThrows<IllegalArgumentException> {
            Email("notanemail")
        }
        assertTrue(ex.message!!.contains("Invalid email format"))
    }

    @Test
    fun `email rejects empty local part`() {
        val ex = assertThrows<IllegalArgumentException> {
            Email("@domain.com")
        }
        assertTrue(ex.message!!.contains("local part cannot be empty"))
    }

    @Test
    fun `email rejects empty domain part`() {
        val ex = assertThrows<IllegalArgumentException> {
            Email("user@")
        }
        assertTrue(ex.message!!.contains("domain part cannot be empty"))
    }

    @Test
    fun `organization requires non-blank name`() {
        val ex = assertThrows<IllegalArgumentException> {
            Organization(
                id = OrganizationId(UUID.randomUUID()),
                name = "   "
            )
        }
        assertTrue(ex.message!!.contains("Organization name cannot be blank"))
    }

    @Test
    fun `document requires non-blank title`() {
        val ex = assertThrows<IllegalArgumentException> {
            Document(
                id = DocumentId(UUID.randomUUID()),
                organizationId = OrganizationId(UUID.randomUUID()),
                ownerUserId = UserId(UUID.randomUUID()),
                title = "   "
            )
        }
        assertTrue(ex.message!!.contains("Document title cannot be blank"))
    }

    @Test
    fun `organization status enum has ACTIVE and INACTIVE`() {
        assertEquals(2, OrganizationStatus.values().size)
        assertTrue(OrganizationStatus.ACTIVE == OrganizationStatus.valueOf("ACTIVE"))
        assertTrue(OrganizationStatus.INACTIVE == OrganizationStatus.valueOf("INACTIVE"))
    }

    @Test
    fun `membership status enum has ACTIVE, SUSPENDED, REMOVED`() {
        assertEquals(3, MembershipStatus.values().size)
    }

    @Test
    fun `value classes accept valid UUID without throwing`() {
        assertDoesNotThrow { OrganizationId(UUID.randomUUID()) }
        assertDoesNotThrow { UserId(UUID.randomUUID()) }
        assertDoesNotThrow { MembershipId(UUID.randomUUID()) }
        assertDoesNotThrow { DocumentId(UUID.randomUUID()) }
    }
}
