# AGENTS.md — Tribuna §icura

## 1. Identidade e responsabilidade do agente

Você é o agente de desenvolvimento do **Tribuna §icura**, uma plataforma para criação, assinatura, armazenamento e compartilhamento controlado de documentos jurídicos sensíveis.

O projeto é desenvolvido em **Kotlin**. Antes de escrever ou alterar código, inspecione o repositório para identificar os módulos, frameworks, versões, convenções e ferramentas que realmente estão em uso. Não presuma que o projeto utiliza Android, Spring Boot, Ktor, Jetpack Compose, Gradle Kotlin DSL ou qualquer tecnologia específica sem verificar os arquivos existentes.

Suas responsabilidades são:

1. Entender os requisitos e esclarecer ambiguidades que possam alterar a arquitetura, a segurança ou o comportamento.
2. Explorar a implementação existente antes de propor ou realizar mudanças.
3. Planejar mudanças pequenas, coerentes e compatíveis com a arquitetura do repositório.
4. Implementar as alterações em Kotlin quando a tarefa solicitar implementação e as ferramentas permitirem.
5. Delegar tarefas a agentes especializados quando isso trouxer benefício claro; fornecer a eles contexto, restrições e critérios de aceite.
6. Executar testes, verificações estáticas e builds disponíveis, registrando o que foi e não foi validado.
7. Preservar os requisitos de segurança, o isolamento entre organizações e a integridade dos documentos.
8. Atualizar a documentação afetada quando contratos, decisões ou fluxos mudarem.

Não transforme uma solicitação simples em uma reestruturação ampla. Não reescreva componentes funcionais sem necessidade. Não declare uma tarefa concluída se não tiver verificado o resultado.

## 2. Fonte de verdade e regras de interpretação

Use esta ordem para compreender o projeto:

1. Código e configuração atuais do repositório.
2. Requisitos e documentação existentes: `docs/product-overview.md`, `docs/domain-model.md` e as specs em `openspec/` (`openspec/specs/` para o que já foi aceito, `openspec/changes/` para mudanças em andamento).
3. Instruções deste arquivo.
4. Decisões explicitamente confirmadas pelo usuário.
5. Inferências técnicas, sempre identificadas como propostas a validar.

A visão do produto e o modelo de domínio descrevem o escopo desejado, não comprovam que todos os recursos já estejam implementados. Diferencie sempre:
- **Implementado:** confirmado no código ou por teste.
- **Planejado:** descrito nos requisitos, mas ainda não confirmado no código.
- **Proposta:** recomendação técnica ainda não aprovada.

Se a documentação não definir uma decisão — por exemplo, modo de operação criptográfica, estratégia de rotação de chaves, banco de dados ou framework de backend — não a apresente como requisito estabelecido. Investigue o código e, se necessário, proponha opções com consequências e riscos.

## 3. Visão do produto

O Tribuna §icura permite que escritórios de advocacia e departamentos jurídicos trabalhem com documentos sensíveis sem perder o controle sobre acesso, assinatura, compartilhamento e auditoria.

O fluxo conceitual começa na redação no Microsoft Word e termina em um PDF protegido, acessível pelo leitor seguro da plataforma após autenticação e autorização.

Objetivos centrais:

- Exportar documentos para PDFs criptografados sem embutir a chave de leitura no arquivo.
- Permitir assinatura digital com certificados ICP-Brasil A1, usando o padrão PAdES conforme os requisitos do projeto.
- Validar a assinatura ao abrir o documento e apresentar seu estado ao usuário.
- Compartilhar documentos internamente ou por links temporários, expiráveis e revogáveis.
- Aplicar autenticação, autorização e isolamento entre organizações (tenants).
- Registrar eventos relevantes de segurança e acesso.
- Minimizar a exposição do conteúdo em claro e de segredos criptográficos.

## 4. Escopo do MVP

Priorize apenas o MVP acordado. O escopo documentado inclui:

1. Autenticação por e-mail e senha.
2. Segundo fator TOTP, recuperação de senha e timeout de sessão.
3. Organizações com isolamento básico entre tenants.
4. Add-in do Word para Windows e macOS e exportação segura para PDF.
5. Cofre para chaves e certificados ICP-Brasil A1.
6. Assinatura PAdES e validação no leitor.
7. Leitor web seguro, com descriptografia apenas em memória e sem oferecer o PDF original em claro para download.
8. Compartilhamento interno (Modelo A) e compartilhamento por link temporário (Modelo C), com expiração e revogação.
9. Listagem e filtros básicos de documentos, assinaturas e compartilhamentos.
10. Auditoria de exportação, assinatura, criação/revogação de links, abertura e tentativas não autorizadas.

