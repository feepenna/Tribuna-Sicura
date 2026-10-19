# Domain Model — Tribuna §icura

Modelo conceitual do domínio do MVP, derivado dos requisitos funcionais existentes. É independente da implementação e não implica que segredos criptográficos sejam persistidos nas entidades de negócio. [^https://app.notion.com/p/3d0529fbaa7d8071b330c706d9093c27]

## 1. Contextos do domínio

- **Identidade e organizações:** autenticação, usuários, membros, papéis e isolamento entre organizações.
- **Documentos:** ciclo de vida do documento, artefatos protegidos e metadados.
- **Criptografia e assinatura:** referências ao cofre, certificados e evidências de assinatura.
- **Compartilhamento e autorização:** concessões de acesso interno ou via link temporário.
- **Auditoria:** trilha imutável de eventos relevantes.

## 2. Entidades

| Entidade | Identidade e dados principais | Responsabilidade |
|---|---|---|
| **Organization** | `organization_id`, nome, estado, configurações básicas | Fronteira de tenant e proprietária dos documentos e políticas da organização. |
| **User** | `user_id`, e-mail, estado de conta, credenciais de autenticação | Identidade autenticável da pessoa. Credenciais e segredos não devem ser armazenados em claro. |
| **Membership** | `membership_id`, `organization_id`, `user_id`, papel, estado | Vincula usuário à organização e define autorização organizacional. |
| **Document** | `document_id`, `organization_id`, `owner_user_id`, nome, estado, `created_at`, `updated_at` | Agregado de negócio para o documento protegido e seu ciclo de vida. |
| **DocumentArtifact** | `artifact_id`, `document_id`, tipo/formato, referência de armazenamento, hash, algoritmo e versão criptográfica | Representa o PDF protegido (e, se necessário, o original controlado). Guarda referências/metadados, nunca a chave em claro. |
| **KeyReference** | `key_reference_id`, `artifact_id`, identificador no cofre/KMS, algoritmo, versão, estado | Referencia material criptográfico gerido pelo cofre. O valor da chave não é atributo da entidade. |
| **CertificateCredential** | `certificate_id`, titular, referência ao cofre, emissor, série, validade, estado | Representa o certificado A1 cadastrado de forma cifrada; a chave privada não sai do ambiente seguro. |
| **Signature** | `signature_id`, `document_id`, `signer_user_id`, `certificate_id`, padrão, instante, hash assinado, resultado de validação, evidência de timestamp | Registra uma assinatura PAdES e seu estado verificável. |
| **ShareGrant** | `share_id`, `document_id`, criador, modo (`internal`/`link`), público-alvo, permissões, expiração, revogação, hash do token | Concede acesso delimitado. Token bruto não deve ser persistido como dado recuperável. |
| **AccessEvent** | `event_id`, organização, documento, ator (ou tentativa anônima), ação, instante, resultado, contexto mínimo | Registro append-only de exportação, assinatura, acesso, negação e revogação. |
| **AuthSession** | `session_id`, `user_id`, instante de emissão/expiração, estado, fator de autenticação | Controla sessão, timeout e evidência de autenticação forte. |

## 3. Relações e cardinalidades

- `Organization 1 — N Membership`; cada Membership aponta para exatamente uma User e uma Organization.
- `Organization 1 — N Document`; cada Document pertence a uma única organização e tem um proprietário User membro dessa organização.
- `Document 1 — N DocumentArtifact`; o documento pode ter artefatos, mas a primeira versão do MVP pode limitar-se ao PDF exportado.
- `DocumentArtifact 1 — 1..N KeyReference` conforme rotação/versões de chave; não se presume uma política específica de rotação.
- `User 1 — N CertificateCredential`; certificados pertencem a um titular e são protegidos pelo cofre.
- `Document 1 — 0..N Signature`; cada Signature referencia exatamente um signer e um certificado.
- `Document 1 — 0..N ShareGrant`; concessões podem ser revogadas ou expirar sem apagar a trilha de auditoria.
- `Document 1 — 0..N AccessEvent`; eventos também apontam para Organization e ator quando identificado.
- `User 1 — 0..N AuthSession`; sessões expiradas ou encerradas deixam de autorizar ações.

## 4. Regras de negócio e invariantes

1. Toda leitura exige identidade/sessão válida ou um link válido, além de autorização para o documento.
2. A organização do usuário, documento e concessão precisa ser consistente; nenhuma concessão pode cruzar tenants sem uma regra explícita.
3. **Modelo A:** exige usuário autenticado e permissão interna vigente.
4. **Modelo C:** exige token válido, não expirado e não revogado; persistir somente representação não reversível do token quando viável.
5. Uma abertura bem-sucedida valida a assinatura e registra um evento de auditoria; tentativas negadas também são registradas sem descriptografar o documento.
6. O documento armazenado e compartilhado é cifrado. A chave e a chave privada do certificado nunca são enviadas em claro ao cliente nem incorporadas ao PDF.
7. O leitor usa dados em claro apenas temporariamente em memória, conforme o requisito atual; a aplicação deve minimizar cópia/cache e documentar os limites técnicos.
8. Revogar uma concessão impede acessos futuros por aquela concessão, sem apagar eventos históricos.
9. Estado de assinatura (válida, inválida ou expirada) é separado do estado do compartilhamento e do estado do documento.
10. Logs de auditoria são append-only e não devem conter chaves, senhas, token bruto ou conteúdo sensível do documento.

## 5. Agregados sugeridos

- **Organization** (raiz): Memberships e políticas básicas; aplicar isolamento de tenant nas operações.
- **Document** (raiz): metadados do documento, artefatos, assinaturas e concessões relacionadas; definir limites transacionais para operações concorrentes.
- **CertificateCredential** (raiz): ciclo de vida e referência segura ao certificado no cofre.
- **AccessEvent** (raiz imutável): eventos aceitos/negados, gravados por serviço de auditoria.
- **AuthSession** (raiz): emissão, expiração e encerramento da sessão.

## 6. Serviços de domínio / casos de uso

- `AuthenticateUser` — autenticar, cumprir 2FA e criar sessão.
- `ExportSecureDocument` — receber exportação do Word, gerar artefato protegido e auditar.
- `SignDocument` — assinar PAdES usando certificado no cofre e guardar evidências.
- `CreateShareGrant` / `RevokeShareGrant` — autorizar compartilhamento e controlar sua vigência.
- `OpenSecureDocument` — autenticar/autorizar, validar assinatura, solicitar leitura segura e registrar resultado.
- `AuditAccess` — registrar eventos sem incluir segredos ou conteúdo desnecessário.

## 7. Eventos de domínio relevantes

`DocumentExported`, `DocumentSigned`, `ShareGrantCreated`, `ShareGrantRevoked`, `DocumentOpened`, `AccessDenied`, `CertificateRegistered`, `SignatureValidationCompleted`.

## 8. Delimitação e premissas

- Plugin Word, leitor web, cofre/KMS e serviços de autenticação são adaptadores/serviços de aplicação, não entidades centrais do domínio.
- Os requisitos atuais não definem versionamento de documentos, hierarquia detalhada de permissões ou esquema criptográfico de envelope; tratá-los como decisões de projeto, não como fatos implementados.
- Chat E2EE, IA, apps móveis, integrações processuais e biblioteca jurídica não fazem parte deste modelo do MVP. [^https://app.notion.com/p/2d4529fbaa7d80bf8a54f252b23b3326] [^https://app.notion.com/p/3d0529fbaa7d8071b330c706d9093c27]

## Referências internas

- [MVP — requisitos funcionais](https://app.notion.com/p/3d0529fbaa7d8071b330c706d9093c27)
- [Tribuna §icura — escopo do projeto](https://app.notion.com/p/2d4529fbaa7d80bf8a54f252b23b3326)
