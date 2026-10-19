package org.example.domain.port.fakes

import org.example.domain.model.Document
import org.example.domain.model.DocumentId
import org.example.domain.model.OrganizationId
import org.example.domain.port.DocumentRepository

class FakeDocumentRepository : DocumentRepository {
    private val documents = mutableMapOf<OrganizationId, MutableMap<DocumentId, Document>>()

    override fun findById(organizationId: OrganizationId, documentId: DocumentId): Document? {
        return documents[organizationId]?.get(documentId)
    }

    override fun findByOrganization(organizationId: OrganizationId): List<Document> {
        return documents[organizationId]?.values?.toList() ?: emptyList()
    }

    override fun save(document: Document) {
        documents.getOrPut(document.organizationId) { mutableMapOf() }[document.id] = document
    }

    fun countForOrganization(organizationId: OrganizationId): Int {
        return documents[organizationId]?.size ?: 0
    }

    fun clear() {
        documents.clear()
    }
}
