package org.example.domain.port.fakes

import org.example.domain.model.Membership
import org.example.domain.model.MembershipId
import org.example.domain.model.OrganizationId
import org.example.domain.model.UserId
import org.example.domain.port.MembershipRepository

class FakeMembershipRepository : MembershipRepository {
    private val memberships = mutableMapOf<OrganizationId, MutableMap<MembershipId, Membership>>()

    override fun findById(organizationId: OrganizationId, membershipId: MembershipId): Membership? {
        return memberships[organizationId]?.get(membershipId)
    }

    override fun findByUser(organizationId: OrganizationId, userId: UserId): Membership? {
        return memberships[organizationId]?.values?.firstOrNull { it.userId == userId }
    }

    override fun save(membership: Membership) {
        memberships.getOrPut(membership.organizationId) { mutableMapOf() }[membership.id] = membership
    }

    /// For test scenarios: force-stores a membership under a given org bucket
    /// regardless of the membership's organizationId field (simulates divergent data).
    fun forceSave(underOrganizationId: OrganizationId, membership: Membership) {
        memberships.getOrPut(underOrganizationId) { mutableMapOf() }[membership.id] = membership
    }

    fun countForOrganization(organizationId: OrganizationId): Int {
        return memberships[organizationId]?.size ?: 0
    }

    fun clear() {
        memberships.clear()
    }
}
