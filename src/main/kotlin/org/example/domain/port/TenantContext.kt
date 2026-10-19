package org.example.domain.port

import org.example.domain.model.MembershipId
import org.example.domain.model.OrganizationId
import org.example.domain.model.UserId

data class TenantContext(
    val organizationId: OrganizationId,
    val userId: UserId,
    val membershipId: MembershipId
) {
}
