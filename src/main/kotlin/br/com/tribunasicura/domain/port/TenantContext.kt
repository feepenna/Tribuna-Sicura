package br.com.tribunasicura.domain.port

import br.com.tribunasicura.domain.model.MembershipId
import br.com.tribunasicura.domain.model.OrganizationId
import br.com.tribunasicura.domain.model.UserId

data class TenantContext(
    val organizationId: OrganizationId,
    val userId: UserId,
    val membershipId: MembershipId
) {
}
