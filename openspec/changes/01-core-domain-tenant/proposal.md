# Proposal

## Why

O Tribuna §icura gerencia documentos jurídicos altamente sensíveis e requer isolamento estrito entre organizações (tenants) desde o modelo de domínio. Para prevenir vazamento de dados, acesso cruzado não autorizado e garantir a integridade dos agregados, é necessário estabelecer o núcleo do domínio com invariantes imutáveis, identificadores tipados, contexto de tenant e portas de repositório que forcem o isolamento em nível de tipo e de contrato.

## What Changes

- Introdução de Value Objects com validação e imutabilidade: `OrganizationId`, `UserId`, `MembershipId`, `DocumentId` e `Email`.
- Definição das entidades e invariantes centrais:
  - `Organization` (com status e nome não vazio).
  - `User` (sem credenciais de autenticação, com e-mail validado/normalizado).
  - `Membership` (vinculando usuário à organização com papel e status).
  - `Document` com `organizationId` estritamente imutável (sem possibilidade de mutação ou `copy()` que altere tenant).
- Introdução do `TenantContext(organizationId, userId, membershipId)`.
- Definição de portas de repositório (`DocumentRepository`, `MembershipRepository`, etc.) sem nenhum método que permita busca ou acesso desprovido de tenant.
- Implementação de casos de uso centrais (`CreateDocument`, `GetDocument`, `ListDocuments`) que respeitam o isolamento de tenant e status ativo da organização e membership.
- Fakes em memória para testes e testes abstratos de contrato (contract tests) reutilizáveis.

## Capabilities

### New Capabilities
- `core/tenant-isolation`: Isolamento multitenant no domínio, garantindo que documentos e memberships sejam estritamente delimitados por organização e que consultas entre organizações diferentes produzam o mesmo resultado que documentos inexistentes.

### Modified Capabilities

## Impact

- Código afetado: pacote `br.com.tribunasicura.domain` (modelos, portas e casos de uso).
- APIs: portas de repositório exigirão `TenantContext` ou `organizationId` obrigatório em todas as operações de consulta e persistência.
- Dependências: apenas biblioteca padrão Kotlin e JUnit 5 já existentes no `pom.xml`. Sem dependências externas adicionais.
