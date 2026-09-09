# TECHNICAL REFERENCE - CosmosX API

Referencia tecnica consolidada: arquitetura, stack, configuracao, API, testes, build, deploy e manutencao.

> Documento de referencia para quem vai consumir, manter ou evoluir a API. Complementa o [GUIA](../docs/GUIA.md) (execucao) e o [ARQUITETURA](../docs/ARQUITETURA.md) (padroes).

---

## Sumario

1. [Visao Geral](#visao-geral)
2. [Stack](#stack)
3. [Arquitetura](#arquitetura)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Configuracao](#configuracao)
6. [API](#api)
7. [Tratamento de Erros](#tratamento-de-erros)
8. [Testes](#testes)
9. [Build e Execucao](#build-e-execucao)
10. [Deploy](#deploy)
11. [Manutencao](#manutencao)
12. [Troubleshooting](#troubleshooting)

---

## Visao Geral

O CosmosX API e uma API REST em **Java + Spring Boot** que simula as operacoes de uma agencia espacial: gerenciamento de astronautas, naves, planetas, missoes, execucao de missoes com eventos aleatorios, estatisticas, ranking e exploracao de planetas.

- **Versao atual:** v2.0.0
- **Licenca:** MIT
- **Repositorio:** https://github.com/GoomezCode/CosmosX_API

## Stack

| Tecnologia | Versao | Uso |
|------------|--------|-----|
| Java | 25 | Linguagem |
| Spring Boot | 4.1.0 | Framework (Spring Web MVC) |
| Spring Data JPA | parte do Boot | Persistencia (Hibernate) |
| H2 | parte do Boot | Banco embarcado (arquivo + in-memory em testes) |
| Bean Validation | parte do Boot | Validacao de DTOs |
| Spring Boot Actuator | parte do Boot | Health check (`/actuator/health`) |
| springdoc-openapi | 3.1.1 | Swagger UI / OpenAPI |
| JaCoCo | 0.8.13 | Cobertura de codigo (gate >= 80% linhas) |
| Maven Wrapper | 3.9.x | Build reproduzivel |

## Arquitetura

Arquitetura em camadas (Layered Architecture):

```
Controller  ->  Service  ->  Repository  ->  H2 (JPA/Hibernate)
```

- **Controller:** recebe requisicoes HTTP, delega ao service, retorna DTOs. Sem logica de negocio.
- **Service:** regras de negocio; operacoes de escrita com `@Transactional`; lanca excecoes customizadas.
- **Repository:** interfaces `JpaRepository<Entidade, Long>` — acesso a dados pronto do Spring Data.
- **Model:** entidades JPA (`@Entity`), colecoes com `@ElementCollection` (fetch EAGER), `ResourceFound` como `@Embeddable`.
- **DTO:** records imutaveis (Request/Response) com validacao.
- **Config:** CORS, OpenAPI (Swagger), DataSeeder.
- **Handler:** `GlobalExceptionHandler` (`@RestControllerAdvice`) normaliza respostas de erro.

Detalhes e decisoes de design: [ARQUITETURA.md](../docs/ARQUITETURA.md).

## Estrutura do Projeto

```
CosmosX_API/
├── CHANGELOG.md
├── LICENSE
├── pom.xml
├── mvnw / mvnw.cmd
├── data/                        # Banco H2 runtime (gitignored)
├── docs/
│   ├── GUIA.md                  # Guia de configuracao e execucao
│   ├── ENDPOINTS.md             # Documentacao detalhada dos endpoints
│   ├── ROADMAP.md               # Historico de implementacao (Fases 1-6)
│   ├── ARQUITETURA.md           # Padroes e decisoes de arquitetura
│   └── TECHNICAL_REFERENCE.md   # Este documento
└── src/
    ├── main/java/com/goomez/CosmosX/
    │   ├── CosmosXApplication.java
    │   ├── config/              # CORS, OpenAPI, DataSeeder
    │   ├── controller/          # 8 controllers REST
    │   ├── service/             # 10 services
    │   ├── repository/          # 4 repositorios JPA
    │   ├── model/               # 8 entidades/enums/embeddables
    │   ├── dto/                 # 21 DTO records
    │   ├── exception/           # 3 excecoes customizadas
    │   └── handler/             # GlobalExceptionHandler
    ├── main/resources/application.properties
    └── test/
        ├── java/com/goomez/CosmosX/
        │   ├── e2e/             # Teste de integracao E2E
        │   ├── controller/      # Testes de controllers (MockMvc)
        │   └── service/         # Testes de services (Mockito)
        └── resources/application.properties   # H2 in-memory p/ testes
```

## Configuracao

### `src/main/resources/application.properties`

```properties
spring.application.name=CosmosX

# H2 em arquivo (gitignored em ./data)
spring.datasource.url=jdbc:h2:file:./data/cosmosx
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

app.cors.allowed-origins=*

management.endpoints.web.exposure.include=health
```

| Propriedade | Descricao |
|-------------|-----------|
| `spring.datasource.url` | Endereco do banco. `file:./data/cosmosx` (dev) ou `mem:cosmosx_test` (testes) |
| `spring.jpa.hibernate.ddl-auto` | `update` em dev (schema evolutivo), `create-drop` em testes |
| `spring.h2.console.*` | H2 console web em `/h2-console` (dev) |
| `app.cors.allowed-origins` | Origens permitidas (CORS). Separar por virgula. Padrao `*` |
| `management.endpoints.web.exposure.include` | Endpoints do Actuator expostos via web (apenas `health`) |

### Referencias externas (ambiente)

| Variavel | Descricao |
|----------|-----------|
| `JAVA_HOME` | Apontar para um JDK 25 (ex.: `~/.jdks/jdk-25.0.4.1+1`) |

## API

Endpoints publicos (leitura + escrita, sem autenticacao):

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/astronauts`, `/astronauts/{id}` | Listar/consultar astronautas |
| POST | `/astronauts` | Criar astronauta |
| PUT | `/astronauts/{id}` | Atualizar astronauta |
| DELETE | `/astronauts/{id}` | Remover astronauta |
| GET | `/spacecraft`, `/spacecraft/{id}` | Listar/consultar naves |
| POST | `/spacecraft` | Criar nave |
| PUT | `/spacecraft/{id}` | Atualizar nave |
| DELETE | `/spacecraft/{id}` | Remover nave |
| GET | `/planet`, `/planet/{id}` | Listar/consultar planetas |
| POST | `/planet` | Criar planeta |
| PUT | `/planet/{id}` | Atualizar planeta |
| DELETE | `/planet/{id}` | Remover planeta |
| GET | `/mission`, `/mission/{id}` | Listar/consultar missoes |
| POST | `/mission` | Criar missao |
| PUT | `/mission/{id}` | Atualizar missao |
| DELETE | `/mission/{id}` | Remover missao |
| POST | `/mission/{id}/start` | Executar missao (simulacao) |
| GET | `/mission/history` | Historico (filtros: `status`, `planetId`) |
| GET | `/stats` | Estatisticas agregadas |
| GET | `/ranking` | Ranking de astronautas |
| POST | `/exploration/discover` | Descobrir planeta (recursos aleatorios) |
| GET | `/actuator/health` | Health check (Actuator) |

Ferramentas:

| Recurso | URL |
|---------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| H2 Console | `http://localhost:8080/h2-console` |
| Health | `http://localhost:8080/actuator/health` |

Documentacao detalhada: [ENDPOINTS.md](../docs/ENDPOINTS.md).

## Tratamento de Erros

Resposta padronizada via `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-09-09T12:00:00",
  "status": 404,
  "message": "Descricao do erro"
}
```

| Situacao | Status |
|----------|--------|
| Recurso nao encontrado (`ResourceNotFoundException`) | 404 |
| Validacao de DTO (`MethodArgumentNotValidException`) | 400 |
| Combustivel insuficiente (`InsufficientFuelException`) | 400 |
| Estado invalido de missao (`InvalidMissionStateException`) | 400 |
| Erro generico | 500 |

## Testes

Suite: testes unitarios (services com Mockito, controllers com MockMvc) + integracao E2E (`@SpringBootTest` com H2 in-memory).

```bash
./mvnw verify   # testes + gate JaCoCo (>= 80% linhas)
```

Relatorio de cobertura: `target/site/jacoco/index.html`.

| Metrica | Valor |
|---------|-------|
| Total de testes | 98 |
| Falhas | 0 |
| Cobertura de linhas | >= 97% (gate >= 80%) |

## Build e Execucao

```bash
# Requisito: JDK 25
export JAVA_HOME=~/.jdks/jdk-25.0.4.1+1

# Compilar + testar + gerar o jar
./mvnw clean verify

# Rodar a aplicacao
./mvnw spring-boot:run
# ou
java -jar target/CosmosX-2.0.0.jar
```

A aplicacao sobe em `http://localhost:8080`. O banco H2 (`./data/`) e criado automaticamente e populado pelo `DataSeeder` quando vazio.

## Deploy

A API e stateless em relacao ao deploy (dados ficam no H2 local). Opcoes:

- **Servidor compartilhado:** `java -jar CosmosX-2.0.0.jar` (porta padrao 8080). Configurar firewall para expor apenas `80/443`.
- **Docker:** construir imagem a partir do jar (ex.: `eclipse-temurin:25-jre`) e mapear a porta 8080.
- **Prod:** considerar `management.endpoints.web.exposure.include=health,info` e definir `app.cors.allowed-origins` com as origens reais do site.

> Nota: para uso em producao com varios usuarios, migrar para um banco externo (PostgreSQL/MySQL) via `spring.datasource.*`.

## Manutencao

- **Dados:** resetar o banco apagando a pasta `./data/` (o `DataSeeder` repopula ao subir).
- **Cobertura:** o gate do JaCoCo (>= 80%) falha o build se o minimo nao for atingido.
- **Novas entidades:** seguir o padrao Model (`@Entity`) + Repository (`JpaRepository`) + Service + Controller + DTO + testes.
- **Commit:** convencao `tipo(escopo): descricao` (ver [GUIA.md](../docs/GUIA.md#contribuindo)).
- **Versao:** gerar tag `vX.Y.Z` em `main` a cada release e registrar no [CHANGELOG.md](../CHANGELOG.md).

## Troubleshooting

| Problema | Causa provavel | Solucao |
|----------|----------------|---------|
| `java: package org.springframework... does not exist` | JDK incorreto | Usar JDK 25 via `JAVA_HOME` |
| `Fuel too low for this mission` (400) | Combustivel < distancia do planeta | Aumentar combustivel da nave |
| `Mission must be PENDING to start` (400) | Missao ja executada | Criar nova missao |
| Porta 8080 em uso | Outra aplicacao na porta | `server.port=8081` |
| McGraw: `UnsupportedOperationException` ao persistir listas | Lista imutavel na entidade | Usar `new ArrayList<>(...)` nas colecoes (ver `Mission`) |