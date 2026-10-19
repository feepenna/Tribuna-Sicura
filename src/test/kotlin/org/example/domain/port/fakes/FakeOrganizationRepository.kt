package org.example.domain.port.fakes

import org.example.domain.model.Organization
import org.example.domain.model.OrganizationId
import org.example.domain.model.OrganizationStatus
import org.example.domain.port.OrganizationRepository

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
