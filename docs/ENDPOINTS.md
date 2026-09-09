# ENDPOINTS - CosmosX API

Documentacao detalhada de todos os endpoints da API.

---

## Sumario

1. [Astronautas](#astronautas--astronauts)
2. [Naves](#naves--spacecraft)
3. [Planetas](#planetas--planet)
4. [Missoes](#missoes--mission)

---

## Astronautas `/astronauts`

### Listar todos

```
GET /astronauts
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Daniel",
    "rank": "Commander",
    "experience": 1200
  }
]
```

### Buscar por ID

```
GET /astronauts/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID do astronauta |

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "Daniel",
  "rank": "Commander",
  "experience": 1200
}
```

**Response 404 Not Found:**
```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 404,
  "message": "Astronaut not found with id: 1"
}
```

### Criar

```
POST /astronauts
```

**Request Body:**
```json
{
  "name": "Laura",
  "rank": "Pilot",
  "experience": 800
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Validacao | Descricao |
|-------|------|-----------|-----------|
| name | String | @NotBlank | Nome do astronauta |
| rank | String | @NotBlank | Patente |
| experience | int | @Min(0) | Pontos de experiencia |

**Response 201 Created:**
```json
{
  "id": 2,
  "name": "Laura",
  "rank": "Pilot",
  "experience": 800
}
```

> O campo `id` e gerado automaticamente pelo servidor.

**Response 400 Bad Request:**
```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 400,
  "message": "name: must not be blank, rank: must not be blank"
}
```

### Atualizar

```
PUT /astronauts/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID do astronauta |

**Request Body:**
```json
{
  "name": "Laura",
  "rank": "Captain",
  "experience": 900
}
```

**Response 200 OK:**
```json
{
  "id": 2,
  "name": "Laura",
  "rank": "Captain",
  "experience": 900
}
```

### Deletar

```
DELETE /astronauts/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID do astronauta |

**Response 204 No Content**

---

## Naves `/spacecraft`

### Listar todas

```
GET /spacecraft
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Falcon-X",
    "fuel": 1000,
    "capacity": 5,
    "status": "READY"
  }
]
```

### Buscar por ID

```
GET /spacecraft/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da nave |

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "Falcon-X",
  "fuel": 1000,
  "capacity": 5,
  "status": "READY"
}
```

**Response 404 Not Found:**
```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 404,
  "message": "Spacecraft not found with id: 1"
}
```

### Criar

```
POST /spacecraft
```

**Request Body:**
```json
{
  "name": "Apollo-7",
  "fuel": 800,
  "capacity": 3,
  "status": "READY"
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Validacao | Descricao |
|-------|------|-----------|-----------|
| name | String | @NotBlank | Nome da nave |
| fuel | int | @Min(0) | Combustivel atual |
| capacity | int | @Min(1) | Capacidade de tripulacao |
| status | String | @NotBlank | Status (READY, MAINTENANCE, etc) |

**Response 201 Created:**
```json
{
  "id": 2,
  "name": "Apollo-7",
  "fuel": 800,
  "capacity": 3,
  "status": "READY"
}
```

### Atualizar

```
PUT /spacecraft/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da nave |

**Request Body:**
```json
{
  "name": "Apollo-7",
  "fuel": 500,
  "capacity": 3,
  "status": "MAINTENANCE"
}
```

**Response 200 OK:**
```json
{
  "id": 2,
  "name": "Apollo-7",
  "fuel": 500,
  "capacity": 3,
  "status": "MAINTENANCE"
}
```

### Deletar

```
DELETE /spacecraft/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da nave |

**Response 204 No Content**

---

## Planetas `/planet`

### Listar todos

```
GET /planet
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Zorion",
    "distance": 500,
    "dangerLevel": 3,
    "resources": ["Iron", "Water"]
  }
]
```

### Buscar por ID

```
GET /planet/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID do planeta |

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "Zorion",
  "distance": 500,
  "dangerLevel": 3,
  "resources": ["Iron", "Water"]
}
```

**Response 404 Not Found:**
```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 404,
  "message": "Planet not found with id: 1"
}
```

### Criar

```
POST /planet
```

**Request Body:**
```json
{
  "name": "Mars-X",
  "distance": 500,
  "dangerLevel": 4,
  "resources": ["Iron", "Water"]
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Validacao | Descricao |
|-------|------|-----------|-----------|
| name | String | @NotBlank | Nome do planeta |
| distance | int | @Min(0) | Distancia em unidades espaciais |
| dangerLevel | int | @Min(0) | Nivel de perigo (0-10) |
| resources | List\<String\> | - | Lista de recursos disponiveis |

**Response 201 Created:**
```json
{
  "id": 2,
  "name": "Mars-X",
  "distance": 500,
  "dangerLevel": 4,
  "resources": ["Iron", "Water"]
}
```

### Atualizar

```
PUT /planet/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID do planeta |

**Request Body:**
```json
{
  "name": "Mars-X",
  "distance": 550,
  "dangerLevel": 5,
  "resources": ["Gold", "Water"]
}
```

**Response 200 OK:**
```json
{
  "id": 2,
  "name": "Mars-X",
  "distance": 550,
  "dangerLevel": 5,
  "resources": ["Gold", "Water"]
}
```

### Deletar

```
DELETE /planet/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID do planeta |

**Response 204 No Content**

---

## Missoes `/mission`

### Listar todas

```
GET /mission
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "spacecraftId": 1,
    "planetId": 1,
    "astronauts": [1, 2],
    "status": "PENDING"
  }
]
```

### Buscar por ID

```
GET /mission/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da missao |

