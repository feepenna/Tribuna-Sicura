# Project Overview — Tribuna §icura

## Visão do produto

Tribuna §icura é uma plataforma para escritórios de advocacia e departamentos jurídicos criarem, assinarem, armazenarem e compartilharem documentos jurídicos com controles rigorosos de acesso. O fluxo começa no Microsoft Word e termina em um PDF protegido, aberto apenas no leitor seguro da plataforma após autenticação e autorização. [^https://app.notion.com/p/2d4529fbaa7d80bf8a54f252b23b3326]

## Problema

Documentos jurídicos sensíveis circulam entre editores, PDFs, e-mail e sistemas de terceiros. Isso dificulta controlar quem acessou um documento, revogar compartilhamentos e comprovar a integridade e a assinatura do arquivo.

## Proposta de valor

- Produzir documentos no Word sem mudar o fluxo de redação habitual.
- Exportar PDFs criptografados sem incluir a chave de leitura no arquivo.
- Assinar PDFs com certificado ICP-Brasil e validar a assinatura no acesso.
- Compartilhar com usuários autorizados da organização ou por link temporário revogável.
- Registrar eventos de segurança e acesso por documento e usuário.

## Usuários e atores

- **Membro da organização:** redige, exporta, assina, consulta ou compartilha documentos conforme suas permissões.
- **Administrador da organização:** administra usuários e políticas básicas de acesso.
- **Destinatário interno:** membro autenticado com permissão para abrir um documento.
- **Destinatário por link:** pessoa autorizada por um link temporário, sujeita à política definida para o compartilhamento.
- **Operador de segurança da plataforma:** componente confiável que aplica políticas, usa o cofre e registra auditoria; não é um usuário que recebe chaves em claro.

## Escopo do MVP

1. Autenticação por e-mail e senha, 2FA TOTP, recuperação de senha e timeout de sessão.
2. Organização com isolamento básico entre tenants.
3. Add-in do Word para Windows e Mac e exportação segura para PDF criptografado.
4. Cofre para chaves e certificados ICP-Brasil A1; assinatura PAdES e validação no leitor.
5. Leitor web seguro, com descriptografia apenas em memória e sem disponibilizar o PDF original em claro para download.
6. Compartilhamento interno (Modelo A) e por link com token temporário, expiração e revogação (Modelo C).
7. Listagem e filtros básicos de documentos, assinaturas e compartilhamentos.
8. Auditoria mínima de exportação, assinatura, criação/revogação de link, abertura e tentativa não autorizada. [^https://app.notion.com/p/3d0529fbaa7d8071b330c706d9093c27]

## Fora do MVP

Chat corporativo E2EE, aplicativos móveis, IA jurídica, Vade Mecum/Constituição, integrações processuais, biblioteca de templates, automações avançadas e relatórios completos de compliance ficam para fases posteriores. Esses itens aparecem no escopo amplo, mas não nos requisitos funcionais do MVP atual. [^https://app.notion.com/p/2d4529fbaa7d80bf8a54f252b23b3326] [^https://app.notion.com/p/3d0529fbaa7d8071b330c706d9093c27]

## Fluxo principal

1. Um membro autentica-se e abre ou redige um documento no Word.
2. O add-in solicita a exportação segura; o serviço cria o PDF protegido e, se solicitado, aplica assinatura digital.
3. O sistema armazena o documento e referências seguras a chaves/certificados no cofre, nunca o segredo em claro em propriedades comuns ou no cliente.
4. O membro concede acesso interno ou cria um link temporário.
5. O destinatário autentica-se ou apresenta um token válido; a política verifica organização, permissão, expiração e revogação.
6. O leitor valida a assinatura, usa os serviços criptográficos para leitura em memória e registra o evento de acesso.

## Princípios e critérios de sucesso

- Nenhum acesso sem autenticação/autorização válida; links podem expirar e ser revogados.
- Segredos criptográficos e chave privada não são expostos ao frontend nem embutidos no PDF.
- Eventos críticos ficam associados ao ator, documento, instante e resultado.
- A assinatura é validada no momento da abertura e seu estado é apresentado ao usuário.
- Um destinatário autorizado consegue abrir o documento sem receber a chave real.

## Decisões a validar

- Arquitetura criptográfica concreta (gestão de chaves, rotação, recuperação e separação de funções); AES-256 está no escopo, mas o modo de operação e o envelope de chaves precisam ser especificados e revisados.
- Política para destinatários de links (identificação, autenticação adicional, limite de usos e coleta de consentimento).
- Limites do leitor contra captura de tela, cópia ou extração de conteúdo; “sem download” não equivale a impedir toda forma de captura.
- Requisitos de disponibilidade, retenção, recuperação de desastre, LGPD e operação de certificados/timestamp.

## Referências internas

- [Tribuna §icura — escopo do projeto](https://app.notion.com/p/2d4529fbaa7d80bf8a54f252b23b3326)
- [MVP — requisitos funcionais](https://app.notion.com/p/3d0529fbaa7d8071b330c706d9093c27)
