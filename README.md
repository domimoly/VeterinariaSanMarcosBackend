# Veterinaria San Marcos – Backend (microservicios)

Cada carpeta es un proyecto Spring Boot independiente, con su propia base de datos MySQL.

| Microservicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| `ms-auth` | 8082 | `veterinaria_auth` | Login, registro de tutores, renovación de token (emite el JWT) y gestión de cuentas (`/api/usuarios`, solo ADMINISTRADOR) |
| `ms-servicios` | 8083 | `veterinaria_servicios` | Catálogo de servicios |
| `ms-mascotas` | 8084 | `veterinaria_mascotas` | Mascotas de los tutores |
| `ms-citas` | 8085 | `veterinaria_citas` | Agenda de citas (consulta a `ms-mascotas` y `ms-servicios`) |
| `ms-fichas` | 8086 | `veterinaria_fichas` | Fichas clínicas (consulta a `ms-mascotas`) |

Swagger de cada uno: `http://localhost:<puerto>/swagger-ui/index.html`

## Roles
- **TUTOR**: solicita citas, ve sus mascotas, sus citas y la ficha de sus mascotas.
- **RECEPCIONISTA**: ve toda la agenda, confirma y reagenda, registra fichas.
- **ADMINISTRADOR**: acceso total. Además de lo de recepción (y de eliminar mascotas), gestiona cuentas en `ms-auth`:
  `GET/POST /api/usuarios`, `PUT /api/usuarios/{id}` (nombre, rol, contraseña), `PUT /api/usuarios/{id}/desactivar` y `/activar`.
  No puede desactivarse ni cambiarse el rol a sí mismo. Una cuenta desactivada no puede iniciar sesión.
  Cuenta inicial: `admin@veterinariasanmarcos.cl` / `Admin123!`.

## Secreto JWT
No se guarda en el repositorio. Copia `.env.properties.example` como `.env.properties` en cada carpeta
y usa **el mismo valor** de `JWT_SECRET` en todos los servicios.
