package br.com.tribunasicura.domain.contract

import br.com.tribunasicura.domain.port.DocumentRepository
import br.com.tribunasicura.domain.port.fakes.FakeDocumentRepository
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
