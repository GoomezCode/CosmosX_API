# ARQUITETURA - CosmosX API

Documentacao dos padroes de design, convenoes e decisoes de arquitetura do projeto.

---

## Sumario

1. [Visao Geral](#visao-geral)
2. [Arquitetura em Camadas](#arquitetura-em-camadas)
3. [Design Patterns](#design-patterns)
4. [Convencoes](#convencoes)
5. [Persistencia](#persistencia)
6. [Tratamento de Erros](#tratamento-de-erros)
7. [Validacao](#validacao)

---

## Visao Geral

O CosmosX API segue a arquitetura **Layered Architecture** (Arquitetura em Camadas) do Spring MVC, com separacao clara de responsabilidades.

```
┌─────────────────────────────────────────────────┐
│                  Controller                      │
│            (Endpoints REST)                      │
├─────────────────────────────────────────────────┤
│                   Service                        │
│              (Logica de Negocio)                 │
├─────────────────────────────────────────────────┤
│             Data (JSON Files)                    │
│            (Persistencia)                        │
└─────────────────────────────────────────────────┘
```

## Arquitetura em Camadas

### Camada: Controller

**Responsabilidade:** Receber requisicoes HTTP e delegar para o Service.

**Localizacao:** `src/main/java/com/goomez/CosmosX/controller/`

**Regras:**
- Anotados com `@RestController`
- Usam `@RequestMapping` para definir o prefixo da rota
- Recebem DTOs como `@RequestBody`
- Retornam DTOs como resposta
- Usam `ResponseEntity` para controlar HTTP status
- NUNCA contem logica de negocio

**Exemplo:**
```java
@RestController
@RequestMapping("/astronauts")
public class AstronautController {
    private final AstronautService service;

    public AstronautController(AstronautService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AstronautResponse>> listAll() {
        // delega para o service
    }
}
```

### Camada: Service

**Responsabilidade:** Conter a logica de negocio e persistencia.

**Localizacao:** `src/main/java/com/goomez/CosmosX/service/`

**Regras:**
- Anotados com `@Service`
- Recebem dependencias via construtor (injecao de dependencia)
- Contem a logica de negocio
- Lanca excecoes customizadas
- NUNCA retornam ResponseEntity

**Exemplo:**
```java
@Service
public class AstronautService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/resources/data/astronaut.json");

    public Astronaut listById(long id) {
        return listAll().stream()
            .filter(a -> a.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Astronaut not found"));
    }
}
```

### Camada: Model

**Responsabilidade:** Representar as entidades do dominio.

**Localizacao:** `src/main/java/com/goomez/CosmosX/model/`

**Regras:**
- POJOs simples com getters/setters
- Sem anotacoes JPA (persistencia em JSON)
- Construtor vazio + construtor com os campos principais
- NUNCA contem logica de negocio

**Exemplo:**
```java
public class Astronaut {
    private long id;
    private String name;
    private String rank;
    private int experience;

    public Astronaut() {}

    public Astronaut(long id, String name, String rank, int experience) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.experience = experience;
    }

    // getters e setters
}
```

### Camada: DTO

**Responsabilidade:** Separar a representacao da API das entidades internas.

**Localizacao:** `src/main/java/com/goomez/CosmosX/dto/`

**Regras:**
- Records Java (imutaveis)
- Um DTO para entrada (Request) e outro para saida (Response)
- Usam anotacoes de validacao (`@NotBlank`, `@Min`, etc.)

**Exemplo:**
```java
// Entrada
public record AstronautRequest(
    @NotBlank String name,
    @NotBlank String rank,
    @Min(0) int experience
) {}

// Saida
public record AstronautResponse(
    Long id,
    String name,
    String rank,
    int experience
) {}
```

## Design Patterns

### 1. DTO Pattern

Separa entidades internas da representacao da API.

**Beneficios:**
- Protege a entidade interna
- Permite validacao por endpoint
- Facilita versionamento da API

**Implementacao:**
- `AstronautRequest` → entrada
- `AstronautResponse` → saida

### 2. Global Exception Handler

Trata todas as excecoes de forma centralizada.

**Beneficios:**
- Respostas padronizadas de erro
- Elimina codigo de tratamento duplicado
- Melhor experiencia para o cliente

**Implementacao:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        // retorna 404 com JSON padronizado
    }
}
```

### 3. Custom Exceptions

Excecoes especificas do dominio.

**Exemplo:**
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### 4. Response Entity Pattern

Controla HTTP status codes explicitamente.

**Status codes usados:**
| Metodo | Status | Descricao |
|--------|--------|-----------|
| GET | 200 OK | Sucesso |
| POST | 201 Created | Recurso criado |
| DELETE | 204 No Content | Removido sem conteudo |
| Erro | 400 Bad Request | Requisicao invalida |
| Erro | 404 Not Found | Recurso nao encontrado |
| Erro | 500 Internal Server Error | Erro do servidor |

## Convencoes

### Nomenclatura de Classes

| Tipo | Padrao | Exemplo |
|------|--------|---------|
| Classe | PascalCase | `AstronautService` |
| Metodo | camelCase | `listAll()` |
| Variavel | camelCase | `astronautList` |
| Constante | SCREAMING_SNAKE_CASE | `MAX_FUEL` |
| Pacote | lowercase | `com.goomez.CosmosX.service` |

### Nomenclatura de Arquivos

| Tipo | Padrao | Exemplo |
|------|--------|---------|
| Controller | `XxxController.java` | `AstronautController.java` |
| Service | `XxxService.java` | `AstronautService.java` |
| Model | `Xxx.java` | `Astronaut.java` |
| DTO Request | `XxxRequest.java` | `AstronautRequest.java` |
| DTO Response | `XxxResponse.java` | `AstronautResponse.java` |
| Exception | `XxxException.java` | `ResourceNotFoundException.java` |

### Estrutura de Pacotes

```
com.goomez.CosmosX/
├── controller/     # Controllers REST
├── service/        # Services de negocio
├── model/          # Entidades
├── dto/            # Data Transfer Objects
├── exception/      # Excecoes customizadas
└── handler/        # Exception handlers
```

## Persistencia

### Formato Atual: JSON Files

**Localizacao:** `src/main/resources/data/`

**Arquivos:**
- `astronaut.json`
- `spacecraft.json`
- `planet.json`
- `mission.json`

**Como funciona:**
1. Service le o arquivo JSON inteiro
2. Converte para `List<Model>` usando Jackson
3. Opera na lista (adiciona, remove, filtra)
4. Reescreve o arquivo JSON atualizado

**Exemplo:**
```java
public List<Astronaut> listAll() {
    return mapper.readValue(arquivo, new TypeReference<List<Astronaut>>() {});
}
```

**Limitacoes:**
- Nao suporta concorrencia (duas escritas simultaneas podem corromper dados)
- Performance ruim para grandes volumes (leitura/escrita de arquivo inteiro)
- Sem transacoes

**Futura migracao:** H2 ou PostgreSQL com JPA

## Tratamento de Erros

### Padrao de Resposta de Erro

```json
{
  "timestamp": "2026-09-09T10:00:00",
  "status": 400,
  "message": "Descricao do erro"
}
```

### Excecoes Tratadas

| Excecao | Status | Descricao |
|---------|--------|-----------|
| `ResourceNotFoundException` | 404 | Recurso nao encontrado |
| `MethodArgumentNotValidException` | 400 | Erro de validacao |
| `InsufficientFuelException` | 400 | Combustivel insuficiente para a missao |
| `InvalidMissionStateException` | 400 | Missao em estado invalido para execucao |
| `Exception` | 500 | Erro generico |

## Validacao

### Anotacoes Usadas

| Anotacao | Descricao | Exemplo |
|----------|-----------|---------|
| `@NotBlank` | String nao pode ser vazia | `@NotBlank String name` |
| `@NotNull` | Campo nao pode ser nulo | `@NotNull Long id` |
| `@NotEmpty` | Lista nao pode ser vazia | `@NotEmpty List<Long> ids` |
| `@Min` | Valor minimo | `@Min(0) int fuel` |

### Como usar

1. Adicionar anotacoes no DTO de Request
2. Usar `@Valid` no Controller

```java
@PostMapping
public ResponseEntity<AstronautResponse> create(@Valid @RequestBody AstronautRequest request) {
    // validacao automatica pelo Spring
}
```
