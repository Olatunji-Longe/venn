# Fund Loading Application (Velocity Limits)

A Spring Boot service that accepts or declines attempts to load funds into customer accounts in real time, enforcing daily and weekly velocity limits and a per-day request count limit. It also exposes OpenAPI/Swagger docs for API reference.

## Prerequisites for running the application
- Java 21 (JDK)
- Gradle (wrapper included; use `./gradlew` / `gradlew.bat`)
- No external database required (uses in-memory H2)

Optional:
- curl or HTTP client (e.g., Postman) to exercise the API
- Browser access for Swagger UI

## Instructions to Run the Application and Tests
- Build (runs all checks):
  - macOS/Linux: `./gradlew clean build`
  - Windows: `gradlew clean build`

---

### Running the Application
- Run the app:
  - The active profile is `dev`. In order to run the application with a profile different from `dev` or `test` (e.g., `prod`), the production environment variables for database connection will need to be set on the host where the application will be run. See: [application.yml](src/main/resources/application.yml)

    - macOS/Linux: 
      ```shell
      ./gradlew bootRun --args='--spring.profiles.active=dev'
      ```
    - Windows: 
      ```
      gradlew.bat bootRun --args="--spring.profiles.active=dev"
      ```
    - Alternatively, run the jar after build: 
      ```shell
      java -jar build/libs/venn-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
      ```

- Run tests only:
  - macOS/Linux: `./gradlew test`
  - Windows: `gradlew.bat test`

---

### Quick API check with curl
POST a single load attempt to the versioned endpoint. Example:

```shell

curl -X POST http://localhost:8090/api/v1.0/funds/load \
  -H 'Content-Type: application/json' \
  -d '{
        "id":"1234",
        "customer_id":"42",
        "load_amount":"$123.45",
        "time":"2018-01-01T10:15:30Z"
      }'
```
Response (accepted example):
```json
{ "id": "1234", "customer_id": "42", "accepted": true }
```
If a duplicate load `id` for the same `customer_id` is received again, the service returns `204 No Content` (duplicate ignored by design).

---

### Inspecting the database

While the application is running on `dev` profile, the H2 console is available at: http://localhost:8090/h2-console.

See: [application-dev.yml](src/main/resources/application-dev.yml) for database connection attributes

---

## API Documentation (Springdoc / Swagger)
After starting the app, visit:
- Swagger UI: http://localhost:8090/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8090/v3/api-docs

---

## Design Notes & Architecture Decisions
- Core requirements from [Hometask.pdf](Hometask.pdf):
  - Enforce velocity limits per customer:
    - Max $5,000 per day
    - Max $20,000 per week
    - Max 3 loads per day (count-based, regardless of amount)
  - Input arrives in chronological order; duplicate load IDs per customer are ignored.
  - Day boundary: midnight UTC; Week starts Monday (UTC).


- Architecture
  - Controller: `FundController` exposes `POST /api/v1.0/funds/load` and returns acceptance decisions.
  - Service: `FundService` encapsulates business rules, including daily/weekly sums and daily count checks.
  - Persistence: JPA repositories over in-memory H2 for fast, isolated runs and tests.
  - Mapping: MapStruct for DTO-to-domain mapping to keep controllers lean.
  - Configuration: `ApplicationConfiguration` registers a Jackson `JsonMapper` tuned for robustness.
  - Limit values kept in configuration properties (`CustomerLimitProperties`).
  - Database migrations: Liquibase manages schema evolution deterministically.
  - Testing: Unit and slice tests validate controller, service, and repository logic with representative input/output fixtures.
  - API versioning: Version present in the URL path (`/api/v1.0/...`) for evolvability.
  - Caching for performance gains around data lookups when querying the database 

---

## Failure Strategies and Logging
- Validation: Incoming requests are validated via Jakarta Bean Validation; invalid payloads yield 400 responses with clear messages.
- Idempotency/Duplicates: Repeated `id` for the same `customer_id` is safely ignored; the controller returns `204 No Content` to signal no-op.
- Global error handling: `ApiControllerExceptionAdvice` centralizes exception-to-HTTP mapping for predictable responses.
- Logging: Uses (SLF4J) with concise messages around failures and key decision points.
- Safe defaults: Unknown fields in JSON are ignored; serialization of empty beans won’t fail, supporting forward compatibility.

---

## Assumptions Made
- Currency is USD with dollar-prefixed string amounts (e.g., `$123.45`).
- All times are ISO-8601 with `Z` (UTC) Timezone, and are processed in UTC.
- Input events are already in ascending chronological order.
- Per instructions, duplicates (same `id` for a `customer_id`) are ignored.
- Daily boundary is 00:00:00 UTC; weeks start Monday 00:00:00 UTC.
- In-memory H2 is sufficient for this exercise; no external DB configuration required.

---

## Possible Future Improvements
- Persistence hardening: Switch to a durable RDBMS profile (PostgreSQL) with proper indexing and connection pooling.
- Idempotency keys and stronger deduping across restarts using unique constraints and outbox patterns.
- Authentication/authorization (e.g., OAuth 2.1) and rate limiting to protect endpoints.
- Bulk/file ingestion pathway for `input.txt` with streaming, back-pressure, and an explicit batch API or CLI tool.
- Redact sensitive data from the logged body of error requests as necessary
- Improve Observability: Add metrics (Micrometer/Prometheus), and health/readiness probes.
- Configuration: Externalize customer limits with dynamic refresh (e.g., configurable values from a database) and feature flags.
- Performance: Using database windowed queries for larger datasets.
