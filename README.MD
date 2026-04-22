# Course API

API REST de e-commerce desenvolvida com **Java 21** e **Spring Boot 3.4.4**, com autenticação JWT, controle de acesso por roles, dois perfis de ambiente (H2 e PostgreSQL) e documentação via Swagger UI.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.4 | Framework principal |
| Spring Security | (via Boot) | Autenticação e autorização |
| Spring Data JPA | (via Boot) | Persistência de dados |
| Hibernate | 6.6.x | ORM |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| H2 Database | (via Boot) | Banco em memória para testes locais |
| PostgreSQL | 13+ | Banco relacional para produção |
| springdoc-openapi | 2.8.6 | Documentação Swagger UI |
| Maven | 3.x | Gerenciamento de dependências e build |

---

## Entidades do domínio

```
User ──< Order ──< OrderItem >── Product >── Category
                   │
                   └── Payment
```

- **User** — usuário com UUID, email único, senha BCrypt e role (USER/ADMIN)
- **Order** — pedido vinculado a um cliente com status e momento
- **OrderItem** — item de pedido com quantidade e preço
- **Product** — produto com categorias (N:N)
- **Category** — categoria de produto
- **Payment** — pagamento vinculado a um pedido

---

## Estrutura do projeto

```
src/main/java/com/educandoweb/course/
├── config/
│   ├── PasswordEncoderConfig.java   # Bean do BCryptPasswordEncoder
│   ├── SecurityConfig.java          # Regras de autorização + JwtFilter
│   ├── SwaggerConfig.java           # OpenAPI + bearerAuth
│   └── TestConfig.java              # Seed de dados para perfil test
├── dto/
│   ├── LoginRequest.java            # { email, password }
│   └── LoginResponse.java           # { token, id, email, role }
├── entities/
│   ├── User.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Product.java
│   ├── Category.java
│   ├── Payment.java
│   ├── enums/OrderStatus.java
│   └── pk/OrderItemPK.java
├── repositories/                    # Interfaces JpaRepository
├── resources/                       # Controllers REST
│   ├── AuthResource.java            # POST /auth/login
│   ├── UserResource.java
│   ├── OrderResource.java
│   ├── ProductResource.java
│   ├── CategoryResource.java
│   └── exceptions/
│       ├── ResourceExceptionHandler.java
│       └── StandardErro.java
├── security/
│   ├── JwtService.java              # Gera e valida tokens JWT
│   ├── JwtFilter.java               # Intercepta requisições e injeta autenticação
│   └── UserDetailsServiceImpl.java  # Carrega usuário para o Spring Security
└── services/                        # Regras de negócio
```

---

## Perfis de ambiente

O projeto possui dois perfis configurados.

### test (padrão local)

Banco H2 em memória. Dados populados automaticamente pelo `TestConfig` a cada inicialização.

Ativo por padrão em `application.properties`:
```properties
spring.profiles.active=test
```

Acesse o console H2 em: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(vazio)*

### prod

PostgreSQL. Requer as seguintes variáveis de ambiente:

| Variável | Descrição | Exemplo |
|---|---|---|
| `DATABASE_URL` | URL JDBC do banco | `jdbc:postgresql://localhost:5432/coursedb` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `sua_senha` |
| `JWT_SECRET` | Chave secreta JWT (mín. 32 chars) | `5b7e9f2a...` |
| `JWT_EXPIRATION` | Expiração do token em ms | `86400000` (24h, padrão) |
| `PORT` | Porta do servidor | `8080` (padrão) |

Para ativar localmente:
1. Crie o banco: `CREATE DATABASE coursedb;`
2. Configure as variáveis no IntelliJ em **Run → Edit Configurations → Environment variables**
3. Mude o perfil em `application.properties` para `prod`

---

## Segurança

### Autenticação

A autenticação é feita via JWT stateless. O token é gerado no login e deve ser enviado no header de todas as requisições protegidas:

```
Authorization: Bearer <token>
```

O token contém no payload: `email`, `userId` (UUID) e `role`.

### Roles

| Role | Descrição |
|---|---|
| `USER` | Usuário padrão — acesso aos próprios recursos |
| `ADMIN` | Administrador — acesso total |

### Matriz de autorização

| Método | Rota | Acesso |
|---|---|---|
| POST | `/auth/login` | Público |
| POST | `/users` | Público (cadastro) |
| GET | `/users` | Somente ADMIN |
| GET | `/users/{id}` | ADMIN ou o próprio usuário |
| PUT | `/users/{id}` | Somente o próprio usuário |
| DELETE | `/users/{id}` | Somente o próprio usuário |
| GET | `/orders` | Autenticado — USER vê só os próprios, ADMIN vê todos |
| GET | `/orders/{id}` | ADMIN ou dono do pedido |
| GET | `/products/**` | Público |
| GET | `/categories/**` | Público |

