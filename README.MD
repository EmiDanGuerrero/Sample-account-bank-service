# 🏦 Bank Account Service

Microservicio REST para gestionar **cuentas bancarias**, pensado como ejercicio técnico, pero construido con criterios de proyecto real:

- Modelo de dominio claro (BankAccount) y arquitectura **hexagonal**.
- Persistencia en **H2 en memoria** mediante Spring Data JPA.
- Exposición de un CRUD completo y un endpoint adicional de **“resumen de cuenta”** que consume el propio API.
- Manejo centralizado de errores con respuestas JSON homogéneas.
- Logging estructurado en **JSON**, listo para ser enviado a un stack **ELK** (Elasticsearch + Logstash + Kibana).
- Uso moderado de **Lombok** para reducir código repetitivo.

---

## 1. Stack y versiones

- **Java** 21  
- **Spring Boot** 3.5.7  
- **Spring Web / RestClient**
- **Spring Data JPA + H2** (BD en memoria)
- **Jakarta Validation** (`@Valid`, `@NotBlank`, etc.)
- **Lombok** (getters/setters/builders)
- **Logback** + `logstash-logback-encoder` (logs JSON)
- **Logbook (Zalando)** para trazas HTTP
- **ELK 9.2.1** (Elasticsearch + Logstash + Kibana) vía Docker (opcional)
- JUnit 5 + Mockito (tests de dominio y de integración, opcionales)

> 💡 Si abrís el proyecto en un IDE (Eclipse/IntelliJ/STS) vas a necesitar:
> - plugin de **Lombok** instalado  
> - **annotation processing** habilitado  
> De lo contrario, el código puede compilar por Maven pero verse con errores en el editor.

---

## 2. Qué resuelve este microservicio

### Funcionalidad de negocio

- Alta, baja lógica, modificación y consulta de **cuentas bancarias**.
- Cada cuenta incluye:
  - `accountNumber`, `cbu`, `ownerName`, `ownerDocument`
  - `currency`, `balance`, `status`, `branchCode`
  - `createdAt`, `updatedAt`
- Reglas de negocio:
  - El **CBU debe ser único** (validación en servicio + constraint en BD).
  - El DELETE sobre `/accounts/{id}` realiza un **cierre lógico** (`status = CLOSED`), no un borrado físico.
  - El endpoint `/accounts/{id}/summary` calcula una vista simplificada de la cuenta e incorpora una señal de riesgo si el saldo es bajo.

### Endpoint que se llama a sí mismo

El enunciado pedía que un endpoint del microservicio consuma otro endpoint del mismo servicio.  
En este caso:

- `GET /api/v1/accounts/{id}/summary`
- Internamente usa **RestClient** para llamar a:
  - `GET /api/v1/accounts/{id}`
- A partir de esa respuesta se construye un `AccountSummaryResponse` con:
  - datos básicos de la cuenta
  - flag `lowBalanceRisk` (`true` si el saldo es menor a cierto umbral).

---

## 3. Arquitectura y organización de código

Se sigue un esquema **Ports & Adapters (arquitectura hexagonal)** para separar dominio de infraestructura.

```text
com.bank_services.account
 ├─ domain
 │   ├─ model        # BankAccount, enums (Currency, AccountStatus)
 │   ├─ port         # AccountRepositoryPort (puerto de salida)
 │   ├─ service      # AccountService con reglas de negocio
 │   └─ exception    # Excepciones: ResourceNotFound, DuplicateResource
 ├─ application
 │   └─ dto          # DTOs de request/response para la API pública
 └─ infrastructure
     ├─ persistence  # JPA: BankAccountEntity, SpringDataAccountRepository,
     │               #      AccountRepositoryAdapter (adapter del puerto)
     ├─ web          # AccountController + SelfAccountClient (RestClient)
     ├─ error        # ApiError + GlobalExceptionHandler
     ├─ config       # Configuración de Logbook, etc.
     └─ logging      # (si aplica) clases auxiliares para logging

### Dominio

- `BankAccount`:
  - implementado con **Lombok** (`@Data`, `@Builder`, etc.) para mantener el modelo limpio.
  - métodos de negocio:
    - `updateFrom(...)` para aplicar cambios sin perder el estado crítico (por ejemplo, `status`).
    - `close()` para cambiar a `CLOSED` y actualizar timestamps.
- `AccountService`:
  - encapsula la lógica de negocio:
    - valida unicidad de CBU.
    - traduce “no encontrado” en `ResourceNotFoundException`.
    - aplica reglas en `create`, `update`, `delete`.

### Persistencia

- `BankAccountEntity`:
  - entidad JPA mapeada sobre la tabla `bank_accounts`.
  - `@GeneratedValue(strategy = GenerationType.UUID)` para IDs.
  - constraint único en `cbu`.
- `AccountRepositoryPort`:
  - expone operaciones de dominio (`save`, `findById`, `existsByCbu`, etc.).
- `AccountRepositoryAdapter`:
  - adapta `AccountRepositoryPort` a un `JpaRepository`.
  - se encarga de mapear `BankAccount` ↔ `BankAccountEntity`.

### API REST

- `AccountController`:
  - expone CRUD en `/api/v1/accounts`.
  - recibe/retorna DTOs (`BankAccountRequest`, `BankAccountResponse`).
- Manejo de errores:
  - `GlobalExceptionHandler` con `@ControllerAdvice`.
  - Todas las respuestas de error salen como `ApiError` (timestamp, status, error, message, path).

---

## 4. Endpoints principales

Base: `http://localhost:8080/api/v1/accounts`

