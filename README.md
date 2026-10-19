# Tribuna §icura

Plataforma para escritórios de advocacia e departamentos jurídicos criarem, assinarem, armazenarem e compartilharem documentos jurídicos sensíveis com controle rigoroso de acesso. O fluxo começa no Microsoft Word e termina em um PDF criptografado, assinado com certificado ICP-Brasil (PAdES) e aberto apenas no leitor seguro da plataforma.

## Status

Estágio inicial. Apenas o núcleo de domínio está implementado:

| Área | Estado |
|---|---|
| Núcleo de domínio e isolamento de tenant (`Organization`, `User`, `Membership`, `Document`) | Implementado com testes — mudança OpenSpec `01-core-domain-tenant` ainda não arquivada |
| Autenticação, 2FA, sessões | Planejado |
| Add-in do Word e exportação segura de PDF | Planejado |
| Cofre de chaves/certificados, assinatura PAdES | Planejado |
| Leitor web seguro | Planejado |
| Compartilhamento interno e por link temporário | Planejado |
| Auditoria | Planejado |

O escopo completo do MVP e o que fica fora dele estão em [docs/product-overview.md](docs/product-overview.md).

## Stack

- Kotlin 2.4 (JVM), Maven via wrapper (`./mvnw`)
- JUnit 5 + kotlin-test
- Sem frameworks de aplicação ou persistência por enquanto (decisões pendentes)

## Como rodar

```bash
./mvnw test      # executa a suíte de testes
./mvnw package   # compila e empacota
```

## Estrutura do repositório

```
.
├── README.md                 # este arquivo
├── AGENTS.md                 # regras para agentes de IA (segurança, fluxo, Kotlin)
├── CLAUDE.md                 # aponta para AGENTS.md
├── LICENSE                   # AGPL-3.0
├── CONTRIBUTING.md           # como contribuir
├── CLA.md                    # contrato de licença de contribuidor
├── docs/
│   ├── product-overview.md   # visão do produto, MVP, fluxo principal, decisões a validar
│   └── domain-model.md       # entidades, relações, invariantes e casos de uso
├── openspec/
│   ├── config.yaml           # contexto e regras do OpenSpec
│   ├── specs/                # especificações aceitas (fonte de verdade do comportamento)
│   └── changes/              # mudanças em andamento; concluídas vão para changes/archive/
├── .github/workflows/        # CI (verificação do CLA)
├── .opencode/                # comandos e skills do fluxo OpenSpec (opsx-*)
└── src/
    ├── main/kotlin/org/example/domain/   # model, port, usecase
    └── test/kotlin/org/example/domain/   # testes, fakes e contract tests
```

## Como o trabalho é organizado

1. **Produto e domínio** — `docs/` descreve o que construir e por quê.
2. **Especificação** — cada mudança começa como proposta em `openspec/changes/<nn>-<nome>/` (proposal, design, tasks, specs delta).
3. **Implementação** — segue as tarefas da mudança e as regras de [AGENTS.md](AGENTS.md).
4. **Arquivamento** — mudança validada é arquivada e suas specs passam a valer em `openspec/specs/`.

## Princípios

Confidencialidade, integridade, autorização explícita, isolamento entre organizações e auditabilidade. Segredos criptográficos nunca vão para o cliente, para logs ou para dentro do PDF. Detalhes em [AGENTS.md §8](AGENTS.md).

## Licença

Copyright (C) 2026 Felipe Penna

Este projeto é distribuído sob a [GNU Affero General Public License v3.0](LICENSE) (AGPL-3.0). Você pode usar, estudar, modificar e redistribuir o código, desde que versões modificadas — inclusive as oferecidas como serviço pela rede — também sejam disponibilizadas sob a AGPL-3.0, com o código-fonte correspondente.

Para uso sob outros termos (por exemplo, integrar a um produto fechado sem as obrigações da AGPL), entre em contato para uma licença comercial.

## Contribuindo

Contribuições são bem-vindas e exigem a assinatura do [CLA](CLA.md). Veja [CONTRIBUTING.md](CONTRIBUTING.md).
