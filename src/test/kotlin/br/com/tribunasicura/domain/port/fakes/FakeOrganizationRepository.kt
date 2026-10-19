package br.com.tribunasicura.domain.port.fakes

import br.com.tribunasicura.domain.model.Organization
import br.com.tribunasicura.domain.model.OrganizationId
import br.com.tribunasicura.domain.model.OrganizationStatus
import br.com.tribunasicura.domain.port.OrganizationRepository

class FakeOrganizationRepository : OrganizationRepository {
    private val organizations = mutableMapOf<OrganizationId, Organization>()

    override fun findById(organizationId: OrganizationId): Organization? {
        return organizations[organizationId]
    }

    override fun save(organization: Organization) {
        organizations[organization.id] = organization
    }

    fun count(): Int {
        return organizations.size
    }

    fun clear() {
        organizations.clear()
    }

    fun addActive(id: OrganizationId, name: String) {
        save(Organization(id = id, name = name, status = OrganizationStatus.ACTIVE))
    }

    fun addInactive(id: OrganizationId, name: String) {
        save(Organization(id = id, name = name, status = OrganizationStatus.INACTIVE))
    }
}
