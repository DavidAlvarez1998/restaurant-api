# RestaurantOS — API

REST API para gestión de restaurantes. Construida con arquitectura hexagonal (Ports & Adapters) sobre Spring Boot 4 y Java 21.

---

## Stack

| | |
|-|--|
| Runtime | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Persistencia | PostgreSQL · Spring Data JPA |
| Validación | Spring Validation |
| Tiempo real | WebSocket (STOMP) |
| Utilidades | Lombok · Spring Actuator |
| Build | Maven |

---

## Requisitos previos

- Java 21+
- PostgreSQL corriendo en `localhost:5432`

---

## Configuración

Crear la base de datos:

```sql
CREATE DATABASE restaurant;
```

Credenciales por defecto (`src/main/resources/application.properties`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant
spring.datasource.username=postgres
spring.datasource.password=0000
```

El esquema se genera automáticamente al arrancar (`ddl-auto=update`).

---

## Levantar el servidor

```bash
./mvnw spring-boot:run
```

Disponible en `http://localhost:8080`.

---

## Comandos

```bash
./mvnw clean package                        # compilar JAR
./mvnw test                                 # todos los tests
./mvnw test -Dtest=ClassName#methodName     # test específico
```

---

## Arquitectura

```
domain/
  model/           # Entidades de dominio puras (sin framework)
  port/
    input/         # Contratos de servicio
    output/        # Contratos de repositorio
  exception/       # Excepciones de dominio

application/service/   # Implementa los input ports (@Transactional)

infrastructure/adapter/
  input/rest/      # Controllers, DTOs, mappers REST
  output/persistence/  # Implementaciones JPA de los output ports
```

**Regla:** `domain/` tiene cero dependencias de Spring, JPA o infraestructura. Los servicios dependen solo de puertos; los controllers dependen solo de puertos de servicio.

---

## Endpoints

Base URL: `http://localhost:8080/api`

### Órdenes

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/ordenes` | Listar todas las órdenes activas |
| `POST` | `/ordenes` | Crear orden |
| `GET` | `/ordenes/{id}` | Obtener orden por ID |
| `PATCH` | `/ordenes/{id}` | Actualizar orden |
| `DELETE` | `/ordenes/{id}` | Eliminar orden |
| `PATCH` | `/ordenes/{id}/estado` | Cambiar estado |
| `POST` | `/ordenes/{id}/items` | Agregar item |
| `PATCH` | `/ordenes/{id}/items/{itemId}` | Actualizar item |
| `DELETE` | `/ordenes/{id}/items/{itemId}` | Eliminar item |
| `POST` | `/ordenes/{id}/pagar` | Registrar pago |
| `POST` | `/ordenes/{id}/items/{itemId}/separar` | Separar item en uno nuevo |
| `POST` | `/ordenes/{id}/dividir` | Dividir orden en dos |
| `GET` | `/ordenes/historial` | Historial paginado (`?page=0&size=20`) |

### Mesas

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/mesas` | Listar mesas |
| `POST` | `/mesas` | Crear mesa |
| `GET` | `/mesas/{id}` | Obtener mesa |
| `PATCH` | `/mesas/{id}` | Actualizar mesa |
| `DELETE` | `/mesas/{id}` | Eliminar mesa |

### Productos

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/productos` | Listar productos |
| `POST` | `/productos` | Crear producto |
| `GET` | `/productos/{id}` | Obtener producto |
| `PATCH` | `/productos/{id}` | Actualizar producto |
| `DELETE` | `/productos/{id}` | Eliminar producto |

### Ingredientes

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/ingredientes` | Listar ingredientes |
| `POST` | `/ingredientes` | Crear ingrediente |
| `GET` | `/ingredientes/{id}` | Obtener ingrediente |
| `PATCH` | `/ingredientes/{id}` | Actualizar ingrediente |
| `DELETE` | `/ingredientes/{id}` | Eliminar ingrediente |

### Otros

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/cocina` | Órdenes activas para cocina |
| `GET` | `/reportes` | Reporte de ventas |
| `POST` | `/imagenes/upload` | Subir imagen (max 50 MB) |
| `GET` | `/uploads/{filename}` | Servir imagen |

---

## Modelos de dominio

| Modelo | Campos destacados |
|--------|------------------|
| `Orden` | `tipoOrden` (MESA/PARA_LLEVAR/DOMICILIO), `estado`, `items`, `pagos`, `totalMonto` |
| `OrdenItem` | `producto`, `cantidad`, `precioUnitario`, `ingredientes` |
| `Pago` | `monto`, `propina`, `metodoPago` (EFECTIVO/TARJETA/TRANSFERENCIA) |
| `Mesa` | `numero` |
| `Producto` | `nombre`, `precio`, `tipo`, `imagen` |
| `Ingrediente` | `nombre`, `precio` |

### Ciclo de vida de una orden

```
ABIERTA → EN_PREPARACION → LISTA → ENTREGADA → PAGADA
                                              ↘ CANCELADA
```

El total se calcula como `(precio × cantidad)` por cada item más sus ingredientes adicionales.  
El pago es acumulativo — la orden pasa a `PAGADA` cuando la suma de pagos ≥ total.

### WebSocket

El servidor expone un endpoint STOMP en `/ws`. Úsalo para recibir actualizaciones en tiempo real desde el cliente.
