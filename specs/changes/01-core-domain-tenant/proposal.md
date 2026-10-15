# Change Proposal: 01-Core Domain & Tenant Isolation

## Context
Tribuna §icura requires strict multitenancy isolation (`organization_id`) and core domain entities (`Organization`, `User`, `Membership`, `Document`) to support legal document management without data leakage between law firms/departments.

## Objective
Implement the foundational domain model in Kotlin, ensuring that every document and membership is strictly bound to an organization, with immutable invariants and unit tests.

## Scope
1. Define Kotlin data classes / domain models for:
   - `Organization` (tenant)
   - `User` (identity)
   - `Membership` (links user to organization with role)
   - `Document` (core aggregate root for documents)
2. Implement repository interfaces and validation rules for tenant isolation.
3. Unit tests verifying domain rules and multitenant boundaries.
