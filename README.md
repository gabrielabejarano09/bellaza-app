# Belleza App
## Gabriela Bejarano, Isabela Diaz

---
 
# Salón de Belleza — Arquitectura Hexagonal

Sistema backend para la gestión de un salón de belleza implementado con **arquitectura hexagonal (Ports & Adapters)**, **Spring Boot 3.2** y **MongoDB**. El sistema está compuesto por tres microservicios independientes que se comunican entre sí vía REST.

---

## Enlace video pruebas en postman

https://youtu.be/d0I5rdzz9eI

## Módulos

| Módulo | Puerto | Base de datos | Descripción |
|---|---|---|---|
| `servicios-service` | **8082** | `servicios_db` | Catálogo de servicios, precios y estado |
| `inventario-service` | **8083** | `inventario_db` | Productos cosméticos y stock |
| `citas-service` | **8081** | `citas_db` | Agenda, confirmación y cancelación de citas |

---

## Arquitectura Hexagonal

Cada módulo implementa arquitectura hexagonal con tres capas bien definidas y separadas por interfaces (puertos).

```
┌──────────────────────────────────────────────────────────────────┐
│                   INFRAESTRUCTURA (adaptadores)                   │
│                                                                    │
│  Adaptadores Primarios (entrada)   Adaptadores Secundarios(salida)│
│                                                                    │
│  ┌─────────────────────┐          ┌──────────────────────────┐   │
│  │   CitaController    │          │  CitaRepositorioAdapter  │   │
│  │   @RestController   │          │  implements Puerto Salida│   │
│  │                     │          │  → CitaMongoRepository   │   │
│  │  GlobalException    │          │  → CitaDocument          │   │
│  │  Handler            │          ├──────────────────────────┤   │
│  └──────────┬──────────┘          │  ServicioClienteAdapter  │   │
│             │ inyecta interfaz    │  → RestTemplate → :8082  │   │
│             │                     ├──────────────────────────┤   │
│  ┌──────────▼──────────┐          │  InventarioClienteAdapter│   │
│  │   Puertos Entrada   │          │  → RestTemplate → :8083  │   │
│  │  AgendarCitaEntrada │          └──────────────┬───────────┘   │
│  │  ConfirmarCita...   │                         │ implementa    │
│  │  CancelarCita...    │          ┌──────────────▼───────────┐   │
│  │  ConsultarCita...   │          │     Puertos Salida        │   │
│  └──────────┬──────────┘          │  CitaRepositorioPuerto   │   │
│             │                     │  ServicioClientePuerto   │   │
│  ┌──────────▼──────────────────────▼──────────────────────┐  │   │
│  │               APLICACION (casos de uso)                 │  │   │
│  │                                                         │  │   │
│  │  AgendarCitaUseCase   — verifica servicio activo,      │  │   │
│  │                         conflicto horario, crea Cita   │  │   │
│  │  ConfirmarCitaUseCase — verifica stock,                │  │   │
│  │                         llama cita.confirmar()         │  │   │
│  │  CancelarCitaUseCase  — llama cita.cancelar()          │  │   │
│  │  ConsultarCitaUseCase — busca por ID                   │  │   │
│  │                                                         │  │   │
│  │  ┌───────────────────────────────────────────────────┐ │  │   │
│  │  │               DOMINIO (nucleo puro)               │ │  │   │
│  │  │                                                   │ │  │   │
│  │  │  Entidad Cita — Java puro, sin Spring ni Mongo    │ │  │   │
│  │  │    confirmar() — valida que no este cancelada     │ │  │   │
│  │  │    cancelar()  — valida estado                    │ │  │   │
│  │  │    constructor — valida fecha futura              │ │  │   │
│  │  │                                                   │ │  │   │
│  │  │  Excepciones de dominio:                          │ │  │   │
│  │  │    CitaNotFoundException                          │ │  │   │
│  │  │    CitaConflictoException                         │ │  │   │
│  │  │    CitaEstadoInvalidoException                    │ │  │   │
│  │  │    FechaCitaInvalidaException                     │ │  │   │
│  │  │    ReglaNegocioCitaException                      │ │  │   │
│  │  │    ServicioInactivoException                      │ │  │   │
│  │  └───────────────────────────────────────────────────┘ │  │   │
│  └─────────────────────────────────────────────────────────┘  │   │
└──────────────────────────────────────────────────────────────────┘
```