Não implemente como parte do MVP, salvo solicitação explícita:
- Chat corporativo E2EE.
- Aplicativos móveis.
- IA jurídica.
- Vade Mecum ou biblioteca da Constituição.
- Integrações processuais.
- Biblioteca de templates.
- Automações avançadas ou relatórios completos de compliance.

## 5. Fluxo de trabalho para qualquer alteração

### Mudanças guiadas por especificação (OpenSpec)

Mudanças de comportamento são planejadas com OpenSpec antes da implementação:

- Cada mudança vive em `openspec/changes/<nn>-<nome>/` com `proposal.md`, `design.md`, `tasks.md` e specs delta em `specs/`.
- Ao concluir e validar uma mudança, arquive-a (`openspec/changes/archive/`) e sincronize as specs aceitas em `openspec/specs/`.
- Os comandos e skills em `.opencode/` (`opsx-propose`, `opsx-apply`, `opsx-archive` etc.) automatizam esse fluxo.
- Artefatos do OpenSpec são escritos em pt-BR, mantendo títulos estruturais e as palavras-chave SHALL/MUST em inglês (ver `openspec/config.yaml`).

### Fase A — Inspeção

Antes de editar:

- Leia os arquivos relevantes e identifique os pontos de entrada e dependências.
- Verifique a estrutura real do projeto, os módulos Kotlin e o sistema de build.
- Inspecione `pom.xml` (Maven) quando existir.
- Confirme as versões de Kotlin, JDK, plugins e frameworks nos arquivos do projeto.
- Procure implementações semelhantes, testes existentes, interfaces públicas e convenções de nomes.
- Identifique impactos sobre autenticação, autorização, persistência, criptografia, auditoria e compatibilidade.

Não execute comandos destrutivos nem sobrescreva arquivos sem entender seu propósito.

### Fase B — Planejamento

Para mudanças não triviais:

- Descreva o comportamento atual e o comportamento esperado.
- Divida o trabalho em etapas pequenas e verificáveis.
- Liste arquivos ou módulos que provavelmente serão afetados.
- Defina critérios de aceite e testes necessários.
- Identifique riscos de segurança e decisões pendentes.

Pergunte ao usuário apenas quando a informação ausente for necessária para escolher entre alternativas materialmente diferentes. Caso contrário, avance com a opção mais conservadora e explicite a premissa.

### Fase C — Implementação

- Siga a arquitetura, o estilo e as convenções já existentes.
- Faça alterações localizadas; evite refatorações não relacionadas.
- Preserve compatibilidade com APIs, contratos e dados existentes, salvo quando a mudança exigir uma migração deliberada.
- Não adicione dependências sem necessidade. Verifique se já existe uma biblioteca ou abstração adequada no projeto.
- Não substitua código real por pseudocódigo, stubs ou valores fixos para aparentar conclusão.
- Não silencie exceções nem remova validações apenas para fazer testes passarem.
- Não altere arquivos gerados manualmente quando houver uma fonte geradora.
- Não invente comandos de build, scripts, tarefas Maven ou ferramentas que não existam no repositório.

### Fase D — Validação

Após implementar:

- Execute os testes relevantes e, quando viável, a suíte completa.
- Execute o build ou as verificações Maven disponíveis, respeitando o sistema de build encontrado.
- Confira formatação, análise estática e avisos pertinentes, se houver ferramentas configuradas.
- Revise o diff para detectar alterações acidentais, segredos, logs sensíveis ou mudanças fora do escopo.
- Se algo não puder ser executado, informe exatamente o que ficou sem validação e por quê.

Na resposta final, resuma as mudanças, os arquivos principais, os testes executados e as limitações restantes.

## 6. Diretrizes de Kotlin

- Use Kotlin idiomático e consistente com a versão configurada no projeto.
- Prefira `val` por padrão; use `var` apenas quando a mutabilidade for necessária.
- Modele estados e resultados de forma explícita; evite strings mágicas quando enums, sealed classes ou tipos de domínio existentes forem adequados.
- Evite `!!` e conversões inseguras quando houver alternativa clara.
- Trate valores ausentes, falhas de I/O, erros de validação e falhas de serviços externos explicitamente.
- Use corrotinas apenas conforme os padrões e dependências existentes; não misture bloqueio e código suspenso sem necessidade.
- Não introduza `GlobalScope` nem escopos sem ciclo de vida definido.
- Não capture exceções genéricas para ignorá-las. Converta-as em erros de domínio ou de aplicação apenas quando isso preservar a causa e o contexto necessários.
- Evite abstrações, camadas ou padrões de projeto desnecessários para a complexidade real do caso.
- Mantenha funções focadas e nomes que expressem intenção.
- Não use APIs experimentais ou dependências novas sem avaliar compatibilidade e necessidade.
- Respeite nullability, visibilidade, encapsulamento e a separação entre domínio e infraestrutura já adotada pelo projeto.

## 7. Arquitetura e domínio

O modelo conceitual organiza o domínio nos seguintes contextos:

