# CHANGELOG - CosmosX API

Todas as mudancas relevantes do projeto por versao.

O formato segue as convencoes de [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e o versionamento semantico (SemVer).

---

## [v2.0.1] - 2026-09-09

Compatibilidade de build: permite iniciar o projeto com qualquer JDK 17, 21 ou 25.

### Corrigido
- `java.version` do `pom.xml` reduzido de `25` para `17` (minimo exigido pelo Spring Boot 4.1). O codigo nao usa recursos exclusivos do Java 25, entao compila/roda em qualquer JDK 17+ — eliminando o `release version XX not supported` quando o ambiente tem apenas um JDK 17/21 instalado
- Documentacao atualizada (GUIA, TECHNICAL_REFERENCE, README) com solucao para o erro `release version 17 not supported`, incluindo o caso de a maquina ter apenas o JRE instalado (sem `javac`): passou-se a exigir `JAVA_HOME` apontando para um JDK completo

---

## [v2.0.0] - 2026-09-09

Fechamento do projeto: validacao final e entrega.

### Adicionado
- Teste de integracao E2E (`@SpringBootTest` + MockMvc) cobrindo o fluxo completo: astronauta -> nave -> planeta -> missao -> execucao -> historico/stats/ranking
- Spring Boot Actuator com endpoint `/actuator/health`
- `docs/TECHNICAL_REFERENCE.md` (referencia tecnica consolidada)
- `CHANGELOG.md` (este arquivo)
- Licenca MIT (`LICENSE`) + metadados no `pom.xml` (`<licenses>`, `<developers>`, `<scm>`)
- Badges no `README.md` (versao, testes, cobertura, licenca, Java/Spring)

### Corrigido
- `Mission` com listas imutaveis (`List.of()` / `stream().toList()`) causava `UnsupportedOperationException` ao persistir `resourcesFound`/`astronauts` via JPA. As colecoes agora sao copiadas defensivamente (`ArrayList`).

### Alterado
- Versao do projeto para `2.0.0` (era `0.0.1-SNAPSHOT`)
- Versao da API Swagger/OpenAPI para `v2.0.0`
- README marcado como projeto CONCLUIDO (removido "em desenvolvimento")
- Todas as fases do ROADMAP marcadas como concluidas

---

## [v1.0.0] - 2026-09-09

Preparacao para integracao com o site (portfolio).

### Adicionado
- `spring-boot-starter-data-jpa` + H2 (persistencia em arquivo `./data/`, gitignored)
- Pacote `repository/` com 4 repositorios JPA (Astronaut, Spacecraft, Planet, Mission)
- `DataSeeder` (`CommandLineRunner`) com dados iniciais (Daniel, Laura, Falcon-X, Zorion, missoes)
- Configuracao CORS (`app.cors.allowed-origins`)
- Swagger/OpenAPI (springdoc-openapi 3.1.1) em `/swagger-ui.html` e `/v3/api-docs`
- JaCoCo com gate de cobertura de linhas >= 80%
- `@ElementCollection` (fetch EAGER) para `Planet.resources`, `Mission.astronauts`, `Mission.resourcesFound`; `ResourceFound` como `@Embeddable`

### Corrigido
- `MissionService.executeMission` agora e `@Transactional` (evita missao presa em `IN_PROGRESS`)

### Removido
- Persistencia JSON (arquivos `src/main/resources/data/*.json`)

---

## [v0.4.0] - 2026-09-09

Estatisticas, ranking e exploracao.

### Adicionado
- `GET /stats` (Estatisticas gerais: total, sucessos, falhas, taxa, recursos, recurso top)
- `GET /ranking` (Ranking de astronautas por experiencia)
- `GET /mission/history` (Historico com filtros `status` e `planetId`)
- `POST /exploration/discover` (Descoberta automatica de planetas com recursos aleatorios)
- Persistencia de `fuelConsumed`, `resourcesFound` e `completedAt` na missao; `discoveredAt` no planeta

---

## [v0.3.0] - 2026-09-09

Simulacao de missoes.

### Adicionado
- `POST /mission/{id}/start` (execucao da missao)
- `FuelService` (validacao e calculo de consumo de combustivel)
- `DangerService` (eventos aleatorios: sucesso, falha mecanica, ataque alienigena, tempestade cosmica)
- `ResourceService` (geracao de recursos)
- Enums `MissionStatus` e `MissionEvent`
- Campo `spacecraftId` na entidade `Mission`

---

## [v0.2.0] - 2026-09-09

Testes unitarios.

### Adicionado
- Testes de Services (JUnit 5 + Mockito)
- Testes de Controllers (MockMvc)
- Configuracao do JaCoCo (medida de cobertura)

---

## [v0.1.0] - 2026-09-09

Versao inicial: CRUD completo.

### Adicionado
- CRUD de astronautas, naves, planetas e missoes
- DTO Pattern (Request/Response)
- Bean Validation
- `GlobalExceptionHandler` com respostas de erro padronizadas
- ID auto-gerado nas entidades