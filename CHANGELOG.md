# Changelog

Todas as mudanças notáveis neste projeto serão documentadas aqui.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

---

## [3.1.0] - 2026-06-22

### ✨ Adicionado
- GitHub Actions para construir imagem Docker automaticamente
- Workflow `docker-build.yml` com build e artefato
- Badge de build status no README

---

## [3.0.0] - 2026-06-22

### ✨ Adicionado
- Migração de SQLite para PostgreSQL
- Flyway para versionamento do banco de dados
- Dockerfile para containerização da aplicação
- docker-compose.yml com PostgreSQL e app
- application-docker.properties para ambiente Docker
- Suporte a arquitetura ARM64

### 🔧 Alterado
- Atualização do Flyway para suporte ao PostgreSQL 17
- Configuração de ambiente para Docker

---

### Atualizar versão `[2.1.0]`
**Onde:** Na linha `[2.1.0] - 2026-06-22`

**Adicionar:**
```markdown
### 🔧 Corrigido
- Exceções customizadas com códigos de erro
- Respostas de erro padronizadas (ErrorResponse)
- Logs com SLF4J em todos os Services

---

## [2.1.0] - 2026-06-22

### ✨ Adicionado
- Endpoint `GET /api/categories` para listar categorias únicas
- Campo `minimumQuantity` no Product com validação
- Endpoint `GET /api/products/below-minimum` para produtos abaixo do mínimo
- Histórico de movimentações (ENTRY/EXIT)
- Endpoint `GET /api/movimentations` com paginação
- 3 novos testes unitários (total: 20)

### 🔧 Corrigido
- Remoção do Lombok e substituição por getters/setters manuais
- Injeção do MovimentationService no ProductService

---

## [1.4.0] - 2026-06-21

### ✨ Adicionado
- Endpoint `GET /api/products/stats` para estatísticas do estoque
- 3 testes unitários para estatísticas

---

## [1.3.0] - 2026-06-21

### ✨ Adicionado
- Endpoint `GET /api/users/me` para perfil do usuário
- Testes unitários para AuthService (5 testes)
- Testes unitários para ProductService (12 testes)

---

## [1.2.0] - 2026-06-20

### ✨ Adicionado
- Swagger/OpenAPI para documentação interativa
- Health Check com Spring Boot Actuator
- Configuração de segurança para rotas do Swagger e Actuator

### 🔧 Removido
- Lombok (substituído por getters/setters manuais)

---

## [1.1.0] - 2026-06-19

### ✨ Adicionado
- CRUD completo de produtos
- Regras de negócio: entrada e saída de estoque
- Filtros por categoria e nome
- Paginação
- Listagem de produtos com estoque baixo

---

## [1.0.0] - 2026-06-18

### ✨ Adicionado
- Autenticação com JWT (registro/login)
- Senhas criptografadas com BCrypt
- Modelos: User, Product, Role
- Estrutura de camadas (Controller, Service, Repository)
- Tratamento global de exceções

---