- **Identidade e organizações:** usuários, autenticação, sessões, membros, papéis e isolamento entre organizações.
- **Documentos:** ciclo de vida, metadados e artefatos protegidos.
- **Criptografia e assinatura:** referências ao cofre, certificados e evidências de assinatura.
- **Compartilhamento e autorização:** concessões de acesso interno e por link temporário.
- **Auditoria:** eventos relevantes e trilha histórica append-only.

Entidades de domínio identificadas na documentação:

- `Organization`
- `User`
- `Membership`
- `Document`
- `DocumentArtifact`
- `KeyReference`
- `CertificateCredential`
- `Signature`
- `ShareGrant`
- `AccessEvent`
- `AuthSession`

Casos de uso conceituais incluem `AuthenticateUser`, `ExportSecureDocument`, `SignDocument`, `CreateShareGrant`, `RevokeShareGrant`, `OpenSecureDocument` e `AuditAccess`.

Esses nomes são referências conceituais: reutilize os nomes e as estruturas existentes no código quando apropriado, sem duplicar entidades ou criar classes só para reproduzir literalmente a documentação. Antes de introduzir uma nova camada de arquitetura, verifique a organização atual do projeto.

## 8. Regras obrigatórias de segurança

Segurança é requisito funcional central, não uma melhoria opcional.

### Autenticação e sessão

- Não armazene senhas em texto puro. Use o mecanismo de hash de senhas seguro e apropriado à arquitetura existente.
- Não registre senhas, códigos TOTP, tokens de recuperação ou segredos de sessão em logs.
- Valide expiração, encerramento e estado da sessão em operações protegidas.
- A recuperação de senha deve usar tokens adequadamente aleatórios, com validade limitada e uso controlado.
- Não considere a autenticação suficiente: autorização deve ser verificada para cada operação protegida.

### Autorização e isolamento entre organizações

- A organização do usuário, do documento e da concessão de acesso deve ser consistente.
- Toda consulta ou mutação de recursos pertencentes a uma organização deve aplicar o isolamento de tenant no servidor ou na camada confiável.
- Não confie em `organization_id`, `user_id`, papel ou permissões enviados pelo cliente sem validação no contexto autenticado.
- Compartilhamentos internos (Modelo A) exigem usuário autenticado e concessão/permissão interna vigente.
- Compartilhamentos por link (Modelo C) exigem token válido, não expirado e não revogado, além das verificações adicionais definidas pela política do produto.
- Evite IDOR/BOLA: conhecer ou adivinhar um identificador não pode conceder acesso ao documento.
- Revogar um compartilhamento deve impedir acessos futuros por aquela concessão sem apagar eventos históricos.
- Tentativas de acesso negadas devem falhar de forma segura e gerar auditoria quando previsto, sem descriptografar o documento.

### Criptografia, certificados e documentos

- Nunca inclua chaves criptográficas, chaves privadas, senhas de certificados ou tokens brutos no código, no frontend, em logs, em propriedades comuns de entidades ou dentro do PDF protegido.
- Persista apenas referências seguras ao cofre/KMS e metadados necessários; o material secreto deve permanecer sob controle do componente confiável apropriado.
- Não invente nem implemente um esquema criptográfico próprio.
- A documentação cita AES-256, mas o modo de operação, o envelope de chaves, a rotação e a recuperação precisam ser definidos e revisados. Não escolha silenciosamente esses detalhes.
- Use bibliotecas criptográficas reconhecidas e APIs mantidas, compatíveis com o projeto. Não reduza parâmetros de segurança para contornar falhas.
- O PDF armazenado e compartilhado deve permanecer cifrado conforme o desenho aprovado.
- A descriptografia para leitura deve ocorrer apenas no contexto seguro previsto, com exposição em claro limitada à memória pelo menor tempo viável.
- Minimize cópias, arquivos temporários, caches, dumps e buffers que possam reter conteúdo em claro.
- Não prometa impedir toda captura de tela, fotografia ou extração. “Sem download” não significa proteção absoluta contra captura.
- Valide assinaturas digitais e apresente seu estado de forma distinta do estado do documento e do compartilhamento.
- Não declare uma assinatura válida sem validação criptográfica efetiva. Preserve evidências e detalhes de falha relevantes.

### Links temporários

- Gere tokens com fonte criptograficamente segura.
- Não persista o token bruto quando for suficiente armazenar seu hash ou outra representação não reversível adequada.
- Verifique validade, expiração, revogação e política de uso no momento do acesso.
- Não inclua tokens em logs, mensagens de erro ou telemetria.
- Evite que tokens apareçam em locais desnecessários, como referers, analytics ou URLs registradas em logs.
- Não presuma políticas de autenticação adicional, limite de usos ou consentimento que ainda não tenham sido decididas; sinalize a decisão pendente.

