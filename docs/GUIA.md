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
├── .opencode/                    # Configuracao opencode
│   └── skills/
│       └── java-design-patterns/
│           └── SKILL.md
├── docs/                         # Documentacao
│   ├── GUIA.md
│   ├── ENDPOINTS.md
│   ├── ROADMAP.md
│   └── ARQUITETURA.md
├── src/
│   ├── main/
│   │   ├── java/com/goomez/CosmosX/
│   │   │   ├── CosmosXApplication.java
│   │   │   ├── controller/       # Endpoints REST
│   │   │   ├── service/          # Logica de negocio
│   │   │   ├── model/            # Entidades
│   │   │   ├── dto/              # Request/Response
│   │   │   ├── exception/        # Excecoes customizadas
│   │   │   └── handler/          # Exception Handler
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data/             # Arquivos JSON (banco de dados)
│   │           ├── astronaut.json
│   │           ├── spacecraft.json
│   │           ├── planet.json
│   │           └── mission.json
│   └── test/
│       └── java/com/goomez/CosmosX/
│           └── CosmosXApplicationTests.java
├── opencode.json                 # Configuracao opencode
├── pom.xml                       # Dependencias Maven
└── README.md
```

## Configuracao

### application.properties

```properties
spring.application.name=CosmosX
```

Porta padrao: **8080**

Para alterar a porta, adicione:
```properties
server.port=8081
```

### Dependencias Principais

| Dependencia | Descricao |
|-------------|-----------|
| `spring-boot-starter-webmvc` | Framework web |
| `spring-boot-starter-validation` | Bean Validation |
| `spring-boot-devtools` | Hot reload (dev) |

## Execucao

```bash
# Usando Maven Wrapper
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows

# Ou apos compilar
java -jar target/CosmosX-0.0.1-SNAPSHOT.jar
```

A aplicacao estara disponivel em: `http://localhost:8080`

## Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes especificos
./mvnw test -Dtest=astronautServiceTest
```

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
