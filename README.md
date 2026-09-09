<h1 align="center">CosmosX API</h1>

API REST em **Java + Spring Boot** para gerenciamento de uma agencia espacial ficticia: astronautas, naves espaciais, planetas e missoes.

> Projeto em desenvolvimento. CRUD basico, simulacao de missoes, estatisticas e persistencia JPA/H2 ja implementados. Veja o [Roadmap](docs/ROADMAP.md).

---

## Sobre o projeto

O CosmosX API e uma API para controlar operacoes de uma frota espacial. Hoje ela permite cadastrar e consultar:

- Astronautas
- Naves espaciais (spacecrafts)
- Planetas
- Missoes

Os dados sao persistidos em um banco **H2** (arquivo `./data/`, gitignored) usando **JPA/Hibernate**.

## Tecnologias

- Java 25
- Spring Boot 4.1.0 (Spring Web MVC)
- Spring Data JPA (Hibernate)
- H2 (banco embarcado + console web)
- Bean Validation (`spring-boot-starter-validation`)
- Swagger/OpenAPI (springdoc-openapi)
- JaCoCo (cobertura >= 80% de linhas)
- Maven (com Maven Wrapper)

## Estrutura do projeto

```
src/main/java/com/goomez/CosmosX/
├── config/          # CORS, OpenAPI, DataSeeder
├── controller/      # Endpoints REST
├── service/         # Regras de negocio
├── repository/      # Repositorios JPA
├── model/           # Entidades JPA
├── dto/             # Request/Response records
├── exception/       # Excecoes customizadas
└── handler/         # Global Exception Handler
```

## Como executar

Pre-requisitos: **JDK 25** e **Maven** (ou use o wrapper incluso no projeto).

```bash
# clonar o repositorio
git clone https://github.com/GoomezCode/CosmosX_API.git
cd CosmosX_API

# rodar a aplicacao
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows
```

A aplicacao sobe por padrao em `http://localhost:8080`.

| Recurso | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

## Testes

```bash
# testes + gate de cobertura (JaCoCo >= 80% linhas)
./mvnw verify
```

## Endpoints disponiveis

| Recurso | GET all | GET by ID | POST | PUT | DELETE |
|---------|---------|-----------|------|-----|--------|
| `/astronauts` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/spacecraft` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/planet` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/mission` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/mission/{id}/start` | - | - | ✅ | - | - |
| `/mission/history` | ✅ | - | - | - | - |
| `/stats` | ✅ | - | - | - | - |
| `/ranking` | ✅ | - | - | - | - |
| `/exploration/discover` | - | - | ✅ | - | - |

Documentacao detalhada: [docs/ENDPOINTS.md](docs/ENDPOINTS.md)

## Documentacao

| Arquivo | Descricao |
|---------|-----------|
| [GUIA.md](docs/GUIA.md) | Guia completo do projeto |
| [ENDPOINTS.md](docs/ENDPOINTS.md) | Documentacao dos endpoints |
| [ROADMAP.md](docs/ROADMAP.md) | Roadmap de implementacao |
| [ARQUITETURA.md](docs/ARQUITETURA.md) | Padroes e arquitetura |

## Status atual

- [x] CRUD de astronautas
- [x] CRUD de naves
- [x] CRUD de planetas
- [x] Cadastro simples de missoes
- [x] CRUD completo (GET by ID, DELETE, PUT para todas entidades)
- [x] ID auto-gerado nas entidades
- [x] DTO Pattern (Request/Response)
- [x] Bean Validation
- [x] Global Exception Handler
- [x] Testes unitarios (95 testes)
- [x] Execucao de missoes (`/mission/{id}/start`)
- [x] Sistema de combustivel
- [x] Sistema de perigo / eventos aleatorios
- [x] Geracao de recursos ao fim da missao
- [x] Endpoint de estatisticas (`/stats`)
- [x] Ranking de astronautas (`/ranking`)
- [x] Historico de missoes (`/mission/history`)
- [x] Descoberta automatica de planetas (`/exploration/discover`)
- [x] Persistencia JPA + H2
- [x] Swagger/OpenAPI
- [x] CORS configurado
- [x] JaCoCo com gate de cobertura >= 80%

## Contribuindo

Este e um projeto pessoal em desenvolvimento. Sugestoes, issues e pull requests sao bem-vindos!

## Licenca

Nenhuma licenca definida ate o momento.