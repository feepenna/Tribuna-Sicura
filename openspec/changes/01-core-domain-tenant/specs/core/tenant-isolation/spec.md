# Spec Delta

## Purpose

Estabelece o isolamento estrito entre organizações (tenants) no modelo de domínio, garantindo que documentos e memberships sejam estritamente delimitados e que acessos cruzados sejam prevenidos.

## ADDED Requirements

### Requirement: Isolamento por Tenant em consultas
O sistema SHALL restringir a visualização de documentos apenas à organização (tenant) associada ao contexto do usuário. Tentativas de acesso a documentos de outras organizações SHALL resultar em comportamento idêntico ao de documentos inexistentes.

#### Scenario: Acesso a documento de outro tenant
- **WHEN** um usuário da Org A tenta acessar um `documentId` existente que pertence à Org B
- **THEN** o sistema retorna `null` ou resultado idêntico ao de `documentId` inexistente

#### Scenario: Listagem de documentos por tenant
- **WHEN** um usuário lista documentos
- **THEN** o sistema retorna apenas documentos pertencentes à organização do contexto atual

### Requirement: Validação de Membership para ações
A criação ou acesso a documentos SHALL exigir uma Membership ativa e pertencente à mesma organização do documento ou contexto.

#### Scenario: Criar documento sem membership ativa
- **WHEN** um usuário com Membership SUSPENDED/REMOVED ou inexistente na organização tenta criar um documento
- **THEN** o sistema rejeita a operação

#### Scenario: Membership em múltiplas orgs
- **WHEN** um usuário possui Membership em duas organizações (Org A e Org B)
- **THEN** o contexto da Org A vê apenas documentos da Org A, e o contexto da Org B vê apenas documentos da Org B

### Requirement: Imutabilidade do Tenant do Documento
O `organizationId` de um documento SHALL ser imutável após a criação.

#### Scenario: Tentativa de alterar tenant do documento
- **WHEN** uma tentativa é feita para alterar o `organizationId` de um documento persistido
- **THEN** a operação é impedida (pelo design do modelo de dados e falta de mutação exposta)
