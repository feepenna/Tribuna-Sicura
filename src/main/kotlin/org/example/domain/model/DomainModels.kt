package org.example.domain.model

import java.util.UUID

@JvmInline
value class OrganizationId(val value: UUID) {
}

@JvmInline
value class UserId(val value: UUID) {
}

@JvmInline
value class MembershipId(val value: UUID) {
}

@JvmInline
value class DocumentId(val value: UUID) {
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        val trimmed = value.trim().lowercase()
        require(trimmed.contains("@")) { "Invalid email format" }
        val atIndex = trimmed.indexOf('@')
        require(atIndex > 0) { "Invalid email format: local part cannot be empty" }
        require(atIndex < trimmed.length - 1) { "Invalid email format: domain part cannot be empty" }
    }

    fun normalize(): String = value.trim().lowercase()
}

enum class OrganizationStatus {
    ACTIVE,
    INACTIVE
}

enum class UserStatus {
    ACTIVE,
    DISABLED
}

enum class MembershipStatus {
    ACTIVE,
    SUSPENDED,
    REMOVED
}

enum class MembershipRole {
    ADMIN,
    MEMBER
}

enum class DocumentStatus {
    ACTIVE,
    ARCHIVED
}

data class Organization(
    val id: OrganizationId,
    val name: String,
    val status: OrganizationStatus = OrganizationStatus.ACTIVE
) {
    init {
        require(name.isNotBlank()) { "Organization name cannot be blank" }
    }
}

data class User(
    val id: UserId,
    val email: Email,
    val status: UserStatus = UserStatus.ACTIVE
)

data class Membership(
    val id: MembershipId,
    val organizationId: OrganizationId,
    val userId: UserId,
    val role: MembershipRole,
    val status: MembershipStatus = MembershipStatus.ACTIVE
)

class Document(
    val id: DocumentId,
    val organizationId: OrganizationId,
    val ownerUserId: UserId,
    val title: String,
    val status: DocumentStatus = DocumentStatus.ACTIVE
) {
    init {
        require(title.isNotBlank()) { "Document title cannot be blank" }
    }
}