### Estructura de carpetas (citas-service)

```
citas-service/src/main/java/com/belleza_app/citas/
├── domain/
│   ├── entities/           ← Cita.java (Java puro, sin @Document ni @Id)
│   ├── exceptions/         ← Excepciones de negocio puras
│   └── ports/
│       ├── in/             ← Puertos de entrada (interfaces para el controller)
│       │   ├── AgendarCitaEntrada.java
│       │   ├── ConfirmarCitaEntrada.java
│       │   ├── CancelarCitaEntrada.java
│       │   └── ConsultarCitaEntrada.java
│       └── out/            ← Puertos de salida (interfaces para infraestructura)
│           ├── CitaRepositorioPuerto.java
│           ├── ServicioClientePuerto.java
│           └── InventarioClientePuerto.java
├── application/
│   └── usecase/            ← Casos de uso (POJOs puros, sin @Service)
│       ├── AgendarCitaUseCase.java
│       ├── ConfirmarCitaUseCase.java
│       ├── CancelarCitaUseCase.java
│       └── ConsultarCitaUseCase.java
└── infraestructure/
    ├── adaptors/
    │   ├── primary/rest/   ← Adaptadores primarios (entrada)
    │   │   ├── CitaController.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── dto/CitaRequest.java, CitaResponse.java
    │   └── secondary/      ← Adaptadores secundarios (salida)
    │       ├── persistence/
    │       │   ├── CitaDocument.java        (aqui si va @Document)
    │       │   ├── CitaMongoRepository.java (Spring Data)
    │       │   └── CitaRepositorioAdapter.java
    │       └── client/
    │           ├── ServicioClienteAdapter.java
    │           └── InventarioClienteAdapter.java
    └── config/
        ├── BeanConfig.java         ← Conecta puertos con casos de uso
        └── RestTemplateConfig.java
```

### Comunicacion entre modulos

```
Cliente HTTP
     |
     v
citas-service :8081
     |
     |-- Al AGENDAR una cita:
     |   └──► GET servicios-service:8082/api/servicios/{id}/activo
     |         (verifica que el servicio este ACTIVO antes de crear la cita)
     |
     └── Al CONFIRMAR una cita:
         └──► GET inventario-service:8083/api/productos/{id}/disponible?cantidad=1
               (verifica que haya stock del producto antes de confirmar)
```

### Inyeccion de dependencias (BeanConfig)

Los casos de uso son POJOs puros sin @Service. Spring los crea en BeanConfig:

```java
@Bean
public AgendarCitaEntrada agendarCitaUseCase(
        CitaRepositorioPuerto citaRepo,       // Spring inyecta el @Component adapter
        ServicioClientePuerto servicioCliente) {
    return new AgendarCitaUseCase(citaRepo, servicioCliente);
}
```

El controller recibe interfaces (puertos de entrada), no implementaciones concretas:

```java
public CitaController(AgendarCitaEntrada agendarCita,    // interfaz, no clase concreta
                      ConfirmarCitaEntrada confirmarCita,
                      CancelarCitaEntrada cancelarCita,
                      ConsultarCitaEntrada consultarCita) { ... }
```

---

## Requisitos

- Java 17 o superior
- MongoDB corriendo en `localhost:27017`
- Maven instalado, o usar `./mvnw` incluido en cada modulo

### Levantar MongoDB con Docker

```bash
docker run -d -p 27017:27017 --name mongo-belleza mongo:6
```

---

## Como correr el proyecto

> El orden es importante: servicios -> inventario -> citas
> porque citas-service llama a los otros dos.

### Con ./mvnw (sin instalar Maven)

```bash
# Primera vez — reparar el wrapper en cada modulo
mkdir -p .mvn/wrapper && cp wrapper/maven-wrapper.properties .mvn/wrapper/

# Terminal 1 — servicios (puerto 8082)
cd servicios-service && ./mvnw spring-boot:run

# Terminal 2 — inventario (puerto 8083)
cd inventario-service && ./mvnw spring-boot:run

# Terminal 3 — citas (puerto 8081)
cd citas-service && ./mvnw spring-boot:run
```

### Con Maven instalado