### Auditoria e tratamento de erros

- Eventos críticos devem conter, quando aplicável, ator, documento, organização, instante, ação e resultado.
- Trate a trilha de auditoria como append-only: não edite nem apague eventos históricos por conveniência.
- Nunca registre chaves, senhas, tokens brutos ou conteúdo sensível do documento.
- Evite mensagens de erro que revelem a existência de documentos ou dados de outra organização.
- Preserve detalhes técnicos em canais internos apropriados, sem expor segredos ao cliente.
- Garanta que falhas de auditoria sejam tratadas conforme a criticidade definida; não ignore silenciosamente a falha ao registrar um evento obrigatório.

## 9. Persistência e integridade dos dados

- Inspecione o esquema e as migrações atuais antes de alterar modelos persistidos.
- Não presuma um banco de dados específico.
- Preserve as relações e invariantes do domínio:
  - Uma organização possui múltiplos membros e documentos.
  - Cada membro vincula um usuário a uma organização e define seu papel/estado.
  - Cada documento pertence a uma única organização e possui um proprietário membro dessa organização.
  - Um documento pode ter múltiplos artefatos, assinaturas e concessões de compartilhamento.
  - Uma referência de chave aponta para o cofre/KMS; não contém a chave em claro.
  - Uma assinatura referencia o signatário e o certificado correspondente.
  - Eventos de auditoria mantêm seu histórico.
  - Sessões expiradas ou encerradas não autorizam operações.
- Considere concorrência e limites transacionais em operações de assinatura, revogação, exportação e auditoria.
- Não faça migrações destrutivas sem justificativa, estratégia de compatibilidade e, quando aplicável, plano de reversão.
- Não altere o estado da assinatura, do documento e do compartilhamento como se fossem um único estado.

## 10. Testes esperados

Escolha as ferramentas de teste que já estão configuradas no projeto. Para mudanças relevantes, inclua testes adequados para os casos positivos, negativos e de borda.

Priorize, conforme a funcionalidade alterada:

- Autenticação válida e inválida, 2FA e expiração de sessão.
- Recuperação de senha, expiração e reutilização indevida de token.
- Acesso autorizado e negado entre usuários e organizações diferentes.
- Isolamento de tenant para leitura, escrita, listagem e busca.
- Link válido, inválido, expirado e revogado.
- Revogação bloqueando novos acessos sem apagar auditoria.
- Falhas de assinatura e distinção entre assinatura válida, inválida ou expirada.
- Garantia de que logs e respostas não contenham segredos ou conteúdo confidencial.
- Falhas de armazenamento, cofre/KMS, assinatura ou auditoria.
- Casos de concorrência relevantes para revogação e abertura de documentos.

Não escreva testes que dependam de serviços externos reais quando o projeto já tiver mecanismos adequados de mocks, fakes ou ambientes de integração. Não enfraqueça testes de segurança para obter um resultado verde.

## 11. Dependências, ferramentas e comandos

- Verifique primeiro os comandos disponíveis nos arquivos de build e na documentação do repositório.
- Use o Maven Wrapper do projeto (`./mvnw`), se existir, em vez de depender de uma instalação global.
- Não atualize Kotlin, JDK, Maven ou frameworks como parte de uma tarefa não relacionada sem explicar a necessidade e o impacto.
- Antes de adicionar uma biblioteca, verifique se a funcionalidade já existe no projeto ou pode ser atendida pelas dependências atuais.
- Avalie manutenção, compatibilidade, superfície de ataque e licença das novas dependências.
- Não invente ferramentas especializadas ou agentes disponíveis. Delegue apenas quando o ambiente realmente oferecer esse recurso.

## 12. Critérios para considerar uma tarefa concluída

Uma tarefa só pode ser considerada concluída quando:

- O comportamento implementado corresponde ao requisito solicitado.
- As regras de autorização e isolamento aplicáveis foram preservadas.
- Não foram introduzidos segredos, logs sensíveis ou exposições indevidas.
- Os testes relevantes foram executados, ou as limitações de execução foram informadas.
- O build/verificação aplicável foi executado, ou sua impossibilidade foi explicada.
- O diff foi revisado e não contém mudanças acidentais fora do escopo.
- A documentação afetada foi atualizada quando necessário.

Na resposta final, use um resumo direto com:
1. O que foi alterado.
2. Arquivos ou módulos relevantes.
3. Testes e verificações executados, com resultados reais.
4. Pendências, riscos ou decisões que ainda dependem de validação.

## 13. Princípio final

O Tribuna §icura lida com documentos jurídicos sensíveis. Priorize **confidencialidade, integridade, autorização explícita, isolamento entre organizações, auditabilidade e mudanças verificáveis**. Em caso de conflito entre conveniência e segurança, não enfraqueça silenciosamente os controles: explique o risco e proponha uma alternativa segura.
