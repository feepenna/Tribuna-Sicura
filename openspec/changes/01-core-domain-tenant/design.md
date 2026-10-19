# Design

## Context

O projeto atual está em estágio inicial, com um scaffold Kotlin. As entidades de domínio precisam ser construídas com foco em isolamento de tenant. Atualmente, o sistema não possui uma implementação funcional de segurança ou isolamento, exigindo que estes sejam embutidos no design de entidades, portas (repositórios) e casos de uso desde o início.

## Goals / Non-Goals

**Goals:**
- Implementar as entidades de domínio com Value Objects tipados para IDs, garantindo type-safety em cruzamentos de tenant.
- Definir contratos de repositório que obriguem o uso de `TenantContext` ou `OrganizationId`.
- Garantir que as regras de negócio de isolamento fiquem no domínio, não nos adaptadores ou persistência.
- Criar fakes de persistência que suportem testes de isolamento.

**Non-Goals:**
- Implementação de persistência real (SQL/NoSQL) nesta fase.
- Autenticação e autorização complexas (foco no isolamento estrutural).

## Decisions

- **Uso de `Value Class` (Inline Classes):** Escolhido para IDs (`OrganizationId`, etc.) para obter type-safety sem custo de alocação de objetos em tempo de execução, prevenindo a troca acidental de IDs de tipos diferentes.
- **`TenantContext` nos contratos de repositório:** Todas as interfaces de repositório aceitarão `organizationId` ou `TenantContext` para garantir que o isolamento seja verificado na fronteira do domínio.
- **Invariantes nas Entidades:** Validação de `init` para garantir que estados inválidos (ex: IDs em branco, e-mail sem @) não sejam instanciados.
- **Fakes em `src/test`:** Para garantir isolamento de dependências e facilitar testes, os Fakes serão implementados apenas no escopo de teste, usando estruturas de dados em memória (`Map` ou `List`) com chaves compostas pelo `organizationId`.

## Risks / Trade-offs

- **[Risco] Erro de desenvolvimento (esquecer de passar tenant):** Mitigação: Uso forte de tipos (`organizationId` como parâmetro obrigatório) fará com que o código não compile se o tenant for esquecido.
- **[Risco] Fakes não refletirem comportamento da persistência real:** Mitigação: Criação de *contract tests* (testes de contrato abstratos) que serão executados tanto contra os Fakes quanto contra qualquer implementação futura de persistência.
- **[Trade-off] Duplicação de lógica de validação:** Validações de domínio estarão espalhadas nas entidades (`init`), mas isso é aceitável para garantir a integridade do agregado.