```bash
cd servicios-service && mvn spring-boot:run
cd inventario-service && mvn spring-boot:run
cd citas-service && mvn spring-boot:run
```

---

## Correr los tests

```bash
# Todos los tests de un modulo
./mvnw test

# Un test especifico
./mvnw test -Dtest=AgendarCitaUseCaseTest

# Con output detallado en consola
./mvnw test -Dsurefire.useFile=false
```

Reportes en `target/surefire-reports/`.

Tipos de tests por modulo:
- `*UseCaseTest.java` — unit tests con Mockito (mockean los puertos/interfaces)
- `*ControllerIntegrationTest.java` — integration tests con MongoDB embebido (Flapdoodle)

---

## Endpoints

### servicios-service — localhost:8082

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | /api/servicios | Crear servicio |
| GET | /api/servicios/{id} | Buscar por ID |
| GET | /api/servicios/activos | Listar servicios activos |
| GET | /api/servicios/{id}/activo | Consultar si esta activo (usado por citas) |
| PATCH | /api/servicios/{id}/activar | Activar servicio |
| PATCH | /api/servicios/{id}/desactivar | Desactivar servicio |

### inventario-service — localhost:8083

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | /api/productos | Registrar producto |
| GET | /api/productos/{id} | Buscar por ID |
| GET | /api/productos/{id}/stock | Consultar stock |
| PATCH | /api/productos/{id}/agregar-stock | Agregar stock |
| PATCH | /api/productos/{id}/descontar-stock | Descontar stock |
| GET | /api/productos/{id}/disponible?cantidad=N | Verificar disponibilidad (usado por citas) |

### citas-service — localhost:8081

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | /api/citas | Agendar cita |
| GET | /api/citas/{id} | Buscar por ID |
| PATCH | /api/citas/{id}/confirmar | Confirmar cita |
| PATCH | /api/citas/{id}/cancelar | Cancelar cita |

---

## Manejo de excepciones

Todos los modulos retornan errores en formato JSON estandar via @RestControllerAdvice:

```json
{
  "timestamp": "2026-06-15T10:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "mensaje": "El cliente ya tiene cita en ese horario"
}
```

| Codigo | Cuando ocurre |
|---|---|
| 400 | Campos requeridos faltantes o invalidos (@NotBlank, @Future) |
| 404 | Recurso no encontrado |
| 409 | Conflicto de horario en cita |
| 422 | Regla de negocio violada |
| 500 | Error interno no esperado |

---

## Pruebas con Postman

### Paso 0 — Configurar entorno

En Postman: New Environment → "Belleza Local"

| Variable | Valor inicial |
|---|---|
| servicios_url | http://localhost:8082 |
| inventario_url | http://localhost:8083 |
| citas_url | http://localhost:8081 |
| servicioId | (vacio — se llena automaticamente) |
| productoId | (vacio — se llena automaticamente) |
| citaId | (vacio — se llena automaticamente) |

---

### Paso 1 — Crear un servicio

**POST** `{{servicios_url}}/api/servicios`

Header: `Content-Type: application/json`

Body:
```json
{
  "nombre": "Corte de cabello",
  "descripcion": "Corte y lavado profesional",
  "precio": 35000,
  "duracionMinutos": 60
}
```

Respuesta esperada: **201 Created**
```json
{
  "id": "68012abc...",
  "nombre": "Corte de cabello",
  "estado": "ACTIVO"
}
```

Pestaña Tests — pegar esto para guardar el ID automaticamente:
```javascript
pm.environment.set("servicioId", pm.response.json().id);
pm.test("Servicio creado", () => pm.response.to.have.status(201));
pm.test("Estado es ACTIVO", () => pm.expect(pm.response.json().estado).to.eql("ACTIVO"));
```

---

### Paso 2 — Registrar un producto en inventario

**POST** `{{inventario_url}}/api/productos`

Header: `Content-Type: application/json`

Body:
```json
{
  "nombre": "Tinte rubio",
  "descripcion": "Tinte profesional color rubio",
  "stock": 20
}
```

Respuesta esperada: **201 Created**
```json
{
  "id": "68012xyz...",
  "nombre": "Tinte rubio",
  "stock": 20
}
```