**Response 200 OK:**
```json
{
  "id": 1,
  "spacecraftId": 1,
  "planetId": 1,
  "astronauts": [1, 2],
  "status": "PENDING"
}
```

**Response 404 Not Found:**
```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 404,
  "message": "Mission not found with id: 1"
}
```

### Criar

```
POST /mission
```

**Request Body:**
```json
{
  "spacecraftId": 1,
  "planetId": 1,
  "astronauts": [1, 2]
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Validacao | Descricao |
|-------|------|-----------|-----------|
| spacecraftId | Long | @NotNull | ID da nave designada |
| planetId | Long | @NotNull | ID do planeta alvo |
| astronauts | List\<Long\> | @NotEmpty | IDs dos astronautas participantes |

**Response 201 Created:**
```json
{
  "id": 1,
  "spacecraftId": 1,
  "planetId": 1,
  "astronauts": [1, 2],
  "status": "PENDING"
}
```

> O status inicial e sempre `PENDING`.

### Atualizar

```
PUT /mission/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da missao |

**Request Body:**
```json
{
  "spacecraftId": 1,
  "planetId": 2,
  "astronauts": [1, 3],
  "status": "PENDING"
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "spacecraftId": 1,
  "planetId": 2,
  "astronauts": [1, 3],
  "status": "PENDING"
}
```

### Executar (Fase 3)

```
POST /mission/{id}/start
```

Executa a simulacao de exploracao espacial. A missao deve estar com status `PENDING`.

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da missao |

**Steps internos:**
1. Valida combustivel da nave (`fuel >= distance`)
2. Calcula consumo (`fuelConsumed = distance * dangerLevel`)
3. Resolve evento de perigo
4. Gera recursos (se sucesso)
5. Atualiza status (`SUCCESS`/`FAILED`) e consumos

**Regra de perigo (roll 1-100):** sucesso se `roll <= 60 - dangerLevel*5`; janelas de 15% (falha mecanica), 15% (ataque alienigena) e 10% + remanescente (tempestade cosmica).

**Response 200 OK:**
```json
{
  "missionId": 1,
  "status": "SUCCESS",
  "fuelConsumed": 500,
  "resourcesFound": [
    { "resource": "Iron", "quantity": 25 }
  ],
  "events": [
    "Mission completed successfully",
    "Resources collected: Iron (25)"
  ]
}
```

**Response 200 OK (exemplo de falha):**
```json
{
  "missionId": 1,
  "status": "FAILED",
  "fuelConsumed": 500,
  "resourcesFound": [],
  "events": [
    "Mechanical failure detected",
    "Spacecraft damaged"
  ]
}
```

> Efeitos por evento: `MECHANICAL_FAILURE` danifica a nave (`DAMAGED`); `ALIEN_ATTACK` reduz 100 XP dos astronautas; `COSMIC_STORM` faz a missao falhar sem gerar recursos. Em qualquer resultado o combustivel e consumido.

### Deletar

```
DELETE /mission/{id}
```

**Parametros:**
| Parametro | Tipo | Obrigatorio | Descricao |
|-----------|------|-------------|-----------|
| id | Long | Sim | ID da missao |

**Response 204 No Content**

---

## Formato de Erro

Todos os erros seguem o padrao:

```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 400,
  "message": "Descricao do erro"
}
```

| Status | Descricao |
|--------|-----------|
| 400 | Requisicao invalida (validacao) |
| 404 | Recurso nao encontrado |
| 500 | Erro interno do servidor |