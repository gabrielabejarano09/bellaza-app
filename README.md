# Belleza App
## Gabriela Bejarano, Isabela Diaz

---
 
## Módulos
 
| Módulo | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `servicios-service` | 8082 | `servicios_db` | Catálogo de servicios, precios y estado |
| `inventario-service` | 8083 | `inventario_db` | Productos cosméticos y stock |
| `citas-service` | 8081 | `citas_db` | Agenda, confirmación y cancelación de citas |
 
---
 
## Requisitos
 
- Java 17 o superior
- MongoDB corriendo en `localhost:27017`
- Maven (opcional — cada módulo incluye `./mvnw`)
 
### Levantar MongoDB con Docker
 
```bash
docker run -d -p 27017:27017 --name mongo-belleza mongo:6
```
 
---
 
## Cómo correr el proyecto
 
> Los módulos deben levantarse en este orden: **servicios → inventario → citas**,  
> porque `citas-service` consume los endpoints de los otros dos.
 
### Con `./mvnw` (sin instalar Maven)
 
Cada módulo trae su propio Maven. Solo necesitas Java instalado.
 
```bash
# Terminal 1 — servicios (puerto 8082)
cd servicios-service
mkdir -p .mvn/wrapper && cp wrapper/maven-wrapper.properties .mvn/wrapper/
./mvnw spring-boot:run
 
# Terminal 2 — inventario (puerto 8083)
cd inventario-service
mkdir -p .mvn/wrapper && cp wrapper/maven-wrapper.properties .mvn/wrapper/
./mvnw spring-boot:run
 
# Terminal 3 — citas (puerto 8081)
cd citas-service
mkdir -p .mvn/wrapper && cp wrapper/maven-wrapper.properties .mvn/wrapper/
./mvnw spring-boot:run
```
 
> El paso `mkdir -p .mvn/wrapper && cp wrapper/...` solo es necesario la primera vez.
 
### Con `mvn` (Maven instalado globalmente)
 
```bash
# Terminal 1
cd servicios-service && mvn spring-boot:run
 
# Terminal 2
cd inventario-service && mvn spring-boot:run
 
# Terminal 3
cd citas-service && mvn spring-boot:run
```
 
---