Pestaña Tests:
```javascript
pm.environment.set("productoId", pm.response.json().id);
pm.test("Producto creado", () => pm.response.to.have.status(201));
pm.test("Stock es 20", () => pm.expect(pm.response.json().stock).to.eql(20));
```

---

### Paso 3 — Agendar una cita

**POST** `{{citas_url}}/api/citas`

Header: `Content-Type: application/json`

Body (ajustar la fecha a una futura):
```json
{
  "clienteNombre": "Gabriela Torres",
  "servicioId": "{{servicioId}}",
  "productoId": "{{productoId}}",
  "fechaHora": "2026-12-15T10:00:00"
}
```

Respuesta esperada: **201 Created**
```json
{
  "id": "68012cita...",
  "clienteNombre": "Gabriela Torres",
  "estado": "PENDIENTE"
}
```

Pestaña Tests:
```javascript
pm.environment.set("citaId", pm.response.json().id);
pm.test("Cita creada", () => pm.response.to.have.status(201));
pm.test("Estado PENDIENTE", () => pm.expect(pm.response.json().estado).to.eql("PENDIENTE"));
```

---

### Paso 4 — Confirmar la cita

**PATCH** `{{citas_url}}/api/citas/{{citaId}}/confirmar`

Sin body.

Respuesta esperada: **200 OK**
```json
{
  "id": "68012cita...",
  "clienteNombre": "Gabriela Torres",
  "estado": "CONFIRMADA"
}
```

Pestaña Tests:
```javascript
pm.test("Cita confirmada", () => pm.response.to.have.status(200));
pm.test("Estado CONFIRMADA", () => pm.expect(pm.response.json().estado).to.eql("CONFIRMADA"));
```

---

### Paso 5 — Cancelar una cita

Primero crea una segunda cita (repite el paso 3 con diferente hora) y guarda el ID. Luego:

**PATCH** `{{citas_url}}/api/citas/{{citaId}}/cancelar`

Sin body.

Respuesta esperada: **200 OK**
```json
{
  "estado": "CANCELADA"
}
```

---

### Paso 6 — Consultar una cita por ID

**GET** `{{citas_url}}/api/citas/{{citaId}}`

Respuesta esperada: **200 OK** con todos los datos de la cita.

---

### Pruebas de errores — reglas de negocio

#### Error 409 — conflicto de horario

Agendas dos citas para el mismo cliente en el mismo horario exacto.

**POST** `{{citas_url}}/api/citas` dos veces con el mismo body del paso 3.

Segunda respuesta esperada: **409 Conflict**
```json
{
  "status": 409,
  "error": "Conflict",
  "mensaje": "El cliente ya tiene cita en ese horario"
}
```

---

#### Error 422 — servicio inactivo

1. Desactivar el servicio:
   **PATCH** `{{servicios_url}}/api/servicios/{{servicioId}}/desactivar`

2. Intentar agendar cita con ese servicioId:
   **POST** `{{citas_url}}/api/citas` con el mismo body del paso 3.

Respuesta esperada: **422 Unprocessable Entity**
```json
{
  "status": 422,
  "mensaje": "No se puede confirmar la cita porque el servicio con id ... esta INACTIVO"
}
```

3. Vuelve a activar el servicio para los proximos tests:
   **PATCH** `{{servicios_url}}/api/servicios/{{servicioId}}/activar`

---

#### Error 422 — stock insuficiente al confirmar

1. Descontar todo el stock:
   **PATCH** `{{inventario_url}}/api/productos/{{productoId}}/descontar-stock`
   Body: `{"cantidad": 20}`

2. Crear una nueva cita (paso 3 con hora diferente).

3. Intentar confirmar esa cita:
   **PATCH** `{{citas_url}}/api/citas/{{citaId}}/confirmar`

Respuesta esperada: **422 Unprocessable Entity**
```json
{
  "status": 422,
  "mensaje": "No hay stock suficiente"
}
```

---

#### Error 404 — cita no encontrada

**GET** `{{citas_url}}/api/citas/id-que-no-existe`

Respuesta esperada: **404 Not Found**
```json
{
  "status": 404,
  "mensaje": "No se encontro la cita con id: id-que-no-existe"
}
```

---

#### Error 400 — campos obligatorios faltantes

**POST** `{{citas_url}}/api/citas`

