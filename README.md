# Scheduled Bank Transfer

Instructor's sample project — demonstrates in code everything the module
asks of the students' final project, in a distinct domain (account /
transfer, instead of foreign currency purchase).

## Prerequisites

Use JDK 25 (the latest LTS release) to build and run the application. The
Maven build targets Java 25; set `JAVA_HOME` to a JDK 25 installation and
ensure its `bin` directory is on `PATH` so Maven and the application use
the same runtime.

## Database

PostgreSQL, started via Docker Compose, schema and seed data managed by
Flyway.

```bash
docker compose up -d
```

This starts Postgres on `localhost:5432` (db `transfer`, user/password
`transfer`/`transfer`, data persisted in the `transfer_pgdata` volume).
No manual setup beyond that — on the next step, Flyway creates the
`accounts` and `transfers` tables and inserts two seed accounts plus one
seed transfer (see `src/main/resources/db/migration`).

**No Docker?** Run against an in-memory H2 database instead, with the
`h2` profile — same Flyway migrations, no setup at all:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 console at `http://localhost:8080/h2-console` (JDBC URL
`jdbc:h2:mem:transfer`, user `sa`, empty password). The database resets
every time the app restarts.

## Running

With Postgres up (or using the `h2` profile above):

```bash
mvn spring-boot:run
```

The API comes up on `http://localhost:8080`. Every business endpoint
requires HTTP Basic authentication:

- username: `instructor`
- password: `training2026`

## API docs (Swagger)

Swagger UI: `http://localhost:8080/swagger-ui.html`
Raw OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Both are public — the one deliberate exception to "every endpoint
requires auth" (see `SecurityConfig`), so anyone can read the contract
without credentials. Actually calling an endpoint from the page still
needs them: use the **Authorize** button (top right) to enter
`instructor` / `training2026` once, then every "Try it out" request
carries them automatically.

## Running the tests

This environment's `~/.m2/settings.xml` has `maven.test.skip=true` as an
active profile property — pass the override explicitly:

```bash
mvn -o test -Dmaven.test.skip=false
```

(`-o` = offline, uses only what is already in the local `.m2`.)

## Endpoints

| Method | Endpoint | UC |
|---|---|---|
| `POST` | `/api/accounts` | UC1 — create account |
| `GET` | `/api/accounts/{cpf}` | UC2 — look up account + transfer history |
| `POST` | `/api/transfers` | UC3 — schedule a transfer |
| `DELETE` | `/api/transfers/{id}?customerCpf=...` | UC4 — cancel a scheduled transfer |

### Example — UC1

```bash
curl -u instructor:training2026 -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Maria Souza","cpf":"43488428095","bankCode":1,"branch":"0001","accountNumber":"12345-6"}'
```

### Example — UC3 (uses the `accountId` returned above)

```bash
curl -u instructor:training2026 -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{"customerCpf":"43488428095","sourceAccountId":1,"destinationBank":341,"destinationBranch":"1234","destinationAccount":"56789-0","amount":1000.00,"scheduledDate":"2026-09-20"}'
```

Bank 1 = Bank of Brazil (same bank as the example customer → fee-free).
Switch `destinationBank` to `341` (Itau) to see the TED fee being
calculated against the current SELIC rate.

### Example — UC4 (uses the `transferId` returned by UC3)

```bash
curl -u instructor:training2026 -X DELETE \
  "http://localhost:8080/api/transfers/1?customerCpf=43488428095"
```

Only the customer who owns the transfer can cancel it, and only before
its `scheduledDate` arrives — both checked in `TransferService.cancel(...)`.

## Where each module concept lives

| Concept | Where |
|---|---|
| Automated tests (pyramid) | `src/test` — pure unit tests (`fee/`), Mockito unit test (`TransferServiceTest`), web+security slice (`TransferControllerSecurityTest`) |
| Architecture | Modular monolith by package (`account`, `transfer`, `bank`, `fee`) |
| Persistence | Spring Data JPA + PostgreSQL; `AccountRepository`/`TransferRepository` are plain `JpaRepository` interfaces — the query methods are derived from their names, no implementation code |
| Schema & seed data | Flyway, `src/main/resources/db/migration` — `V1`/`V2` create the tables, `V3` inserts two accounts and one transfer |
| SOLID | `TransferService`/`AccountService` depend on `BankClient` (an interface), not the concrete implementation — Dependency Inversion; constructor injection everywhere |
| Clean Code | Consistent domain names, short methods, named business exceptions |
| **Adapter** | `bank/BrasilApiBankClient` — the only place that knows BrasilAPI's JSON shape |
| **Strategy** | `fee/SameBankFeeStrategy` and `fee/TedFeeStrategy` — fee calculation varies by transfer type |
| **Facade** | `transfer/TransferService.schedule(...)` — hides the orchestration of account + bank + fee |
| **Singleton** | `config/RestClientConfig` — a single shared `RestTemplate` |
| External integration | `brasilapi.com.br/api/banks/v1/{code}` and `/api/taxas/v1/SELIC`, no API key needed |
| Authentication | `config/SecurityConfig` — HTTP Basic, every endpoint protected |
| Error handling | `web/GlobalExceptionHandler` — 404/409/422/503/400 depending on the exception |
