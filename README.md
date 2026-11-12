# Pastelería Mil Sabores · Backend

API REST de la tienda en línea Pastelería Mil Sabores (DSY1104 Desarrollo Full Stack II, Evaluación Parcial 3).

- **Spring Boot 4.1** · Java 21 · Maven
- **MySQL** con Spring Data JPA (Hibernate)
- **Spring Security + JWT** con roles Administrador, Vendedor y Cliente
- **Swagger** (springdoc) en `http://localhost:8080/swagger-ui.html`
- Frontend: https://github.com/solange1988/pasteleria-mil-sabores-react

## Cómo ejecutarlo

1. Tener MySQL corriendo en `localhost:3306` con el usuario `root` / `root123`
   (o cambiar usuario y contraseña en `src/main/resources/application.properties`).
   La base `pasteleria_mil_sabores` se crea sola.
2. Abrir la carpeta en IntelliJ IDEA y ejecutar `BackendApplication`.
3. Abrir `http://localhost:8080/swagger-ui.html`.

La primera vez se cargan los 16 productos y 3 usuarios de prueba:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@duoc.cl | admin123 |
| Vendedor | vendedor@duoc.cl | vende123 |
| Cliente | cliente@gmail.com | cliente123 |

## Estructura (capas)

```
cl.milsabores.backend
├── model        Entidades JPA: Usuario, Categoria, Producto, Boleta, DetalleBoleta, Rol
├── repository   Interfaces JpaRepository (acceso a MySQL)
├── service      Lógica de negocio: compras, descuentos, stock, usuarios, autenticación
├── controller   Endpoints REST /api/v1/...
├── dto          Datos que entran y salen de la API (nunca se expone la contraseña)
├── security     JWT (crear/verificar tokens), filtro, reglas por rol, CORS, BCrypt
├── exception    Errores con su código HTTP (400, 401, 403, 404)
└── config       Swagger y datos iniciales
```

## Modelo de datos

`USUARIO 0..1 ── * BOLETA 1 ── * DETALLE_BOLETA * ── 1 PRODUCTO * ── 1 CATEGORIA`

La relación usuario-boleta es opcional: un visitante sin cuenta también puede comprar.

## Endpoints

| Método | Ruta | Acceso |
|---|---|---|
| POST | /api/v1/auth/registro | Público |
| POST | /api/v1/auth/login | Público |
| POST | /api/v1/auth/renovar | Público (con token de renovación) |
| GET | /api/v1/productos, /api/v1/productos/{id}, /api/v1/productos/ofertas | Público |
| GET | /api/v1/productos/criticos | Administrador, Vendedor |
| POST, PUT, DELETE | /api/v1/productos/... | Administrador |
| GET | /api/v1/categorias, /api/v1/categorias/{id} | Público |
| POST, PUT, DELETE | /api/v1/categorias/... | Administrador |
| POST | /api/v1/boletas | Público (si hay token, aplica descuentos del usuario) |
| GET | /api/v1/boletas/mias | Con sesión |
| GET | /api/v1/boletas, /api/v1/boletas/{numero} | Administrador, Vendedor |
| PATCH | /api/v1/boletas/{numero}/estado | Administrador |
| DELETE | /api/v1/boletas/{numero} | Administrador |
| GET, PUT | /api/v1/usuarios/perfil | Con sesión |
| GET, POST, PUT, DELETE | /api/v1/usuarios/... | Administrador |

## Pruebas

`BoletaServiceTest`, `DescuentoServiceTest` y `JwtServiceTest` (JUnit 5 + Mockito).
En IntelliJ: ventana Maven → Lifecycle → test.