Body:
```json
{}
```

Respuesta esperada: **400 Bad Request**
```json
{
  "status": 400,
  "mensaje": "Errores de validacion: clienteNombre: El nombre del cliente es obligatorio; ..."
}
```

---

#### Error 400 — fecha en el pasado

**POST** `{{citas_url}}/api/citas`

Body:
```json
{
  "clienteNombre": "Gabriela Torres",
  "servicioId": "{{servicioId}}",
  "productoId": "{{productoId}}",
  "fechaHora": "2020-01-01T10:00:00"
}
```

Respuesta esperada: **400 Bad Request** (falla la validacion @Future del DTO)

---

#### Error 422 — precio invalido en servicio

**POST** `{{servicios_url}}/api/servicios`

Body:
```json
{
  "nombre": "Test invalido",
  "precio": -100,
  "duracionMinutos": 60
}
```

Respuesta esperada: **422 Unprocessable Entity**
```json
{
  "status": 422,
  "mensaje": "El precio del servicio debe ser mayor a cero"
}
```

---

#### Error 422 — duracion fuera de rango

**POST** `{{servicios_url}}/api/servicios`

Body:
```json
{
  "nombre": "Test invalido",
  "precio": 50000,
  "duracionMinutos": 600
}
```

Respuesta esperada: **422 Unprocessable Entity**
```json
{
  "status": 422,
  "mensaje": "La duracion debe estar entre 15 y 480 minutos"
}
```

---

#### Error 422 — descontar mas stock del disponible

**PATCH** `{{inventario_url}}/api/productos/{{productoId}}/descontar-stock`

Body:
```json
{
  "cantidad": 9999
}
```

Respuesta esperada: **422 Unprocessable Entity**
```json
{
  "status": 422,
  "mensaje": "La cantidad a descontar no puede superar el stock disponible..."
}
```

---

### Orden recomendado de la coleccion Postman

```
Belleza App/
├── Setup/
│   ├── 1. Crear servicio           POST /api/servicios          → 201, guarda servicioId
│   └── 2. Registrar producto       POST /api/productos           → 201, guarda productoId
├── Flujo feliz/
│   ├── 3. Agendar cita             POST /api/citas               → 201, guarda citaId
│   ├── 4. Confirmar cita           PATCH /api/citas/{id}/confirmar → 200 CONFIRMADA
│   ├── 5. Consultar cita           GET /api/citas/{id}           → 200
│   └── 6. Cancelar cita (nueva)   PATCH /api/citas/{id}/cancelar  → 200 CANCELADA
├── Errores de negocio/
│   ├── 7. Conflicto de horario    POST /api/citas (duplicada)   → 409
│   ├── 8. Servicio inactivo       desactivar + agendar          → 422
│   ├── 9. Stock insuficiente      descontar todo + confirmar    → 422
│   └── 10. Cita no encontrada     GET /api/citas/fakeId         → 404
└── Validaciones/
    ├── 11. Body vacio             POST /api/citas {}             → 400
    ├── 12. Fecha pasada           POST /api/citas fecha 2020     → 400
    ├── 13. Precio invalido        POST /api/servicios precio -1  → 422
    └── 14. Stock negativo         PATCH descontar 9999           → 422
```

---

## Reglas de negocio implementadas

### Modulo Citas
- La fecha de la cita debe ser posterior a la fecha actual (validado en el DTO con @Future y en la entidad con validarFechaFutura)
- Un cliente no puede tener dos citas exactamente en el mismo horario (CitaConflictoException)
- Una cita cancelada no puede confirmarse (CitaEstadoInvalidoException)
- Al agendar se verifica que el servicio este activo llamando a servicios-service por REST
- Al confirmar se verifica que haya stock del producto llamando a inventario-service por REST

### Modulo Servicios
- El precio debe ser mayor a cero (ReglaNegocioServicioException)
- La duracion debe estar entre 15 y 480 minutos (ReglaNegocioServicioException)
- Todo servicio nuevo nace con estado ACTIVO

### Modulo Inventario
- El stock inicial no puede ser negativo (StockInsuficienteException)
- La cantidad a descontar no puede superar el stock disponible (StockInsuficienteException)
- La cantidad a agregar debe ser mayor a cero (StockInsuficienteException)
