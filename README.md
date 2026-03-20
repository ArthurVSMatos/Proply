# 🏠 Proply - Real Estate SaaS API

Proply é uma API RESTful desenvolvida com Spring Boot para gestão de imóveis em um modelo SaaS (Software as a Service), com arquitetura multi-tenant segura e escalável.

---

## 🚀 Funcionalidades

- 🔐 Autenticação Stateless com JWT
- 🏢 Multi-tenant (isolamento rigoroso de dados por empresa via Token)
- 🏠 CRUD completo de imóveis (Properties)
- 📄 Paginação de resultados com Spring Data
- 🛡️ DTO Pattern para evitar vazamento de dados sensíveis da base de dados

---

## 🧠 Arquitetura

O projeto segue uma arquitetura modular baseada em features (Feature-based architecture), facilitando a escalabilidade:

com.proply
├── config/        → Configurações (Security, CORS, etc.)
├── shared/        → Regras globais e Contexto (TenantContext)
├── features/      → Domínio principal da aplicação
│   ├── auth/
│   ├── user/
│   └── property/

### 🔥 Padrões e Práticas Utilizadas
- **Feature-based architecture**
- **DTO Pattern** (Camada de entrada e saída isoladas da Entidade)
- **Stateless Authentication** (JWT)
- **Multi-tenant** (Shared Database com isolamento via código/Queries)

---

## 🔐 Segurança

- Autenticação e Autorização via **JWT (JSON Web Token)**.
- **Isolamento Multi-tenant Automático:** A API identifica a qual empresa o usuário pertence diretamente pelo Payload do JWT, eliminando a necessidade de headers manuais inseguros.
- Proteção nativa contra acesso indevido (IDOR - Insecure Direct Object Reference) nas operações de leitura, atualização e exclusão.

---

## 📡 Endpoints Principais

### 🔑 Auth
| Método | Endpoint         | Descrição                  |
|--------|------------------|----------------------------|
| POST   | `/auth/login`    | Login do usuário (Gera JWT)|
| POST   | `/auth/register` | Registro de nova empresa/user|

---

### 🏠 Properties (Requer JWT)
| Método | Endpoint            | Descrição                                  |
|--------|--------------------|--------------------------------------------|
| POST   | `/properties`      | Criar um novo imóvel para a empresa logada |
| GET    | `/properties`      | Listar imóveis da empresa (com paginação)  |
| GET    | `/properties/{id}` | Buscar os detalhes de um imóvel específico |
| PUT    | `/properties/{id}` | Atualizar dados de um imóvel               |
| DELETE | `/properties/{id}` | Remover um imóvel do catálogo              |

---

## 📄 Paginação

A listagem de imóveis é otimizada e preparada para lidar com milhares de registos. Exemplo de uso:

`GET /properties?page=0&size=10`

A resposta devolve um objeto paginado do Spring Data, que inclui metadados essenciais para a interface (Front-end):
* **`content`**: O array (lista) de imóveis solicitados.
* **`totalElements`**: O número total de imóveis encontrados.
* **`totalPages`**: O número total de páginas disponíveis.
* **`last` / `first`**: Booleanos que indicam se estás na última ou primeira página.


## 🛠️ Tecnologias
Java 17+

Spring Boot 3+

Spring Security

Spring Data JPA / Hibernate

PostgreSQL (ou outro banco relacional)

JWT (JSON Web Tokens)

## ▶️ Como rodar o projeto
Bash
# Clone o repositório
git clone [https://github.com/ArthurVSMatos/Proply.git](https://github.com/ArthurVSMatos/Proply.git)

# Entre no diretório do projeto
cd Proply

# Rode a aplicação
./mvnw spring-boot:run
🧪 Testes da API
Use ferramentas como Postman ou Insomnia.
⚠️ Importante: Para acessar as rotas de /properties, você deve enviar o token JWT gerado no login através do header Authorization:
Bearer <seu-token-jwt>

🚀 Próximas Melhorias (Roadmap)
[ ] Tratamento global de exceções (Global Exception Handler) para padronizar erros da API.

[ ] Regras de Negócio: Bloquear a edição e exclusão de imóveis com status SOLD.

[ ] Upload de imagens de imóveis (Integração com AWS S3 / Cloudinary).

[ ] Filtros avançados na paginação (por cidade, faixa de preço, tipo).

[ ] Gestão de clientes (CRM básico) e Agendamento de visitas.

👨‍💻 Desenvolvido por Arthur Matos 🚀 📌 Status do Projeto: ✅ Em desenvolvimento ativo