# ROADMAP - CosmosX API

Roadmap de implementacao do projeto CosmosX API.

---

## Sumario

1. [Visao Geral](#visao-geral)
2. [Fase 1 - CRUD Completo](#fase-1---crud-completo)
3. [Fase 2 - Testes Unitarios](#fase-2---testes-unitarios)
4. [Fase 3 - Simulacao de Missoes](#fase-3---simulacao-de-missoes)
5. [Fase 4 - Estatisticas e Ranking](#fase-4---estatisticas-e-ranking)
6. [Fase 5 - Preparacao p/ Integracao com Site](#fase-5---preparacao-p-integracao-com-site-portfolio)
7. [Fase 6 - Validacao Final e Entrega](#fase-6---validacao-final-e-entrega)

---

## Visao Geral

| Fase | Descricao | Prioridade | Status |
|------|-----------|------------|--------|
| 1 | CRUD Completo | Alta | ✅ Concluida |
| 2 | Testes Unitarios | Alta | ✅ Concluida |
| 3 | Simulacao de Missoes | Alta | ✅ Concluida |
| 4 | Estatisticas e Ranking | Media | ✅ Concluida |
| 5 | Preparacao p/ Integracao com Site | Alta | ✅ Concluida |
| 6 | Validacao Final e Entrega | Alta | ⏳ Pendente |

---

## Fase 1 - CRUD Completo

**Objetivo:** Completar o CRUD para todas as entidades. ✅ Concluida

### Entidade: Spacecraft

| Endpoint | Metodo | Status |
|----------|--------|--------|
| `/spacecraft` | GET | ✅ Implementado |
| `/spacecraft/{id}` | GET | ✅ Implementado |
| `/spacecraft` | POST | ✅ Implementado |
| `/spacecraft/{id}` | PUT | ✅ Implementado |
| `/spacecraft/{id}` | DELETE | ✅ Implementado |

### Entidade: Planet

| Endpoint | Metodo | Status |
|----------|--------|--------|
| `/planet` | GET | ✅ Implementado |
| `/planet/{id}` | GET | ✅ Implementado |
| `/planet` | POST | ✅ Implementado |
| `/planet/{id}` | PUT | ✅ Implementado |
| `/planet/{id}` | DELETE | ✅ Implementado |

### Entidade: Mission

| Endpoint | Metodo | Status |
|----------|--------|--------|
| `/mission` | GET | ✅ Implementado |
| `/mission/{id}` | GET | ✅ Implementado |
| `/mission` | POST | ✅ Implementado |
| `/mission/{id}` | PUT | ✅ Implementado |
| `/mission/{id}` | DELETE | ✅ Implementado |

### Tarefas

- [x] Criar DTOs de Update (SpacecraftUpdateRequest, PlanetUpdateRequest, MissionUpdateRequest)
- [x] Adicionar metodos `listById`, `update`, `delete` nos Services
- [x] Adicionar endpoints nos Controllers
- [x] Atualizar documentacao ENDPOINTS.md
- [x] ID auto-gerado em todos os Services

---

## Fase 2 - Testes Unitarios

**Objetivo:** Garantir cobertura minima de 80% para Services e Controllers. ✅ Concluida

### Servicos para testar

| Servico | Metodos | Status |
|---------|---------|--------|
| AstronautService | listAll, listById, add, update, delete | ✅ Coberto |
| SpacecraftService | listAll, listById, add, update, delete | ✅ Coberto |
| PlanetService | listAll, listById, add, update, delete | ✅ Coberto |
| MissionService | listAll, listById, add, update, delete | ✅ Coberto |

### Controllers para testar

| Controller | Metodos | Status |
|------------|---------|--------|
| AstronautController | GET, GET by ID, POST, PUT, DELETE | ✅ Coberto |
| SpacecraftController | GET, GET by ID, POST, PUT, DELETE | ✅ Coberto |
| PlanetController | GET, GET by ID, POST, PUT, DELETE | ✅ Coberto |
| MissionController | GET, GET by ID, POST, PUT, DELETE | ✅ Coberto |

### Tarefas

- [x] Configurar dependencias de teste (JUnit 5, MockMvc, Mockito)
- [x] Criar testes para AstronautService (8 testes)
- [x] Criar testes para SpacecraftService (8 testes)
- [x] Criar testes para PlanetService (8 testes)
- [x] Criar testes para MissionService (8 testes)
- [x] Criar testes para AstronautController (7 testes)
- [x] Criar testes para SpacecraftController (6 testes)
- [x] Criar testes para PlanetController (6 testes)
- [x] Criar testes para MissionController (6 testes)
- [x] Refatorar Services para injecao do data path (`@Value` + `app.data.path`)

### Resultado

| Metrica | Valor |
|---------|-------|
| Total de testes | 57 |
| Falhas | 0 |
| Sucesso | 100% |

---

## Fase 3 - Simulacao de Missoes

**Objetivo:** Transformar o cadastro de missoes em uma simulacao real de exploracao espacial. ✅ Concluida

> **Decisao de design:** a entidade `Mission` passou a ter o campo `spacecraftId` (nave responsavel pela missao). O endpoint usa `/mission/{id}/start` para manter consistencia com o restante da API.

### Novo Endpoint

```
POST /mission/{id}/start
```

### Funcionalidades

#### 3.1 Execucao de Missao

Ao iniciar uma missao, o sistema deve:

1. Verificar se a nave tem combustivel suficiente
2. Calcular o consumo de combustivel
3. Verificar eventos de perigo
4. Gerar recursos (se sucesso)
5. Atualizar status da missao

#### 3.2 Sistema de Combustivel

```java
// Regra: combustivel da nave deve ser >= distancia do planeta
if (spacecraft.getFuel() < planet.getDistance()) {
    throw new InsufficientFuelException("Fuel too low for this mission");
}

// Consumo: combustivel = distancia * fator de perigo
int fuelConsumed = planet.getDistance() * planet.getDangerLevel();
```

#### 3.3 Sistema de Perigo

Cada planeta tem um `dangerLevel` (0-10). Ao iniciar a missao:

| Evento | Chance | Efeito |
|--------|--------|--------|
| Sucesso | 60% - (dangerLevel * 5%) | Missao completa |
| Falha Mecanica | 15% | Missao falha, nave danificada |
| Ataque Alienigena | 15% | Missao falha, astronautas perdem XP |
| Tempestade Cosmica | 10% + remanescente | Missao falha, perda de recursos |

> **Regra implementada:** `roll` de 1 a 100. Sucesso se `roll <= 60 - dangerLevel*5` (minimo 0). Janelas fixas de 15/15/10 para falha mecanica, ataque alienigena e tempestade cosmica. O restante do roll vai para a tempestade cosmica, tornando planetas perigosos mais arriscados.

#### 3.4 Geracao de Recursos

Se a missao for bem-sucedida:

```json
{
  "missionId": 1,
  "status": "SUCCESS",
  "fuelConsumed": 300,
  "resourcesFound": [
    { "resource": "Gold", "quantity": 25 },
    { "resource": "Water", "quantity": 50 }
  ]
}
```

### Response do Endpoint

```json
{
  "missionId": 1,
  "status": "SUCCESS",
  "fuelConsumed": 300,
  "resourcesFound": [
    { "resource": "Gold", "quantity": 25 }
  ],
  "events": [
    "Mission completed successfully",
    "Resources collected: Gold (25)"
  ]
}
```

### Tarefas

- [x] Adicionar `spacecraftId` na entidade `Mission` (+ DTOs e JSON)
- [x] Criar enum `MissionStatus` (PENDING, IN_PROGRESS, SUCCESS, FAILED)
- [x] Criar enum `MissionEvent` (SUCCESS, MECHANICAL_FAILURE, ALIEN_ATTACK, COSMIC_STORM)
- [x] Criar `MissionService.executeMission(Long id)`
- [x] Criar `FuelService` para validacao de combustivel
- [x] Criar `DangerService` para calculo de eventos aleatorios
- [x] Criar `ResourceService` para geracao de recursos
- [x] Criar endpoint `POST /mission/{id}/start`
- [x] Atualizar documentacao

### Resultado

| Metrica | Valor |
|---------|-------|
| Total de testes | 79 |
| Falhas | 0 |
| Sucesso | 100% |

---

## Fase 4 - Estatisticas e Ranking

**Objetivo:** Fornecer dados agregados e rankings. ✅ Concluida

> **Decisao de design:** missao passou a persistir `fuelConsumed`, `resourcesFound` e `completedAt`; planeta ganhou `discoveredAt`. O historico usa `/mission/history` (singular) para manter consistencia com a API.

### Novos Endpoints

#### 4.1 Estatisticas Gerais

```
GET /stats
```

**Response:**
```json
{
  "totalMissions": 48,
  "successes": 39,
  "failures": 9,
  "successRate": 81.25,
  "resourcesCollected": 1250,
  "topResource": "Gold"
}
```

#### 4.2 Ranking de Astronautas

```
GET /ranking
```

**Response:**
```json
[
  { "name": "Daniel", "rank": "Commander", "experience": 5200, "missionsCompleted": 15 },
  { "name": "Laura", "rank": "Pilot", "experience": 3800, "missionsCompleted": 12 }
]
```

#### 4.3 Historico de Missoes

```
GET /mission/history
```

**Query Parameters:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| status | String | Nao | Filtrar por status (SUCCESS, FAILED) |
| planetId | Long | Nao | Filtrar por planeta |

**Response:**
```json
[
  {
    "id": 1,
    "planetName": "Zorion",
    "astronauts": ["Daniel", "Laura"],
    "status": "SUCCESS",
    "fuelConsumed": 300,
    "resourcesFound": ["Gold", "Water"],
    "completedAt": "2026-09-09T14:30:00"
  }
]
```

#### 4.4 Descoberta Automatica de Planetas

```
POST /exploration/discover
```

**Request Body:**
```json
{
  "name": "Nebulon-7",
  "distance": 1200,
  "dangerLevel": 6
}
```

**Response:**
```json
{
  "id": 5,
  "name": "Nebulon-7",
  "distance": 1200,
  "dangerLevel": 6,
  "resources": ["Crystal", "Titanium"],
  "discoveredAt": "2026-09-09T15:00:00"
}
```

> Os recursos sao gerados aleatoriamente baseados no `dangerLevel`.

### Tarefas

- [x] Persistir resultados de execucao na `Mission` (`fuelConsumed`, `resourcesFound`, `completedAt`)
- [x] Criar `StatsService` com metodos de agregacao
- [x] Criar endpoint `GET /stats`
- [x] Criar `RankingService` e endpoint `GET /ranking`
- [x] Criar endpoint `GET /mission/history`
- [x] Criar `ExplorationService` para descoberta de planetas
- [x] Criar endpoint `POST /exploration/discover`
- [x] Atualizar documentacao

### Resultado

| Metrica | Valor |
|---------|-------|
| Total de testes | 95 |
| Falhas | 0 |
| Sucesso | 100% |

---

## Fase 5 - Preparacao p/ Integracao com Site (Portfolio)

**Objetivo:** Preparar a API para o site futuro: persistencia real (JPA + H2), CORS, Swagger/OpenAPI, cobertura medida (JaCoCo) e entrega via `main`. ✅ Concluida

> **Decisoes de design aprovadas:** migracao JSON -> JPA + H2 (arquivo em `./data/`, gitignored); `ResourceFound` virou `@Embeddable`; API publica leitura + escrita (sem autenticacao por enquanto); tag `v1.0.0` criada no merge para `main`.

### Tarefas

- [x] Adicionar `spring-boot-starter-data-jpa` e H2 ao `pom.xml`
- [x] Anotar entidades (`@Entity`, `@Id`, `@GeneratedValue`, `@ElementCollection`)
- [x] Criar pacote `repository/` (AstronautRepository, SpacecraftRepository, PlanetRepository, MissionRepository)
- [x] Refatorar services para usar repositorios (remover `ObjectMapper`/leitura de arquivo)
- [x] Criar data seeding via `CommandLineRunner`
- [x] Configurar datasource H2 em `application.properties` (remover `app.data.path`)
- [x] Remover `src/main/resources/data/*.json`
- [x] Configurar CORS (`app.cors.allowed-origins`)
- [x] Adicionar Swagger/OpenAPI (springdoc-openapi 3.1.1)
- [x] Adicionar JaCoCo com check de 80% de linha
- [x] Reescrever testes de service (mock de repositories)
- [x] Atualizar documentacao
- [x] Merge `develop` -> `main` + tag `v1.0.0`

### Resultado

| Metrica | Valor |
|---------|-------|
| Total de testes | 95 |
| Falhas | 0 |
| Sucesso | 100% |
| Cobertura de linhas (JaCoCo) | 97% (gate >= 80%) |

---

## Fase 6 - Validacao Final e Entrega

**Objetivo:** Fechar o projeto com validacao completa, documentacao de referencia tecnica, termos de licenca e marcacao de CONCLUIDO. ⏳ Pendente

> **Decisoes de design aprovadas:** licenca **MIT**; liberacao final com merge `develop` -> `main` + tag `v2.0.0` (a `v1.0.0` sera criada na Fase 5); teste de integracao E2E com `@SpringBootTest`; Actuator para health check; API continua publica (leitura + escrita). **Depende da Fase 5.**

### Tarefas

#### 1. Testes
- [ ] Criar teste de integracao E2E (`@SpringBootTest` + MockMvc): astronauta -> nave -> planeta -> missao -> execucao -> stats/ranking
- [ ] Rodar suite completa e confirmar 0 falhas
- [ ] Confirmar gate de cobertura (JaCoCo >= 80% de linha, herdado da Fase 5)

#### 2. Documentacoes
- [ ] Criar Technical Reference Guide (`docs/TECHNICAL_REFERENCE.md`) consolidando arquitetura, API, configuracao, manutencao e deploy
- [ ] Auditoria final de todas as docs (GUIA, ENDPOINTS, ROADMAP, ARQUITETURA, README) — consistencia com o codigo
- [ ] Criar `CHANGELOG.md` com historico de versoes Fase 1-6

#### 3. Termos
- [ ] Adicionar licenca MIT (`LICENSE`)
- [ ] Referenciar licenca no README e no `pom.xml` (`<licenses>`)

#### 4. Ajustes finais
- [ ] Adicionar Spring Boot Actuator (`/actuator/health`)
- [ ] Adicionar badges no README (versao, testes, Java/Spring)
- [ ] Marcar README como projeto CONCLUIDO e remover "em desenvolvimento"
- [ ] Marcar todas as fases (1-6) como concluidas no ROADMAP

#### 5. Entrega
- [ ] Commit + push `develop`
- [ ] Merge `develop` -> `main` + push
- [ ] Tag `v2.0.0` + push da tag

---

## Prioridade de Implementacao

```
Fase 1 (CRUD) → Fase 2 (Testes) → Fase 3 (Simulacao) → Fase 4 (Estatisticas) → Fase 5 (Preparacao p/ Site) → Fase 6 (Validacao Final)
```

Cada fase depende da anterior. Nao e possivel pular fases.
