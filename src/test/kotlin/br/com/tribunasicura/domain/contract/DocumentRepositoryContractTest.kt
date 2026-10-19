package br.com.tribunasicura.domain.contract

import br.com.tribunasicura.domain.model.Document
import br.com.tribunasicura.domain.model.DocumentId
import br.com.tribunasicura.domain.model.MembershipId
import br.com.tribunasicura.domain.model.OrganizationId
import br.com.tribunasicura.domain.model.UserId
import br.com.tribunasicura.domain.port.DocumentRepository
import org.junit.jupiter.api.Test

abstract class DocumentRepositoryContractTest {

    abstract fun repository(): DocumentRepository

    protected fun newOrgId() = OrganizationId(java.util.UUID.randomUUID())

    protected fun newDocId() = DocumentId(java.util.UUID.randomUUID())

    @Test
    fun `get by id for a different tenant returns null, same as nonexistent id`() {
        val repo = repository()
        val orgA = newOrgId()
        val orgB = newOrgId()
        val docId = newDocId()

        // Save a document in org A
        repo.save(
            Document(
                id = docId,
                organizationId = orgA,
                ownerUserId = UserId(java.util.UUID.randomUUID()),
                title = "Doc in org A"
            )
        )

        // Querying org B for that same documentId must return null (same as nonexistent)
        val fromOtherTenant = repo.findById(orgB, docId)
        val fromNonexistent = repo.findById(orgB, DocumentId(java.util.UUID.randomUUID()))

        org.junit.jupiter.api.Assertions.assertNull(fromOtherTenant)
        org.junit.jupiter.api.Assertions.assertNull(fromNonexistent)
        org.junit.jupiter.api.Assertions.assertEquals(fromOtherTenant, fromNonexistent)
    }

    @Test
    fun `list returns only documents of the context organization`() {
        val repo = repository()
        val orgA = newOrgId()
        val orgB = newOrgId()

        repo.save(
            Document(
                id = newDocId(),
                organizationId = orgA,
                ownerUserId = UserId(java.util.UUID.randomUUID()),
                title = "Doc A1"
            )
        )
        repo.save(
            Document(
                id = newDocId(),
                organizationId = orgA,
                ownerUserId = UserId(java.util.UUID.randomUUID()),
                title = "Doc A2"
            )
        )
        repo.save(
            Document(
                id = newDocId(),
                organizationId = orgB,
                ownerUserId = UserId(java.util.UUID.randomUUID()),
                title = "Doc B1"
            )
        )

        val docsA = repo.findByOrganization(orgA)
        val docsB = repo.findByOrganization(orgB)

        org.junit.jupiter.api.Assertions.assertEquals(2, docsA.size)
        org.junit.jupiter.api.Assertions.assertEquals(1, docsB.size)
        org.junit.jupiter.api.Assertions.assertTrue(docsA.all { it.organizationId == orgA })
        org.junit.jupiter.api.Assertions.assertTrue(docsB.all { it.organizationId == orgB })
    }
}
