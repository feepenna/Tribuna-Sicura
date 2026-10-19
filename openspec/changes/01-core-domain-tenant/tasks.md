# Tasks

## 1. Documentação OpenSpec

- [ ] 1.1 Ajustar proposal.md para cobrir valor-objects, entidades, TenantContext, portas e cenários obrigatórios — verificado com `openspec validate`
- [ ] 1.2 Criar specs/core/tenant-isolation/spec.md com cenários — verificado com `openspec validate`
- [ ] 1.3 Criar design.md com decisões técnicas — verificado com `openspec validate`
- [ ] 1.4 Criar tasks.md com verificação por tarefa — verificado com `openspec validate`

## 2. Value Objects

- [ ] 2.1 Criar IDs tipados (value class sobre UUID/string) para OrganizationId, UserId, MembershipId, DocumentId — verificado com teste de unidade
- [ ] 2.2 Criar Email (normalização/validação mínima) — verificado com teste de unidade
- [ ] 2.3 Nomes não vazios (organização, usuário, documento) — verificado com teste de unidade
- [ ] 2.4 Testes de unidades para todos os Value Objects — `mvn test` passa

## 3. Entidades e estados

- [ ] 3.1 Entidade Organization (id, nome, status) — verificado com teste de unidade
- [ ] 3.2 Entidade User (id, e-mail, status, sem credenciais) — verificado com teste de unidade
- [ ] 3.3 Entidade Membership (id, organizationId, userId, role, status) — verificado com teste de unidade
- [ ] 3.4 Entidade Document com organizationId imutável — sem `copy()` que permita trocar tenant — verificado com teste de unidade
- [ ] 3.5 Criação de Document recebe Membership ativa e rejeita org divergente/inativa — verificado com teste de invariante
- [ ] 3.6 Testes de invariantes das entidades — `mvn test` passa

## 4. TenantContext e portas

- [ ] 4.1 TenantContext(organizationId, userId, membershipId) — verificado com teste de unidade
- [ ] 4.2 DocumentRepository e MembershipRepository sem método sem tenant — verificado com teste de compilação/unidade
- [ ] 4.3 Revisar portas para nenhuma busca sem tenant — verificado com inspeção de código

## 5. Fakes e contract tests

- [ ] 5.1 Criar FakeDocumentRepository e FakeMembershipRepository em src/test — verificado com compilação
- [ ] 5.2 Contract test abstrato reutilizável pela persistência futura — verificado com teste no fake
- [ ] 5.3 Executar contract test no fake — `mvn test` passa

## 6. Casos de uso

- [ ] 6.1 CreateDocument — verificado com teste de unidade
- [ ] 6.2 GetDocument (inexistente e outro tenant retornam o mesmo resultado) — verificado com teste de unidade
- [ ] 6.3 ListDocuments — verificado com teste de unidade
- [ ] 6.4 Testes de isolamento (cenários obrigatórios) — `mvn test` passa
  - Org A consulta documento da Org B → idêntico ao ID inexistente
  - ListDocuments devolve só documentos da org do contexto
  - Membership SUSPENDED/REMOVED ou org INACTIVE → não lê nem cria
  - Usuário com Memberships em duas orgs: cada contexto vê só a sua
  - organizationId do Document não pode ser alterado

## 7. Revisão

- [ ] 7.1 Diff sem segredos, sem dependências novas, sem itens fora de escopo, `.idea` intacto — `git diff` revisado
- [ ] 7.2 `mvn test` completo e com sucesso
- [ ] 7.3 Resumo com o que foi e não foi validado