---

## Endpoints

### Autenticação

#### POST /auth/login

Autentica o usuário e retorna o token JWT.

**Request:**
```json
{
  "email": "maria@gmail.com",
  "password": "123456"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": "b0b389c7-4171-4d60-89f2-3a078071fc69",
  "email": "maria@gmail.com",
  "role": "USER"
}
```

---

### Usuários

#### GET /users
Lista todos os usuários. **Requer role ADMIN.**

#### GET /users/{id}
Busca usuário por UUID. **Requer ser o próprio usuário ou ADMIN.**

#### POST /users
Cadastra novo usuário. **Público.**

**Request:**
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "999999999",
  "password": "minhasenha"
}
```

#### PUT /users/{id}
Atualiza dados do usuário. **Somente o próprio usuário.**

#### DELETE /users/{id}
Remove o usuário. **Somente o próprio usuário.**

---

### Pedidos

#### GET /orders
Lista pedidos. **USER vê apenas os próprios; ADMIN vê todos.**

#### GET /orders/{id}
Busca pedido por ID. **ADMIN ou dono do pedido.**

---

### Produtos

#### GET /products
Lista todos os produtos. **Público.**

#### GET /products/{id}
Busca produto por ID. **Público.**

---

### Categorias

#### GET /categories
Lista todas as categorias. **Público.**

#### GET /categories/{id}
Busca categoria por ID. **Público.**

---

## Como executar localmente

### Pré-requisitos

- Java 21+
- Maven 3.x

### Perfil test (H2 — recomendado para desenvolvimento)

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/course.git
cd course
```

2. Certifique-se que `application.properties` tem:
```properties
spring.profiles.active=test
```

3. Configure a variável de ambiente `JWT_SECRET` no IntelliJ ou exporte no terminal:
```bash
export JWT_SECRET=5b7e9f2a1c4d8e3f6a0b2c5d9e1f4a7b3c6d0e2f5a8b1c4d7e0f3a6b9c2d5e8f
```

4. Execute:
```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Perfil prod (PostgreSQL)

1. Crie o banco PostgreSQL:
```sql
CREATE DATABASE coursedb;
```

2. Configure as variáveis de ambiente e mude o perfil para `prod`

3. Execute:
```bash
./mvnw spring-boot:run
```

---

## Swagger UI

Com a aplicação rodando, acesse a documentação interativa em:

```
http://localhost:8080/swagger-ui.html
```

Para testar rotas protegidas:
1. Faça `POST /auth/login` para obter o token
2. Clique em **Authorize 🔒** no canto superior direito
3. Cole o token no campo `bearerAuth` (sem o prefixo `Bearer`)
4. Clique em **Authorize** e feche o modal
5. Agora todas as requisições incluem o token automaticamente

---

## Dados de teste (perfil test)

O `TestConfig` popula automaticamente os seguintes dados ao subir no perfil `test`:

| Nome | Email | Senha | Role |
|---|---|---|---|
| Maria Brown | maria@gmail.com | 123456 | USER |
| Alex Green | alex@gmail.com | 123456 | USER |
| Bob Grey | bob@gmail.com | 123456 | USER |

---

## Variáveis de ambiente — resumo

| Variável | Obrigatória | Padrão | Descrição |
|---|---|---|---|
| `JWT_SECRET` | Sim | — | Chave para assinar tokens JWT |
| `JWT_EXPIRATION` | Não | `86400000` | Expiração do token em milissegundos |
| `DATABASE_URL` | Somente no prod | — | URL JDBC do PostgreSQL |
| `DB_USERNAME` | Somente no prod | — | Usuário do banco |
| `DB_PASSWORD` | Somente no prod | — | Senha do banco |
| `PORT` | Não | `8080` | Porta do servidor |

---

## Boas práticas implementadas

- Senhas armazenadas com **BCrypt** — nunca em texto puro
- IDs como **UUID** — resistente a enumeração sequencial
- JWT **stateless** — sem sessão no servidor
- Separação de responsabilidades: security, config, resources, services, repositories
- Variáveis sensíveis via **variáveis de ambiente** — nunca hardcoded no repositório
- Dois perfis bem definidos: `test` para desenvolvimento e `prod` para produção
- Ownership check em todas as rotas que manipulam dados de usuários e pedidos