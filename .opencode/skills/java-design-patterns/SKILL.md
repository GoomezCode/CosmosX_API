---
name: java-design-patterns
description: Use when applying Java design patterns to Spring Boot projects. Covers Singleton, Factory, DTO, Repository, Builder, Strategy, and exception handling patterns.
---

# Java Design Patterns

Ao trabalhar em projetos Java/Spring Boot, aplique os padrões de design abaixo quando relevante.

## 1. DTO Pattern

Separe modelos de API (request/response) das entidades internas.

- Crie pacotes `dto/` separados dos `model/`
- Use records para DTOs imutáveis
- Nunca exponha entidades JPA diretamente nos endpoints

```java
// DTO de entrada
public record AstronautRequest(String name, String rank, int experience) {}

// DTO de saída
public record AstronautResponse(Long id, String name, String rank, int experience) {}
```

## 2. Repository Pattern

Separe lógica de persistência dos Services.

- Crie interfaces que extendem `JpaRepository` ou `CrudRepository`
- Implementações customizadas ficam em classes separadas
- Services dependem das interfaces, não das implementações

```java
public interface AstronautRepository extends JpaRepository<Astronaut, Long> {
    List<Astronaut> findByRank(String rank);
}
```

## 3. Service Pattern

Services contêm lógica de negócio e dependem de Repositories (não de DAOs ou Controllers).

- Anote com `@Service`
- Use injeção via construtor (nunca `@Autowired` em campo)
- Um Service por domínio de negócio

```java
@Service
public class AstronautService {
    private final AstronautRepository repository;

    public AstronautService(AstronautRepository repository) {
        this.repository = repository;
    }
}
```

## 4. Global Exception Handler

Use `@ControllerAdvice` com `@ExceptionHandler` para tratar erros centralmente.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(LocalDateTime.now(), 404, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(LocalDateTime.now(), 400, message));
    }
}
```

## 5. Builder Pattern

Use para objetos com muitos campos opcionais. Em Java moderno, prefira Records ou Static Factory Methods.

```java
public class MissionBuilder {
    private Long planetId;
    private List<Long> astronauts = new ArrayList<>();
    private String status = "PENDING";

    public MissionBuilder planetId(Long planetId) {
        this.planetId = planetId;
        return this;
    }

    public MissionBuilder astronauts(List<Long> astronauts) {
        this.astronauts = astronauts;
        return this;
    }

    public Mission build() {
        return new Mission(null, planetId, astronauts, status);
    }
}
```

## 6. Strategy Pattern

Use quando múltiplos comportamentos podem ser trocad em runtime.

```java
public interface DangerStrategy {
    int calculateDanger(Planet planet, Spacecraft spacecraft);
}

@Component
public class DefaultDangerStrategy implements DangerStrategy {
    public int calculateDanger(Planet planet, Spacecraft spacecraft) {
        return planet.getDangerLevel();
    }
}
```

## 7. Factory Pattern

Use para criar objetos sem expor lógica de criação ao caller.

```java
@Component
public class MissionFactory {
    private final AstronautRepository astronautRepo;
    private final PlanetRepository planetRepo;

    public Mission create(MissionRequest request) {
        if (!astronautRepo.existsById(request.astronautId()))
            throw new ResourceNotFoundException("Astronaut not found");
        return new Mission(null, request.planetId(), request.astronauts(), "PENDING");
    }
}
```

## 8. Validação com Bean Validation

Use `@Valid` nos controllers e anotações de validação nos DTOs.

```java
public record AstronautRequest(
    @NotBlank String name,
    @NotBlank String rank,
    @Min(0) int experience
) {}
```

## 9. Response Pattern padronizado

Use um wrapper para respostas paginadas ou de coleção.

```java
public record PagedResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements
) {}
```

## Convenções

- Classes: PascalCase (`AstronautService`, não `astronautService`)
- Pacotes: lowercase (`com.goomez.cosmosx.service`, não `Service`)
- Métodos: camelCase
- DTOs: `XxxRequest` (entrada), `XxxResponse` (saída)
- Exceptions customizadas: `XxxException` extends `RuntimeException`
- Controllers: `XxxController`
- Services: `XxxService`
- Repositories: `XxxRepository`
