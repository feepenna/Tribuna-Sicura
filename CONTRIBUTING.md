# Como contribuir com o Tribuna §icura

Obrigado pelo interesse! Este guia explica como propor mudanças, quais regras o código precisa seguir e o que acontece com os direitos sobre a sua contribuição.

## Antes de tudo: o CLA

O Tribuna §icura é distribuído sob a [AGPL-3.0](LICENSE) e também é licenciado comercialmente pelo mantenedor. Para que as duas coisas sejam possíveis, **toda contribuição exige a assinatura do [Contrato de Licença de Contribuidor (CLA)](CLA.md)**.

Em resumo, o CLA:

- mantém você como titular e autor da sua contribuição. Não é uma cessão de direitos;
- concede ao mantenedor uma licença ampla e irrevogável, que permite distribuir sua contribuição sob a AGPL-3.0 e também sob outras licenças, inclusive comerciais;
- inclui uma licença de patentes e a declaração de que o código é seu, ou de que você tem permissão para enviá-lo.

Leia o [texto completo](CLA.md) antes de assinar. A assinatura é feita uma única vez, direto no pull request:

1. Abra seu pull request normalmente.
2. O bot **CLA Assistant** vai comentar pedindo a assinatura.
3. Responda no PR com o comentário exato:
   ```
   I have read the CLA Document and I hereby sign the CLA
   ```
4. A verificação do CLA fica verde. Se ela não atualizar, comente `recheck`.

Pull requests sem o CLA assinado não podem ser aceitos.

Se você contribui como parte do seu trabalho, confirme antes que o seu empregador permite. Em muitos contratos, o código produzido pertence à empresa.

## Vulnerabilidades de segurança

**Não abra issues públicas para falhas de segurança.** O projeto lida com documentos jurídicos sensíveis. Reporte de forma privada pela aba *Security → Report a vulnerability* do repositório. Inclua passos de reprodução e o impacto esperado.

## Fluxo de trabalho

As mudanças de comportamento seguem um fluxo guiado por especificação ([OpenSpec](openspec/)):

1. **Discuta primeiro.** Para qualquer coisa além de correções pequenas, abra uma issue descrevendo o problema e a proposta. Confira o escopo do MVP em [docs/product-overview.md](docs/product-overview.md). Itens fora do MVP provavelmente não serão aceitos agora.
2. **Especifique.** Mudanças relevantes começam como proposta em `openspec/changes/<nn>-<nome>/` (`proposal.md`, `design.md`, `tasks.md` e specs delta). Os artefatos são escritos em pt-BR, com as palavras-chave SHALL/MUST em inglês.
3. **Implemente.** Siga as regras de [AGENTS.md](AGENTS.md). Elas valem para pessoas e para agentes de IA, e as de segurança (§8) não são negociáveis.
4. **Teste.** Rode a suíte completa antes de abrir o PR:
   ```bash
   ./mvnw test
   ```
5. **Abra o PR** descrevendo o que mudou, como foi testado e o que ficou pendente.

## Padrões de código

- Kotlin idiomático, seguindo o estilo oficial (`kotlin.code.style=official`) e as diretrizes de [AGENTS.md §6](AGENTS.md).
- Mudanças pequenas e focadas. Não misture refatorações não relacionadas.
- Inclua testes para casos positivos, negativos e de borda. Para regras de acesso, inclua sempre o isolamento entre organizações.
- Não adicione dependências sem justificar a necessidade, a licença (que precisa ser compatível com a AGPL-3.0) e a superfície de ataque.
- Nunca inclua segredos, chaves, tokens ou dados reais de clientes, nem no código, nem nos testes, nem nos logs.

## Mensagens de commit

Use [Conventional Commits](https://www.conventionalcommits.org/pt-br/), como no histórico do projeto:

```
feat: adiciona revogação de links temporários
fix: impede listagem de documentos de outra organização
docs: atualiza modelo de domínio
test: cobre expiração de sessão
chore: atualiza wrapper do Maven
```

## Dúvidas

Abra uma issue com a sua pergunta. Para licenciamento comercial, entre em contato com o mantenedor.
