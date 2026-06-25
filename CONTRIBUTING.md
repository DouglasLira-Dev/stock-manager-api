# Guia de Contribuição

Obrigado por considerar contribuir com este projeto! 🎉

## Como Contribuir

### 1. Reportar Issues
- Verifique se a issue já não foi reportada
- Use o template de issue
- Descreva o problema detalhadamente

### 2. Enviar Pull Requests
- Fork o repositório
- Crie uma branch: `git checkout -b feature/nova-funcionalidade`
- Faça suas alterações
- Execute os testes: `mvn test`
- Commit: `git commit -m "feat: adiciona nova funcionalidade"`
- Push: `git push origin feature/nova-funcionalidade`
- Abra um Pull Request

### 3. Padrão de Commits
Use [Conventional Commits](https://www.conventionalcommits.org/):

| Tipo | Descrição |
| :--- | :--- |
| `feat:` | Nova funcionalidade |
| `fix:` | Correção de bug |
| `docs:` | Documentação |
| `test:` | Testes |
| `chore:` | Tarefas de build/manutenção |

### 4. Padrão de Branch
- `main` - produção
- `develop` - desenvolvimento
- `feature/*` - novas funcionalidades
- `fix/*` - correções de bugs
- `hotfix/*` - correções urgentes

---

## 🧪 Testes

Antes de enviar um PR, execute os testes:

```bash
mvn test
#todos o testes devem passar.

```
---

## 🐳 Docker

O projeto suporta execução via Docker:

```bash
# Construir a imagem
docker build -t stock-manager-api:latest .

# Subir os containers
docker-compose up -d

# Parar os containers
docker-compose down

A imagem também é construída automaticamente pelo GitHub Actions a cada push na branch main.

---
## 📝 Documentação
Atualize o READM.md se necessário.
---

## 📧 Contato
Desenvolvedor: Douglas Lira
GitHub: DouglasLira-Dev


---

## 📄 `CODE_OF_CONDUCT.md` – Código de Conduta

**Caminho:** `CODE_OF_CONDUCT.md` (raiz do projeto)

```markdown
# Código de Conduta

## Nosso Compromisso

Nós, como membros, contribuidores e líderes, nos comprometemos a tornar a participação em nossa comunidade uma experiência livre de assédio para todos.

## Comportamentos Esperados

- ✅ Uso de linguagem acolhedora e inclusiva
- ✅ Respeito a diferentes pontos de vista
- ✅ Aceitação de críticas construtivas
- ✅ Foco no que é melhor para a comunidade
- ✅ Empatia com outros membros

## Comportamentos Inaceitáveis

- ❌ Uso de linguagem ou imagens sexualizadas
- ❌ Comentários ofensivos, insultos ou ataques pessoais
- ❌ Assédio público ou privado
- ❌ Publicação de informações privadas sem permissão
- ❌ Qualquer conduta que seria inapropriada em um ambiente profissional

## Aplicação

Projetos de código aberto são espaços seguros para todos. Se você testemunhar ou sofrer comportamentos inaceitáveis, entre em contato com o mantenedor do projeto.

---

**Este código de conduta é adaptado do [Contributor Covenant](https://www.contributor-covenant.org/).**