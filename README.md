<h1 align="center">CosmosX API</h1>

<p align="center">
  <img alt="Versao" src="https://img.shields.io/badge/versao-v2.0.1-blue">
  <img alt="Status" src="https://img.shields.io/badge/status-concluido-success">
  <img alt="Testes" src="https://img.shields.io/badge/testes-98%20passaram-brightgreen">
  <img alt="Cobertura" src="https://img.shields.io/badge/cobertura-%3E%3D97%25-brightgreen">
  <img alt="Java" src="https://img.shields.io/badge/java-17%2B-orange">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen">
  <img alt="Licenca" src="https://img.shields.io/badge/licenca-MIT-blue">
</p>

API REST em **Java + Spring Boot** para gerenciamento de uma agencia espacial ficticia: astronautas, naves espaciais, planetas, missoes, simulacao de missoes, estatisticas e exploracao.

> ✅ **Projeto CONCLUIDO.** Todas as fases do [Roadmap](docs/ROADMAP.md) foram implementadas e entregues na tag `v2.0.1`.

---

## Sobre o projeto

O CosmosX API e uma API para controlar operacoes de uma frota espacial. Ela permite cadastrar e consultar:

- Astronautas
- Naves espaciais (spacecrafts)
- Planetas
- Missoes

Alem disso, inclui simulacao de execucao de missoes (com eventos aleatorios de perigo, consumo de combustivel e coleta de recursos), estatisticas gerais, ranking de astronautas, historico e descoberta automatica de planetas.

Os dados sao persistidos em um banco **H2** (arquivo `./data/`, gitignored) usando **JPA/Hibernate**.

## Tecnologias

- Java 17+ (compilado/testado até o Java 25)
- Spring Boot 4.1.0 (Spring Web MVC)
- Spring Data JPA (Hibernate)
- H2 (banco embarcado + console web)
- Bean Validation (`spring-boot-starter-validation`)
- Spring Boot Actuator (health check `/actuator/health`)
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

Pre-requisitos: **JDK 17+** (ex.: 17, 21 ou 25 — precisa ter `javac`), e **Maven** (ou use o wrapper incluso no projeto).

```bash
# clonar o repositorio
git clone https://github.com/GoomezCode/CosmosX_API.git
cd CosmosX_API

# apontar para um JDK completo (com javac), se necessario
export JAVA_HOME=~/.jdks/jdk-25.0.4.1+1

# rodar a aplicacao
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows
```

> **Dica:** se der erro `release version 17 not supported`, o JDK em uso nao tem compilador/versao adequada — veja a [Solucao de Problemas](docs/GUIA.md#solucao-de-problemas).

A aplicacao sobe por padrao em `http://localhost:8080`.

| Recurso | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |
| Health Check | http://localhost:8080/actuator/health |

## Testes

```bash
# testes + gate de cobertura (JaCoCo >= 80% linhas)
./mvnw verify
```

Suite: **98 testes** (unitarios de services/controllers + integracao E2E com H2 in-memory), 0 falhas, cobertura >= 97%.

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
| `/actuator/health` | ✅ | - | - | - | - |

Documentacao detalhada: [docs/ENDPOINTS.md](docs/ENDPOINTS.md)

## Documentacao

| Arquivo | Descricao |
|---------|-----------|
| [GUIA.md](docs/GUIA.md) | Guia de configuracao e execucao |
| [ENDPOINTS.md](docs/ENDPOINTS.md) | Documentacao dos endpoints |
| [ROADMAP.md](docs/ROADMAP.md) | Roadmap de implementacao (Fases 1-6) |
| [ARQUITETURA.md](docs/ARQUITETURA.md) | Padroes e arquitetura |
| [TECHNICAL_REFERENCE.md](docs/TECHNICAL_REFERENCE.md) | Referencia tecnica consolidada |
| [CHANGELOG.md](CHANGELOG.md) | Historico de versoes |

## Status

- [x] CRUD de astronautas, naves, planetas e missoes (com GET by ID, PUT e DELETE)
- [x] ID auto-gerado nas entidades
- [x] DTO Pattern (Request/Response)
- [x] Bean Validation
- [x] Global Exception Handler
- [x] Simulacao de missoes (`/mission/{id}/start`) com combustivel, perigo e recursos
- [x] Estatisticas (`/stats`), ranking (`/ranking`) e historico (`/mission/history`)
- [x] Descoberta automatica de planetas (`/exploration/discover`)
- [x] Persistencia JPA + H2
- [x] Swagger/OpenAPI
- [x] CORS configurado
- [x] Spring Boot Actuator (`/actuator/health`)
- [x] Testes unitarios + integracao E2E (98 testes, 0 falhas)
- [x] JaCoCo com gate de cobertura >= 80%
- [x] Licenca MIT

## Contribuindo

Sugestoes, issues e pull requests sao bem-vindos! Veja o fluxo de contribuicao no [GUIA](docs/GUIA.md).

## Licenca

Distribuido sob a licenca **MIT**. Veja o arquivo [LICENSE](LICENSE).