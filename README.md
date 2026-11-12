# Veterinaria San Marcos – Backend (microservicios)

Cada carpeta es un proyecto Spring Boot independiente, con su propia base de datos MySQL.

| Microservicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| `auth` | 8082 | `veterinaria_auth` | Login, registro de tutores, renovación de token (emite el JWT) |
| `servicios` | 8083 | `veterinaria_servicios` | Catálogo de servicios |
| `mascotas` | 8084 | `veterinaria_mascotas` | Mascotas de los tutores |
| `citas` | 8085 | `veterinaria_citas` | Agenda de citas (consulta a `mascotas` y `servicios`) |
| `fichas` | 8086 | `veterinaria_fichas` | Fichas clínicas (consulta a `mascotas`) |

Swagger de cada uno: `http://localhost:<puerto>/swagger-ui/index.html`

## Secreto JWT
No se guarda en el repositorio. Copia `.env.properties.example` como `.env.properties` en cada carpeta
y usa **el mismo valor** de `JWT_SECRET` en todos los servicios.
