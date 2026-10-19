package org.example.domain.contract

import org.example.domain.port.DocumentRepository
import org.example.domain.port.fakes.FakeDocumentRepository
import org.junit.jupiter.api.Test

class FakeDocumentRepositoryContractTest : DocumentRepositoryContractTest() {

    override fun repository(): DocumentRepository = FakeDocumentRepository()

    @Test
    fun `fake is a valid contract implementation`() {
        // Smoke test that the fake itself works through the contract
        val repo = repository()
        org.junit.jupiter.api.Assertions.assertNotNull(repo)
    }
}
