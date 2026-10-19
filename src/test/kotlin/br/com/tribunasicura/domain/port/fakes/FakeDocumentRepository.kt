package br.com.tribunasicura.domain.port.fakes

import br.com.tribunasicura.domain.model.Document
import br.com.tribunasicura.domain.model.DocumentId
import br.com.tribunasicura.domain.model.OrganizationId
import br.com.tribunasicura.domain.port.DocumentRepository

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
