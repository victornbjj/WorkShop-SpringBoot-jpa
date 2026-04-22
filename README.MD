# 🛒 WorkShop SpringBoot JPA

API REST completa para gerenciamento de pedidos, construída com Spring Boot 3, Spring Security e autenticação JWT. O projeto cobre desde o modelo de domínio até autorização baseada em roles, com suporte a dois perfis de banco de dados.

---

## 🚀 Tecnologias

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-green?style=flat-square&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-green?style=flat-square&logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-prod-blue?style=flat-square&logo=postgresql)
![H2](https://img.shields.io/badge/H2-test-lightgrey?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=flat-square&logo=swagger)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square&logo=apachemaven)

---

## 📐 Modelo de Domínio

```
User ──< Order ──< OrderItem >── Product >── Category
              └── Payment
```

- Um `User` pode ter vários `Order`
- Cada `Order` possui vários `OrderItem`
- Cada `OrderItem` referencia um `Product`
- Um `Product` pode pertencer a várias `Category`
- Um `Order` pode ter um `Payment`

---

## 🔐 Segurança

A API utiliza **JWT (JSON Web Token)** via `jjwt 0.12.6` com autenticação stateless.

O token carrega os claims: `email`, `userId` e `role`.

### Roles disponíveis

| Role | Permissões |
|------|-----------|
| `USER` | Ver/editar/deletar apenas os próprios dados e pedidos |
| `ADMIN` | Acesso total — listar todos os usuários e pedidos |

### Regras por endpoint

| Método | Endpoint | Regra |
|--------|----------|-------|
| POST | `/auth/login` | Público |
| POST | `/users` | Público |
| GET | `/users` | Somente `ADMIN` |
| GET | `/users/{id}` | Próprio usuário ou `ADMIN` |
| PUT | `/users/{id}` | Somente o próprio usuário |
| DELETE | `/users/{id}` | Somente o próprio usuário |
| GET | `/orders` | Autenticado — `ADMIN` vê todos, `USER` vê só os seus |
| GET | `/orders/{id}` | Dono do pedido ou `ADMIN` |
| GET | `/products/**` | Autenticado |
| GET | `/categories/**` | Autenticado |

---

## 📦 Endpoints

### 🔑 Auth

#### POST `/auth/login`
```json
{
  "email": "maria@gmail.com",
  "password": "123456"
}
```
**Resposta 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": "b84daace-2fcd-4037-b0e1-b0cacd9cbe41",
  "email": "maria@gmail.com",
  "role": "USER"
}
```

---

### 👤 Users

#### POST `/users` — Criar usuário
```json
{
  "name": "João Silva",
  "email": "joao@gmail.com",
  "phone": "999999999",
  "password": "123456"
}
```

#### GET `/users` — Listar todos `(ADMIN)`
#### GET `/users/{id}` — Buscar por ID
#### PUT `/users/{id}` — Atualizar
```json
{
  "name": "João Atualizado",
  "email": "joao@gmail.com",
  "phone": "988888888",
  "password": "novaSenha"
}
```
#### DELETE `/users/{id}` — Deletar

> Todas as rotas de `/users` (exceto POST) exigem header `Authorization: Bearer {token}`

---

### 📦 Orders

#### GET `/orders` — Listar pedidos
- `ADMIN`: retorna todos os pedidos
- `USER`: retorna apenas os pedidos do usuário autenticado

#### GET `/orders/{id}` — Buscar pedido por ID
- Retorna `403` se o pedido não pertencer ao usuário autenticado (exceto `ADMIN`)

---

### 🛍️ Products

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/products` | Listar todos |
| GET | `/products/{id}` | Buscar por ID |

---

### 🗂️ Categories

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/categories` | Listar todas |
| GET | `/categories/{id}` | Buscar por ID |

---

## ⚙️ Como executar

### Pré-requisitos

- Java 21+
- Maven 3.8+

### Perfil de teste (H2 in-memory)

```bash
# Clone o repositório
git clone https://github.com/victornbjj/WorkShop-SpringBoot-jpa.git
cd WorkShop-SpringBoot-jpa

