# GUIA - CosmosX API

Guia completo para configuracao, execucao e contribuicao no projeto.

---

## Sumario

1. [Pre-requisitos](#pre-requisitos)
2. [Instalacao](#instalacao)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Configuracao](#configuracao)
5. [Execucao](#execucao)
6. [Testes](#testes)
7. [Contribuindo](#contribuindo)

---

## Pre-requisitos

| Ferramenta | Versao Minima | Como verificar |
|------------|---------------|----------------|
| Java | 25 | `java -version` |
| Maven | 3.9+ | `mvn -version` |

> O projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`), entao o Maven nao precisa estar instalado globalmente.

## Instalacao

```bash
# 1. Clonar o repositorio
git clone https://github.com/GoomezCode/CosmosX_API.git

# 2. Entrar no diretorio
cd CosmosX_API

# 3. Compilar o projeto
./mvnw clean compile     # Linux/Mac
mvnw.cmd clean compile   # Windows
```

## Estrutura do Projeto

```
CosmosX_API/
├── .opencode/                    # Configuracao opencode (local, gitignored)
│   └── skills/
│       └── java-design-patterns/
│           └── SKILL.md
├── data/                         # Banco H2 (runtime, gitignored)
├── docs/                         # Documentacao
│   ├── GUIA.md
│   ├── ENDPOINTS.md
│   ├── ROADMAP.md
│   └── ARQUITETURA.md
├── src/
│   ├── main/
│   │   ├── java/com/goomez/CosmosX/
│   │   │   ├── CosmosXApplication.java
│   │   │   ├── config/           # CORS, OpenAPI, DataSeeder
│   │   │   ├── controller/       # Endpoints REST
│   │   │   ├── service/          # Logica de negocio
│   │   │   ├── repository/       # Repositorios JPA (Spring Data)
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── dto/              # Request/Response
│   │   │   ├── exception/        # Excecoes customizadas
│   │   │   └── handler/          # Exception Handler
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/goomez/CosmosX/
│       │   ├── CosmosXApplicationTests.java
│       │   ├── controller/       # Testes de endpoint (MockMvc)
│       │   └── service/          # Testes de services (Mockito)
│       └── resources/
│           └── application.properties  # H2 in-memory p/ testes
├── opencode.json                 # Configuracao opencode (local, gitignored)
├── pom.xml                       # Dependencias Maven
└── README.md
```

## Configuracao

### application.properties

```properties
spring.application.name=CosmosX

# H2 file-based (gitignored)
spring.datasource.url=jdbc:h2:file:./data/cosmosx
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

# H2 console (dev)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# CORS (site integrado)
app.cors.allowed-origins=*
```

Porta padrao: **8080**

Para alterar a porta, adicione:
```properties
server.port=8081
```

> O banco H2 e criado automaticamente em `./data/cosmosx` (gitignored). Nos testes, um H2 **in-memory** e usado via `src/test/resources/application.properties`.

### Dependencias Principais

| Dependencia | Descricao |
|-------------|-----------|
| `spring-boot-starter-webmvc` | Framework web |
| `spring-boot-starter-data-jpa` | Persistencia JPA (Hibernate) |
| `spring-boot-h2console` | Banco H2 + console web |
| `spring-boot-starter-validation` | Bean Validation |
| `springdoc-openapi-starter-webmvc-ui` | Swagger/OpenAPI |
| `spring-boot-devtools` | Hot reload (dev) |
| `spring-boot-starter-webmvc-test` | Testes (JUnit 5, MockMvc, Mockito) |
| `jacoco-maven-plugin` | Medida de cobertura (gate >= 80% linhas) |

## Execucao

```bash
# Usando Maven Wrapper
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows

# Ou apos compilar
java -jar target/CosmosX-0.0.1-SNAPSHOT.jar
```

A aplicacao estara disponivel em: `http://localhost:8080`

| Recurso | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

## Testes

```bash
# Executar todos os testes + gate de cobertura (JaCoCo >= 80% linhas)
./mvnw verify

# Executar somente os testes
./mvnw test

# Executar testes especificos
./mvnw test -Dtest=astronautServiceTest
```

> Relatorio de cobertura: `target/site/jacoco/index.html`.

## Contribuindo

### Branches

- `main` - Producao
- `develop` - Desenvolvimento
- `feature/*` - Nova funcionalidade
- `fix/*` - Correcao de bug

### Fluxo

1. Criar branch da feature (`git checkout -b feature/nome-da-feature`)
2. Fazer as alteracoes
3. Commitar com mensagem descritiva
4. Push e criar Pull Request

### Convencao de Commits

```
tipo(escopo): descricao

Exemplos:
feat(mission): adicionar execucao de missao
fix(astronaut): corrigir validacao de ID
docs(endpoints): atualizar documentacao
test(service): adicionar testes para MissionService
```
