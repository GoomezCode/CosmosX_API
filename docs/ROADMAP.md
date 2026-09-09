# ROADMAP - CosmosX API

Roadmap de implementacao do projeto CosmosX API.

---

## Sumario

1. [Visao Geral](#visao-geral)
2. [Fase 1 - CRUD Completo](#fase-1---crud-completo)
3. [Fase 2 - Testes Unitarios](#fase-2---testes-unitarios)
4. [Fase 3 - Simulacao de Missoes](#fase-3---simulacao-de-missoes)
5. [Fase 4 - Estatisticas e Ranking](#fase-4---estatisticas-e-ranking)

---

## Visao Geral

| Fase | Descricao | Prioridade | Status |
|------|-----------|------------|--------|
| 1 | CRUD Completo | Alta | ✅ Concluida |
| 2 | Testes Unitarios | Alta | Pendente |
| 3 | Simulacao de Missoes | Alta | Pendente |
| 4 | Estatisticas e Ranking | Media | Pendente |

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

**Objetivo:** Garantir cobertura minima de 80% para Services e Controllers.

### Servicos para testar

| Servico | Metodos | Prioridade |
|---------|---------|------------|
| AstronautService | listAll, listById, add, delete | Alta |
| SpacecraftService | listAll, add | Media |
| PlanetService | listAll, add | Media |
| MissionService | listAll, add | Media |

### Controllers para testar

| Controller | Metodos | Prioridade |
|------------|---------|------------|
| AstronautController | GET, GET by ID, POST, DELETE | Alta |
| SpacecraftController | GET, POST | Media |
| PlanetController | GET, POST | Media |
| MissionController | GET, POST | Media |

### Tarefas

- [ ] Configurar dependencias de teste (JUnit 5, MockMvc)
- [ ] Criar testes para AstronautService
- [ ] Criar testes para AstronautController
- [ ] Criar testes para outros Services
- [ ] Criar testes para outros Controllers
- [ ] Atualizar cobertura minima para 80%

---

## Fase 3 - Simulacao de Missoes

**Objetivo:** Transformar o cadastro de missoes em uma simulacao real de exploracao espacial.

### Novo Endpoint

```
POST /missions/{id}/start
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
| Tempestade Cosmica | 10% | Missao falha, perda de recursos |

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

- [ ] Criar enum `MissionStatus` (PENDING, IN_PROGRESS, SUCCESS, FAILED)
- [ ] Criar enum `MissionEvent` (SUCCESS, MECHANICAL_FAILURE, ALIEN_ATTACK, COSMIC_STORM)
- [ ] Criar `MissionService.executeMission(Long id)`
- [ ] Criar `FuelService` para validacao de combustivel
- [ ] Criar `DangerService` para calculo de eventos aleatorios
- [ ] Criar `ResourceService` para geracao de recursos
- [ ] Criar endpoint `POST /missions/{id}/start`
- [ ] Atualizar documentacao

---

## Fase 4 - Estatisticas e Ranking

**Objetivo:** Fornecer dados agregados e rankings.

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
GET /missions/history
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

- [ ] Criar `StatsService` com metodos de agregacao
- [ ] Criar endpoint `GET /stats`
- [ ] Criar endpoint `GET /ranking`
- [ ] Criar endpoint `GET /missions/history`
- [ ] Criar `ExplorationService` para descoberta de planetas
- [ ] Criar endpoint `POST /exploration/discover`
- [ ] Atualizar documentacao

---

## Prioridade de Implementacao

```
Fase 1 (CRUD) → Fase 2 (Testes) → Fase 3 (Simulacao) → Fase 4 (Estatisticas)
```

Cada fase depende da anterior. Nao e possivel pular fases.
