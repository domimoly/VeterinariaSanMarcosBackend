# Veterinaria San Marcos – Backend (microservicios)

Cada carpeta es un proyecto Spring Boot independiente, con su propia base de datos MySQL.

| Microservicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| `ms-auth` | 8082 | `veterinaria_auth` | Login, registro de tutores, renovación de token (emite el JWT), datos personales del tutor (`GET/PUT /api/auth/perfil`: RUN validado, teléfono, región, comuna y dirección) y gestión de cuentas (`/api/usuarios`, solo ADMINISTRADOR) |
| `ms-servicios` | 8083 | `veterinaria_servicios` | Catálogo de servicios |
| `ms-mascotas` | 8084 | `veterinaria_mascotas` | Mascotas de los tutores (con su día de registro, para contar pacientes nuevos por mes) |
| `ms-citas` | 8085 | `veterinaria_citas` | Agenda de citas (consulta a `ms-mascotas` y `ms-servicios`) |
| `ms-fichas` | 8086 | `veterinaria_fichas` | Fichas clínicas (consulta a `ms-mascotas`) |
| `ms-productos` | 8087 | `veterinaria_productos` | Catálogo y stock de la tienda (lectura pública, edición solo administrador) |
| `ms-pedidos` | 8088 | `veterinaria_pedidos` | Compras: calcula el total con el precio real, descuenta stock en `ms-productos` y guarda el pedido |

Swagger de cada uno: `http://localhost:<puerto>/swagger-ui/index.html`

## Acceso sin token
Según la regla del caso, **solo `POST /api/auth/login` y `POST /api/auth/registro`** (más Swagger) se pueden usar sin token. Todo lo demás —incluido `/api/auth/perfil`, los pedidos y la edición de productos— responde 401 en JSON sin token válido. La única excepción es **la lectura del catálogo de productos** (`GET /api/productos`), pública porque es una tienda online; comprar sí exige sesión. `PUT /api/productos/stock` es interno (clave `X-Clave-Interna`) y Nginx no lo publica.

## Roles
- **TUTOR**: solicita citas, ve sus mascotas, sus citas y la ficha de sus mascotas. Compra en la tienda y ve sus pedidos.
- **RECEPCIONISTA**: ve toda la agenda, confirma y reagenda, registra fichas. Ve todos los pedidos de la tienda, los marca como entregados o los cancela.
- **ADMINISTRADOR**: acceso total. Además de lo de recepción (y de eliminar mascotas), administra el catálogo de la tienda (`/api/productos`: crear, editar precio y stock, ocultar, eliminar) y gestiona cuentas en `ms-auth`:
  `GET/POST /api/usuarios`, `PUT /api/usuarios/{id}` (nombre, rol, contraseña), `PUT /api/usuarios/{id}/desactivar` y `/activar`.
  No puede desactivarse ni cambiarse el rol a sí mismo. Una cuenta desactivada no puede iniciar sesión.
  Cuenta inicial: `admin@veterinariasanmarcos.cl` / `Admin123!`.

## Secretos
No se guardan en el repositorio. Copia `.env.properties.example` como `.env.properties` en cada carpeta
y usa **el mismo valor** de `JWT_SECRET` en todos los servicios.
`ms-productos` y `ms-pedidos` además comparten `CLAVE_INTERNA` (otro valor, distinto del JWT): con ella `ms-pedidos`
descuenta o devuelve stock en `ms-productos` sin que el cliente pueda hacerlo desde el navegador.

## Flujo de la tienda
1. `GET /api/productos` (público, como en toda tienda online) entrega el catálogo; el carrito vive en el navegador y solo guarda código y cantidad.
2. `POST /api/pedidos` (con sesión) recibe los datos de entrega y las líneas. **El precio nunca viaja desde el cliente**:
   `ms-pedidos` lo toma de `ms-productos`, calcula el total y descuenta el stock en una sola transacción (todo o nada, con bloqueo de filas).
3. El pedido queda `PAGADO`. Recepción/administrador lo pasan a `ENTREGADO` (`PUT /api/pedidos/{id}/entregar`) o lo `CANCELADO`
   (`PUT /api/pedidos/{id}/cancelar`, devuelve el stock). Un pedido entregado no se puede cancelar.
4. Cada tutor ve solo sus pedidos; el personal ve todos. El pago es una simulación en el frontend (se aprueba el 70 % de las veces).

## Despliegue con Docker

Todo el sistema (7 microservicios, MariaDB con una base y un usuario por servicio, y el frontend con Nginx) se levanta con Docker Compose:

```bash
cp .env.example .env     # completar secretos; .env no se sube a Git
docker compose up -d --build
```

- Solo el contenedor `web` publica el puerto 80; los microservicios se publican únicamente en `127.0.0.1` (Swagger por túnel SSH).
- `docker/mariadb-init/01-crear-bases.sh` crea las 7 bases y sus usuarios la primera vez.
- El frontend se toma de `FRONTEND_DIR` (por defecto `../VeterinariaSanMarcosReact`), que debe contener el `Dockerfile` del proyecto React.
- Variables de entorno de cada servicio: `*_DB_URL`, `*_DB_USER`, `*_DB_PASSWORD`, `JWT_SECRET`, `APP_CORS_ALLOWED_ORIGIN`, `SERVER_ADDRESS` (opcional).
