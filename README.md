<h1 align="center">🚀 CosmosX API</h1>

API REST em **Java + Spring Boot** para gerenciamento de uma agência espacial fictícia: astronautas, naves espaciais, planetas e missões.

> ⚠️ **Projeto em desenvolvimento.** O CRUD básico das entidades já está funcionando, mas a parte mais interessante do projeto — a **simulação de exploração espacial** — ainda não foi implementada. Veja a seção [Roadmap](#-roadmap--próximos-passos) abaixo.

---

## 📌 Sobre o projeto

O CosmosX API é uma API para controlar operações de uma frota espacial. Hoje ela permite cadastrar e consultar:

- 👨‍🚀 Astronautas
- 🚀 Naves espaciais (spacecrafts)
- 🌍 Planetas
- 🛰️ Missões

Os dados são persistidos em arquivos `.json` (sem banco de dados por enquanto), localizados em `src/main/java/com/goomez/CosmosX/Data/`.

## 🛠️ Tecnologias

- Java 26
- Spring Boot 4.1.0 (Spring Web MVC)
- Jackson (`tools.jackson`) para serialização/desserialização JSON
- Maven (com Maven Wrapper — `mvnw` / `mvnw.cmd`)

## 📂 Estrutura do projeto

```
src/main/java/com/goomez/CosmosX/
├── Controller/       # Endpoints REST
├── Service/          # Regras de negócio e persistência em JSON
├── Model/            # Entidades (Astronaut, Mission, Planet, Spacecraft)
├── Data/              # Arquivos .json usados como "banco de dados"
└── CosmosXApplication.java
```

## ▶️ Como executar

Pré-requisitos: **Java 26** e **Maven** (ou use o wrapper incluso no projeto).

```bash
# clonar o repositório
git clone https://github.com/GoomezCode/CosmosX_API.git
cd CosmosX_API

# rodar a aplicação
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows
```

A aplicação sobe por padrão em `http://localhost:8080`.

## 📡 Endpoints disponíveis (implementados atualmente)

### Astronautas — `/astronauts`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/astronauts` | Lista todos os astronautas |
| GET | `/astronauts/{id}` | Busca um astronauta por ID |
| POST | `/astronauts` | Cadastra um novo astronauta |
| DELETE | `/astronauts/{id}` | Remove um astronauta |

**Exemplo de corpo (POST):**
```json
{
  "id": 2,
  "name": "Laura",
  "rank": "Pilot",
  "experience": 800
}
```

### Naves — `/spacecraft`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/spacecraft` | Lista todas as naves |
| POST | `/spacecraft` | Cadastra uma nova nave |

```json
{
  "id": 2,
  "name": "Falcon-X",
  "fuel": 1000,
  "capacity": 5,
  "status": "READY"
}
```

### Planetas — `/planet`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/planet` | Lista todos os planetas |
| POST | `/planet` | Cadastra um novo planeta |

```json
{
  "id": 2,
  "name": "Mars-X",
  "distance": 500,
  "dangerLevel": 4,
  "resources": ["Iron", "Water"]
}
```

### Missões — `/mission`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/mission` | Lista todas as missões |
| POST | `/mission` | Cadastra uma nova missão |

```json
{
  "id": 1,
  "planetId": 1,
  "astronauts": [1, 2],
  "status": "PENDING"
}
```

> Por enquanto, `POST /mission` apenas **salva** o registro da missão. Ela ainda não é *executada* de fato (ver roadmap).

---

## 🧭 Roadmap / Próximos passos

Esta é a parte que ainda **não foi implementada** e é o principal objetivo do projeto: transformar o cadastro de missões em uma **simulação real de exploração espacial**.

### 🎮 Simulação de exploração
Em vez de só salvar dados, a ideia é que a missão vire uma operação de verdade:

```
POST /missions/1/start
```

Resposta esperada:
```json
{
  "missionId": 1,
  "status": "SUCCESS",
  "fuelConsumed": 300,
  "resourcesFound": ["Gold", "Water"]
}
```

### ⛽ Sistema de combustível
Cada planeta tem uma `distance`, e a nave só pode viajar até lá se tiver combustível suficiente:

```java
if (ship.getFuel() < planet.getDistance()) {
    throw new RuntimeException("Insufficient fuel");
}
```

### ☄️ Sistema de perigo
Cada planeta possui um `dangerLevel`. Ao iniciar a missão, um evento aleatório é sorteado e pode resultar em:

- Falha mecânica
- Ataque alienígena
- Tempestade cósmica
- Sucesso

### 💎 Recursos descobertos
Planetas podem conter recursos como `Iron`, `Gold`, `Titanium`, `Crystal`, `Water`. Ao final de uma missão bem-sucedida, um ou mais recursos (com quantidade) são gerados, por exemplo:

```json
{
  "resource": "Titanium",
  "quantity": 35
}
```

### 📊 Estatísticas gerais
```
GET /stats
```
```json
{
  "missions": 48,
  "successes": 39,
  "failures": 9,
  "resourcesCollected": 1250
}
```

### 🏆 Funcionalidades avançadas
- **Ranking de astronautas** — `GET /ranking`
  ```json
  [{ "name": "Daniel", "experience": 5200 }]
  ```
- **Histórico de missões** — `GET /missions/history`
- **Descoberta automática de planetas** — `POST /exploration/discover`
  ```json
  {
    "name": "Nebulon-7",
    "distance": 1200,
    "dangerLevel": 6
  }
  ```

---

## ✅ Status atual

- [x] CRUD de astronautas
- [x] CRUD de naves
- [x] CRUD de planetas
- [x] Cadastro simples de missões
- [ ] Execução de missões (`/missions/{id}/start`)
- [ ] Sistema de combustível
- [ ] Sistema de perigo / eventos aleatórios
- [ ] Geração de recursos ao fim da missão
- [ ] Endpoint de estatísticas (`/stats`)
- [ ] Ranking de astronautas
- [ ] Histórico de missões
- [ ] Descoberta automática de planetas

## 🤝 Contribuindo

Este é um projeto pessoal em desenvolvimento. Sugestões, issues e pull requests são bem-vindos!

## 📄 Licença

Nenhuma licença definida até o momento.
