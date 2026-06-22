<div align="center">

# 📦 Stock Manager API

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)](https://jwt.io/)
[![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)

[![GitHub release](https://img.shields.io/github/v/release/DouglasLira-Dev/stock-manager-api?style=flat-square)](https://github.com/DouglasLira-Dev/stock-manager-api/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/DouglasLira-Dev/stock-manager-api?style=flat-square)](https://github.com/DouglasLira-Dev/stock-manager-api/commits/main)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-17%20passed-brightgreen?style=flat-square)](#)

</div>

---

## 🚀 Sobre o Projeto

API REST para controle de inventário/estoque, desenvolvida como projeto de portfólio para demonstrar proficiência em **Spring Boot**, **Spring Security**, **JWT**, **testes unitários** e boas práticas de engenharia de software.

---

## ✨ Funcionalidades (v1.3.0)

### 🔐 Autenticação e Segurança
- ✅ Cadastro e login de usuários com JWT (stateless)
- ✅ Senhas criptografadas com BCrypt
- ✅ Perfil do usuário (`GET /api/users/me`)

### 📦 Gerenciamento de Estoque
- ✅ CRUD completo de produtos
- ✅ Regras de negócio: Entrada e Saída de Estoque (com validação de quantidade negativa)
- ✅ Listagem de produtos com estoque baixo (< 5 unidades)
- ✅ Paginação e filtros por categoria e nome

### 📚 Documentação e Monitoramento
- ✅ Documentação interativa com Swagger/OpenAPI
- ✅ Health Check com Spring Boot Actuator

### 🧪 Qualidade e Testes
- ✅ Tratamento global de exceções
- ✅ 17 testes unitários (AuthService e ProductService)
- ✅ Código em camadas (Controller → Service → Repository)

---

## 🛠️ Tecnologias

| Categoria | Tecnologias |
| :--- | :--- |
| **Back-end** | Java 17, Spring Boot 3.4.4, Spring Security 6 |
| **Banco de Dados** | SQLite, Hibernate (JPA), Spring Data JPA |
| **Autenticação** | JWT (jjwt 0.12.6) |
| **Documentação** | SpringDoc OpenAPI (Swagger UI) |
| **Monitoramento** | Spring Boot Actuator |
| **Testes** | JUnit 5, Mockito |
| **Build** | Maven |

---

## 📂 Estrutura do Projeto
```
src/
├── main/
│ ├── java/com/estoque/api/
│ │ ├── controller/ # Endpoints REST
│ │ ├── service/ # Regras de negócio
│ │ ├── repository/ # Acesso a dados (JPA)
│ │ ├── model/ # Entidades JPA
│ │ ├── dto/ # Objetos de transferência
│ │ ├── security/ # Configurações de segurança e JWT
│ │ ├── config/ # Configurações (Swagger, etc.)
│ │ └── exception/ # Tratamento global de erros
│ └── resources/
│ └── application.properties
└── test/
└── java/com/estoque/api/service/
├── AuthServiceTest.java # 5 testes
└── ProductServiceTest.java # 12 testes
```


---

## ⚙️ Como Executar

### Pré-requisitos
- JDK 17 ou 21
- Maven 3.9+

### Passos

```bash
# Clone o repositório
git clone https://github.com/DouglasLira-Dev/stock-manager-api.git

# Navegue até a pasta
cd stock-manager-api

# Compile e execute
mvn clean spring-boot:run

#A aplicação estará disponível em http://localhost:8080.

## Executar os Testes

```bash
mvn test
```
---
## 📮 Endpoints Principais

### 🔐 Autenticação

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|---------------|
| POST | `/auth/register` | Cadastrar um novo usuário | Público |
| POST | `/auth/login` | Autenticar e obter token JWT | Público |
| GET | `/api/users/me` | Obter dados do usuário autenticado | JWT (Bearer) |

### 📦 Produtos

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|---------------|
| POST | `/api/products` | Criar um novo produto | JWT (Bearer) |
| GET | `/api/products` | Listar produtos (paginação e filtros) | JWT (Bearer) |
| GET | `/api/products/{id}` | Buscar produto por ID | JWT (Bearer) |
| PUT | `/api/products/{id}` | Atualizar produto | JWT (Bearer) |
| DELETE | `/api/products/{id}` | Deletar produto | JWT (Bearer) |
| PATCH | `/api/products/{id}/stock/add` | Entrada de estoque (adicionar quantidade) | JWT (Bearer) |
| PATCH | `/api/products/{id}/stock/remove` | Saída de estoque (remover com validação) | JWT (Bearer) |
| GET | `/api/products/low-stock` | Listar produtos com estoque baixo (< 5) | JWT (Bearer) |

### 📊 Monitoramento

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|---------------|
| GET | `/actuator/health` | Health Check da aplicação | Público |

## 🔗 Documentação Interativa (Swagger)

A documentação completa está disponível automaticamente em:

```
http://localhost:8080/swagger-ui.html
```

A interface permite:
- Visualizar todos os endpoints e modelos (DTOs)
- Testar as requisições diretamente pelo navegador
- Autenticar com JWT (botão "Authorize")

## 🧪 Testes

O projeto conta com 17 testes unitários:

| Classe de Teste | Quantidade | Cenários |
|------------------|------------|----------|
| AuthServiceTest | 5 testes | Registro, login, validações |
| ProductServiceTest | 12 testes | CRUD, regras de estoque, filtros |

```bash
# Executar todos os testes
mvn test
```

## 📝 Licença

Este projeto está sob a licença MIT. Consulte o arquivo `LICENSE` para obter mais detalhes.

## 📧 Contato

**Desenvolvedor:** Douglas Lira  
**GitHub:** [DouglasLira-Dev](https://github.com/DouglasLira-Dev)

---

⭐ Se este projeto te ajudou de alguma forma, considere deixar uma estrela no repositório! Isso ajuda muito na visibilidade do projeto.