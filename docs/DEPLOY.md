# Deploy - CosmosX API

Guia de publicacao da API em producao.

## URL de producao

A API esta no ar:

- **Base URL:** https://cosmosx-api.onrender.com
- **Health check:** https://cosmosx-api.onrender.com/actuator/health
- **Swagger UI:** https://cosmosx-api.onrender.com/swagger-ui.html

> **Atencao:** o banco H2 e efemero no tier gratuito do Render. O disco e recriado a cada deploy e os dados sao reiniciados (o `DataSeeder` repopula quando o banco esta vazio). Para uso com dados persistentes, use uma VM com disco duravel (ex.: Oracle Cloud Always Free) ou um banco externo (PostgreSQL via `spring.datasource.*`).

## Como o deploy funciona

O Render constroi a imagem a partir do `Dockerfile` da raiz do repositorio (multi-stage):

1. Stage `build`: imagem `maven:3.9-eclipse-temurin-17` compila o projeto e gera o jar
2. Stage runtime: imagem `eclipse-temurin:17-jre` copia apenas o jar e executa

O container roda com:

```
java -Xms256m -Xmx256m -XX:MaxMetaspaceSize=160m -jar /app/app.jar --server.port=$PORT
```

- `PORT` e injetado pelo Render; `--server.port=$PORT` faz o Spring Boot ouvir nessa porta
- As flags de memoria estao ajustadas para o tier gratuito (512 MB)

## Publicando no Render

1. Empurre o codigo para `main` no GitHub (o Render faz auto-deploy a cada push)
2. Crie um **Web Service** (nao use Blueprint nem Static Site) apontando para o repositorio, branch `main`
3. Configure o **Environment** como **`Docker`** (nao `Node`) — o Render usa o `Dockerfile` da raiz
4. Deixe **Build Command** e **Start Command** vazios (ignorados quando o Environment e Docker)
5. Escolha a regiao mais proxima e o plano **Free**
6. Confirme o deploy e valide o health check em `/actuator/health`

### Health check

Em **Settings > Health Check Path**, definir: `/actuator/health`

## Build local da imagem

```bash
docker build -t cosmosx-api .
docker run -p 8080:8080 -e PORT=8080 cosmosx-api
# -> http://localhost:8080/actuator/health
```

## Limites do tier gratuito

| Item | Comportamento |
|------|---------------|
| RAM | 512 MB (a imagem usa no maximo ~256 MB de heap) |
| Uptime | 750 horas/mes; a instancia "adormece" apos ~15 min sem trafego |
| Cold start | ~30-60 s na primeira requisicao apos dormir |
| Disco | Efemero (o H2 `./data/` reseta a cada deploy) |
| Custo | U$ 0 (sem cartao de credito) |

## Troubleshooting de deploy

| Erro | Causa | Solucao |
|------|-------|---------|
| `./mvnw: Permission denied` | Bit de execucao do `mvnw` ausente no Git | `git update-index --chmod=+x mvnw` + commit |
| `The JAVA_HOME ... not defined correctly` | Environment `Node` em vez de `Docker` (sem JDK) | Trocar Environment para `Docker` no Render |
| Comando de build estranho (ex.: `...yarn`) | Campo Build Command preenchido | Deixar vazio quando o Environment e Docker |
| 404/erro na primeira chamada | Instancia "adormecida" (cold start) | Aguardar ~1 min e tentar de novo |