| Método | URL         | Descripción                         |
|--------|-------------|-------------------------------------|
| POST   | `/`         | Crear cuenta                        |
| GET    | `/`         | Listar todas las cuentas            |
| GET    | `/{id}`     | Obtener cuenta por ID               |
| PUT    | `/{id}`     | Actualizar cuenta                   |
| DELETE | `/{id}`     | Cerrar cuenta (status = CLOSED)     |
| GET    | `/{id}/summary` | Resumen de cuenta (self-call REST) |

### Ejemplos rápidos con `curl` (Windows CMD)

**Crear:**

```bat
curl -X POST "http://localhost:8080/api/v1/accounts" ^
  -H "Content-Type: application/json" ^
  -d "{""accountNumber"":""ACC-001"",""cbu"":""1230000100000000000011"",""ownerName"":""Juan Perez"",""ownerDocument"":""30123456"",""currency"":""ARS"",""balance"":500.00,""branchCode"":""001""}"

**Resumen de cuenta:**

```bat
curl -X GET "http://localhost:8080/api/v1/accounts/{ID}/summary"

## 5. Base de datos H2

La aplicación utiliza **H2 en memoria**, por lo que no requiere instalar ninguna base externa para ejecutarse.

- **Consola H2:**  
  http://localhost:8080/h2-console

- **Credenciales por defecto:**  
  - JDBC URL: `jdbc:h2:mem:bankdb`  
  - Usuario: `sa`  
  - Contraseña: *(vacía)*

El esquema se genera automáticamente a partir de la entidad JPA (`BankAccountEntity`).

---

## 6. Logging y Observabilidad

### Logging interno

La aplicación usa:

- **Logback** con encoder JSON (`LogstashEncoder`)
- **Logbook (Zalando)** para registrar:
  - método HTTP
  - URL
  - cuerpo enviado/recibido
  - tiempo de respuesta
  - código de estado

Esto permite analizar peticiones sin necesidad de herramientas externas.

### Integración opcional con ELK

El proyecto incluye un stack básico **ELK (Elasticsearch + Logstash + Kibana)** para visualizar logs estructurados.

### Cómo levantar ELK

En la raíz del proyecto hay una carpeta `elk/` con:

- `docker-compose.yml`
- `logstash/pipeline/logstash.conf`

Ejecutar:

```bash
cd elk
docker compose up -d


Servicios disponibles:

- Elasticsearch:  http://localhost:9200
- Kibana:         http://localhost:5601
- Logstash (TCP): puerto 5000

#### Visualización en Kibana

1. Ir a: http://localhost:5601  
2. Abrir la sección **Discover**  
3. Crear un *Data View* con el patrón:

   bank-logs-*

Los logs enviados por la aplicación aparecerán automáticamente allí.

---

## 7. Cómo ejecutar la aplicación

### Requisitos

- Java 21  
- Maven 3.9+  
- Docker Desktop (solo si vas a usar ELK)

### Ejecutar en modo desarrollo

mvn spring-boot:run -DskipTests

### Construir y ejecutar el JAR

mvn clean package -DskipTests
java -jar target/bank-account-service-0.0.1-SNAPSHOT.jar

La API se expone en:

http://localhost:8080/api/v1/accounts

### Levantar ELK (opcional)

cd elk
docker compose up -d

---

## 8. Pruebas automatizadas (opcional)

El proyecto contempla dos tipos de pruebas:

### Tests de dominio (JUnit + Mockito)

Verifican, entre otros:

- Control de CBU duplicado  
- Comportamiento de `create`, `update`, `getById`  
- Lógica de cierre de cuenta (`close()`)

### Tests de integración (SpringBootTest + TestRestTemplate)

Prueban:

- Flujo POST + GET end-to-end  
- Persistencia real sobre H2  
- Códigos HTTP esperados (200, 201, 404, 409)

Ejecución de pruebas:

mvn clean test

---

## 9. Líneas de mejora

Algunas extensiones naturales del proyecto:

- Documentación automática de la API con **OpenAPI / Swagger UI**  
- Autenticación y autorización (JWT + Spring Security)  
- Filtros y paginación en endpoints de consulta  
- Métricas técnicas y de negocio con Micrometer + Prometheus/Grafana  
- Extensión del dominio a:
  - Movimientos bancarios  
  - Clientes  
  - Transferencias  
  - Integración con otros microservicios reutilizando la misma arquitectura hexagonal
