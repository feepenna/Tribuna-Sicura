# Tasks: 01-Core Domain & Tenant Isolation

- [ ] **Task 1: Domain Entities & Value Objects**
  - [ ] Create `Organization` entity (`organization_id`, name, status)
  - [ ] Create `User` entity (`user_id`, email, auth state)
  - [ ] Create `Membership` entity (`membership_id`, `organization_id`, `user_id`, role)
  - [ ] Create `Document` entity (`document_id`, `organization_id`, `owner_user_id`, title, status)

- [ ] **Task 2: Invariants & Business Rules**
  - [ ] Enforce tenant boundary validation on document access
  - [ ] Ensure membership matches organization scope

- [ ] **Task 3: Unit Tests**
  - [ ] Write JUnit 5 tests in Kotlin verifying domain invariants and tenant isolation