# Configure as variáveis de ambiente
export JWT_SECRET=minha-chave-secreta-com-minimo-32-caracteres

# Execute
./mvnw spring-boot:run
```

A aplicação sobe na porta `8080` com banco H2 in-memory e dados de teste já inseridos automaticamente.

### Perfil de produção (PostgreSQL)

Configure as variáveis de ambiente:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/workshop
export DB_USERNAME=postgres
export DB_PASSWORD=sua_senha
export JWT_SECRET=minha-chave-secreta-com-minimo-32-caracteres
export JWT_EXPIRATION=86400000
export PORT=8080
```

---

## 🗄️ Dados de Teste (perfil `test`)

Ao iniciar com `spring.profiles.active=test`, os seguintes dados são inseridos automaticamente:

### Usuários
| Nome | Email | Senha | Role |
|------|-------|-------|------|
| Maria Brown | maria@gmail.com | 123456 | USER |
| Alex Green | alex@gmail.com | 123456 | USER |

> Para promover um usuário a ADMIN, acesse o H2 Console e execute:
> ```sql
> UPDATE TB_USER SET ROLE = 'ADMIN' WHERE EMAIL = 'maria@gmail.com';
> ```

### Pedidos
| ID | Status | Cliente |
|----|--------|---------|
| 1 | PAID | Maria |
| 2 | WAITING_PAYMENT | Alex |
| 3 | WAITING_PAYMENT | Maria |

### Produtos
| ID | Nome | Preço |
|----|------|-------|
| 1 | The Lord of the Rings | R$ 90,50 |
| 2 | Smart TV | R$ 2.190,00 |
| 3 | Macbook Pro | R$ 1.250,00 |
| 4 | PC Gamer | R$ 1.200,00 |
| 5 | Rails for Dummies | R$ 100,99 |

---

## 📋 H2 Console (perfil test)

Acesse `http://localhost:8080/h2-console` com:

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(vazio)* |

---

## 📖 Documentação Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

A documentação já está configurada com suporte a **JWT Bearer Auth** — clique em **Authorize** e insira o token obtido no login.

---

## 🏗️ Estrutura do Projeto

```
src/main/java/com/educandoweb/course/
├── config/
│   ├── SecurityConfig.java       # Configuração do Spring Security
│   ├── SwaggerConfig.java        # Configuração OpenAPI/Swagger
│   ├── PasswordEncoderConfig.java
│   └── TestConfig.java           # Seed de dados (perfil test)
├── dto/
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── entities/
│   ├── User.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Product.java
│   ├── Category.java
│   ├── Payment.java
│   └── enums/OrderStatus.java
├── repositories/
├── resources/                    # Controllers REST
│   ├── AuthResource.java
│   ├── UserResource.java
│   ├── OrderResource.java
│   ├── ProductResource.java
│   ├── CategoryResource.java
│   └── exceptions/
│       ├── ResourceExceptionHandler.java
│       └── StandardErro.java
├── security/
│   ├── JwtService.java           # Geração e validação de tokens
│   ├── JwtFilter.java            # Filtro de autenticação
│   └── UserDetailsServiceImpl.java
└── services/
    ├── UserService.java
    ├── OrderService.java
    ├── ProductService.java
    ├── CategoryService.java
    └── exceptions/
```

---

## 🔄 Status dos OrderStatus

| Status | Descrição |
|--------|-----------|
| `WAITING_PAYMENT` | Aguardando pagamento |
| `PAID` | Pago |
| `SHIPPED` | Enviado |
| `DELIVERED` | Entregue |
| `CANCELED` | Cancelado |

---

## 👨‍💻 Autor

Desenvolvido por **victornbjj** como parte de um workshop prático de Spring Boot com JPA, Spring Security e JWT.