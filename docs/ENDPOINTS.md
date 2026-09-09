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

**Response 400 Bad Request:**
```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 400,
  "message": "name: must not be blank, rank: must not be blank"
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
    "planetId": 1,
    "astronauts": [1, 2],
    "status": "PENDING"
  }
]
```

### Criar

```
POST /mission
```

**Request Body:**
```json
{
  "planetId": 1,
  "astronauts": [1, 2]
}
```

**Campos Obrigatorios:**
| Campo | Tipo | Validacao | Descricao |
|-------|------|-----------|-----------|
| planetId | Long | @NotNull | ID do planeta alvo |
| astronauts | List\<Long\> | @NotEmpty | IDs dos astronautas participantes |

**Response 201 Created:**
```json
{
  "id": 1,
  "planetId": 1,
  "astronauts": [1, 2],
  "status": "PENDING"
}
```

> O status inicial e sempre `PENDING`. A execucao da missao ainda nao foi implementada.

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
