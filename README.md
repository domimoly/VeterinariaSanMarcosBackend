<div align="center">

# Veterinaria San Marcos

### Backend en microservicios (Spring Boot + MariaDB)

Sistema web de la clínica veterinaria: agenda de citas, fichas clínicas, gestión de usuarios y tienda online.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-10.11-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![React](https://img.shields.io/badge/React-Frontend-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)

</div>

**Asignatura:** DSY1104 – Desarrollo FullStack II · Duoc UC · Evaluación Parcial 3 · proyecto en pareja.

## Enlaces

| Qué | Enlace |
|---|---|
| Sitio desplegado en AWS | <http://34.203.108.95> |
| Repositorio del frontend (React) | <https://github.com/domimoly/VeterinariaSanMarcosReact> |
| Repositorio del backend (este) | <https://github.com/domimoly/VeterinariaSanMarcosBackend> |
| Swagger de los 7 microservicios | Ver la tabla de [Documentación Swagger](#documentación-swagger--openapi) |

## 1. Contexto del proyecto

La clínica necesita un sistema donde los **tutores** registren a sus mascotas y soliciten citas, la **recepción** gestione la agenda y las fichas clínicas, y la **administración** controle las cuentas, los reportes y el catálogo de la tienda. El backend se divide en **un microservicio por entidad**, cada uno con su propia base de datos, y todos comparten la autenticación con JWT que emite `ms-auth`. El frontend (React) y el backend se despliegan juntos en una instancia EC2 de AWS con Docker Compose.

## 2. Microservicios implementados

Cada carpeta es un proyecto Spring Boot independiente, con su propia base de datos.

| Microservicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| `ms-auth` | 8082 | `veterinaria_auth` | Login, registro de tutores, renovación de token (emite el JWT), datos personales del tutor (`GET/PUT /api/auth/perfil`: RUN validado, teléfono, región, comuna y dirección) y gestión de cuentas (`/api/usuarios`, solo ADMINISTRADOR) |
| `ms-servicios` | 8083 | `veterinaria_servicios` | Catálogo de servicios |
| `ms-mascotas` | 8084 | `veterinaria_mascotas` | Mascotas de los tutores (con su día de registro, para contar pacientes nuevos por mes) |
| `ms-citas` | 8085 | `veterinaria_citas` | Agenda de citas (consulta a `ms-mascotas` y `ms-servicios`) |
| `ms-fichas` | 8086 | `veterinaria_fichas` | Fichas clínicas (consulta a `ms-mascotas`) |
| `ms-productos` | 8087 | `veterinaria_productos` | Catálogo y stock de la tienda (lectura pública, edición solo administrador) |
| `ms-pedidos` | 8088 | `veterinaria_pedidos` | Compras: calcula el total con el precio real, descuenta stock en `ms-productos` y guarda el pedido |

## 3. Rutas principales (Nginx)

En Docker y AWS el frontend entra por el puerto 80 y Nginx reenvía cada ruta al microservicio correspondiente:

| Ruta pública | Microservicio | Puerto interno |
|---|---|---|
| `/api/auth-service/` | `ms-auth` | 8082 |
| `/api/servicios-service/` | `ms-servicios` | 8083 |
| `/api/mascotas-service/` | `ms-mascotas` | 8084 |
| `/api/citas-service/` | `ms-citas` | 8085 |
| `/api/fichas-service/` | `ms-fichas` | 8086 |
| `/api/productos-service/` | `ms-productos` | 8087 |
| `/api/pedidos-service/` | `ms-pedidos` | 8088 |

## Documentación Swagger / OpenAPI

| Microservicio | AWS | Local |
|---|---|---|
| `ms-auth` | <http://34.203.108.95:8082/swagger-ui/index.html> | <http://localhost:8082/swagger-ui/index.html> |
| `ms-servicios` | <http://34.203.108.95:8083/swagger-ui/index.html> | <http://localhost:8083/swagger-ui/index.html> |
| `ms-mascotas` | <http://34.203.108.95:8084/swagger-ui/index.html> | <http://localhost:8084/swagger-ui/index.html> |
| `ms-citas` | <http://34.203.108.95:8085/swagger-ui/index.html> | <http://localhost:8085/swagger-ui/index.html> |
| `ms-fichas` | <http://34.203.108.95:8086/swagger-ui/index.html> | <http://localhost:8086/swagger-ui/index.html> |
| `ms-productos` | <http://34.203.108.95:8087/swagger-ui/index.html> | <http://localhost:8087/swagger-ui/index.html> |
| `ms-pedidos` | <http://34.203.108.95:8088/swagger-ui/index.html> | <http://localhost:8088/swagger-ui/index.html> |

Para probar endpoints protegidos: en `ms-auth`, `POST /api/auth/login` entrega el token; con el botón **Authorize** se usa en cualquiera de los 7 Swagger (comparten el mismo secreto JWT).

## Acceso sin token

Según la regla del caso, **solo `POST /api/auth/login` y `POST /api/auth/registro`** (más Swagger) se pueden usar sin token. Todo lo demás —incluido `/api/auth/perfil`, los pedidos y la edición de productos— responde 401 en JSON sin token válido. La única excepción es **la lectura del catálogo de productos** (`GET /api/productos`), pública porque es una tienda online; comprar sí exige sesión. `PUT /api/productos/stock` es interno (clave `X-Clave-Interna`) y Nginx no lo publica.

## Roles

- **TUTOR**: solicita citas, ve sus mascotas, sus citas y la ficha de sus mascotas. Compra en la tienda y ve sus pedidos.
- **RECEPCIONISTA**: ve toda la agenda, confirma y reagenda, registra fichas. Ve todos los pedidos de la tienda, los marca como entregados o los cancela.
- **ADMINISTRADOR**: acceso total. Además de lo de recepción (y de eliminar mascotas), administra el catálogo de la tienda (`/api/productos`: crear, editar precio y stock, ocultar, eliminar) y gestiona cuentas en `ms-auth`:
  `GET/POST /api/usuarios`, `PUT /api/usuarios/{id}` (nombre, rol, contraseña), `PUT /api/usuarios/{id}/desactivar` y `/activar`.
  No puede desactivarse ni cambiarse el rol a sí mismo. Una cuenta desactivada no puede iniciar sesión.

Cuentas iniciales de las ejecuciones locales (con Docker o sin él): `admin@veterinariasanmarcos.cl` / `Admin123!` y `recepcion@veterinariasanmarcos.cl` / `Recepcion123!`. En AWS las claves de estas cuentas vienen del archivo `.env` del servidor.

## Secretos

No se guardan en el repositorio. Copia `.env.properties.example` como `.env.properties` en cada carpeta
y usa **el mismo valor** de `JWT_SECRET` en todos los servicios.
`ms-productos` y `ms-pedidos` además comparten `CLAVE_INTERNA` (otro valor, distinto del JWT): con ella `ms-pedidos`
descuenta o devuelve stock en `ms-productos` sin que el cliente pueda hacerlo desde el navegador.
Para ejecutar sin Docker también se pueden entregar como variables de entorno (ver «Ejecución local»).

## Flujo de la tienda

1. `GET /api/productos` (público, como en toda tienda online) entrega el catálogo; el carrito vive en el navegador y solo guarda código y cantidad.
2. `POST /api/pedidos` (con sesión) recibe los datos de entrega y las líneas. **El precio nunca viaja desde el cliente**:
   `ms-pedidos` lo toma de `ms-productos`, calcula el total y descuenta el stock en una sola transacción (todo o nada, con bloqueo de filas).
3. El pedido queda `PAGADO`. Recepción/administrador lo pasan a `ENTREGADO` (`PUT /api/pedidos/{id}/entregar`) o lo `CANCELADO`
   (`PUT /api/pedidos/{id}/cancelar`, devuelve el stock). Un pedido entregado no se puede cancelar.
4. Cada tutor ve solo sus pedidos; el personal ve todos. El pago es una simulación en el frontend (se aprueba el 70 % de las veces).

## Pruebas

- **Backend:** `ms-auth` incluye pruebas (`AuthApplicationTests` y `RunChilenoTest`, que valida el RUN con módulo 11). Se ejecutan con `.\mvnw.cmd test` dentro de la carpeta `ms-auth`.
- **Frontend:** 82 archivos de prueba y 467 pruebas (Vitest + Testing Library) con `npm run test:run`. Ver el repositorio del frontend.
- **Pruebas de integración manuales:** se hacen contra Swagger y contra el sitio desplegado, con las tres cuentas (tutor, recepción y administrador).

## Instrucciones de ejecución

### Ejecución local sin Docker (orden para la demostración)

Requisitos: JDK 17, MySQL o MariaDB encendido en `localhost:3306` (usuario `root` sin clave, como en XAMPP) y Node.js para el frontend.
Todos los comandos son de PowerShell y se ejecutan en la carpeta `VeterinariaSanMarcosBackend`.

**1. Secretos (solo la primera vez en cada computador).** Después de ejecutarlos, **cierra VS Code por completo y ábrelo de nuevo**:

```powershell
setx JWT_SECRET "VmV0ZXJpbmFyaWFTYW5NYXJjb3MtY2xhdmUtbG9jYWwtZGUtcHJ1ZWJhcy0yMDI2LUpXVC1hYmM="
setx CLAVE_INTERNA "clave-interna-local-123"
```

Estos valores son solo para pruebas locales. En AWS se usan los del archivo `.env` (que no se sube a GitHub).
El mismo `JWT_SECRET` sirve para los 7 servicios; `CLAVE_INTERNA` solo la usan `ms-productos` y `ms-pedidos`.

**2. Encender la base de datos.** En XAMPP, presiona **Start** en MySQL. Cada servicio crea su propia base la primera vez.

**3. Levantar los 7 microservicios y comprobar que arrancaron.** Este script los inicia sin ventanas y espera a que cada uno responda:

```powershell
# Ajusta esta ruta si tu JDK 17 está en otra carpeta
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.5"
$svc = [ordered]@{ "ms-auth"=8082; "ms-servicios"=8083; "ms-mascotas"=8084; "ms-citas"=8085; "ms-fichas"=8086; "ms-productos"=8087; "ms-pedidos"=8088 }
$logs = "$env:TEMP\vsm-logs"; New-Item -ItemType Directory -Force $logs | Out-Null
foreach ($s in $svc.Keys) {
  Start-Process -FilePath "$PWD\$s\mvnw.cmd" -ArgumentList "spring-boot:run" -WorkingDirectory "$PWD\$s" -WindowStyle Hidden -RedirectStandardOutput "$logs\$s.log" -RedirectStandardError "$logs\$s.err.log"
}
$pendientes = @($svc.Keys)
for ($i = 0; $i -lt 60 -and $pendientes.Count -gt 0; $i++) {
  Start-Sleep 5
  foreach ($s in @($pendientes)) {
    try {
      Invoke-WebRequest "http://localhost:$($svc[$s])/v3/api-docs" -UseBasicParsing -TimeoutSec 2 | Out-Null
      Write-Host "OK    $s (puerto $($svc[$s]))" -ForegroundColor Green
      $pendientes = @($pendientes | Where-Object { $_ -ne $s })
    } catch {}
  }
}
foreach ($s in $pendientes) { Write-Host "FALLO $s -> revisa $logs\$s.log" -ForegroundColor Red }
```

Cuando todo sale bien, la terminal muestra una línea verde por servicio:

```
OK    ms-auth (puerto 8082)
OK    ms-servicios (puerto 8083)
OK    ms-mascotas (puerto 8084)
OK    ms-citas (puerto 8085)
OK    ms-fichas (puerto 8086)
OK    ms-productos (puerto 8087)
OK    ms-pedidos (puerto 8088)
```

Si alguno sale en rojo, el motivo está al final de su log: `notepad "$env:TEMP\vsm-logs\ms-xxx.log"`.

**4. Probar la API.** Swagger de cada servicio: `http://localhost:<puerto>/swagger-ui/index.html`
(ver la tabla de [Documentación Swagger](#documentación-swagger--openapi)).

**5. Levantar el frontend.** En la carpeta del proyecto React (`VeterinariaSanMarcosReact`):

```powershell
npm install      # solo la primera vez
npm run dev
```

Se abre en `http://localhost:5173` y usa los servicios que se levantaron arriba.

**6. Apagar todo** al terminar:

```powershell
Get-NetTCPConnection -LocalPort 8082,8083,8084,8085,8086,8087,8088 -State Listen -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

### Ejecución con Docker en Windows (laboratorio)

Levanta todo el sistema igual que en AWS: los 7 microservicios, MariaDB y el frontend con Nginx.
Requiere Docker Desktop y la carpeta del frontend (`VeterinariaSanMarcosReact`) **al lado** de esta carpeta.
Todos los comandos son de PowerShell y se ejecutan en la carpeta `VeterinariaSanMarcosBackend`.

**1. Encender Docker Desktop** y esperar a que abajo a la izquierda diga **Engine running**.
Debe estar en modo contenedores de **Linux**. Si aparece el error `no matching manifest for windows(...)`, cambia el modo:

```powershell
& "C:\Program Files\Docker\Docker\DockerCli.exe" -SwitchLinuxEngine
```

**2. Apagar los servicios de la ejecución sin Docker**, porque usan los mismos puertos (8082 al 8088).
Si Apache de XAMPP está usando el puerto 80, también hay que detenerlo.

```powershell
Get-NetTCPConnection -LocalPort 8082,8083,8084,8085,8086,8087,8088 -State Listen -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

**3. Crear el `.env` y traer el frontend.** Los valores son de prueba y el archivo `.env` no se sube a Git:

```powershell
@"
JWT_SECRET=VmV0ZXJpbmFyaWFTYW5NYXJjb3MtY2xhdmUtbG9jYWwtZGUtcHJ1ZWJhcy0yMDI2LUpXVC1hYmM=
APP_CORS_ALLOWED_ORIGIN=http://localhost
CLAVE_INTERNA=clave-interna-local-123
DB_ROOT_PASSWORD=RootLocal123
AUTH_DB_PASSWORD=AuthLocal123
SERVICIOS_DB_PASSWORD=ServLocal123
MASCOTAS_DB_PASSWORD=MascLocal123
CITAS_DB_PASSWORD=CitasLocal123
FICHAS_DB_PASSWORD=FichasLocal123
PRODUCTOS_DB_PASSWORD=ProdLocal123
PEDIDOS_DB_PASSWORD=PedLocal123
RECEPCION_PASSWORD=Recepcion123!
ADMIN_PASSWORD=Admin123!
"@ | Set-Content .env -Encoding ascii

if (-not (Test-Path ..\VeterinariaSanMarcosReact)) { git clone https://github.com/domimoly/VeterinariaSanMarcosReact ..\VeterinariaSanMarcosReact }
```

**4. Construir y levantar todo.** La primera vez demora varios minutos, porque descarga las imágenes y compila los 7 servicios:

```powershell
docker compose up -d --build
```

**5. Comprobar.** Deben aparecer 9 contenedores en estado `running` (la base de datos, además, `healthy`):

```powershell
docker compose ps
```

Luego abre `http://localhost`: ahí está la página de la veterinaria. Los servicios también responden en `http://localhost:<puerto>/swagger-ui/index.html`.

**Comandos útiles**

| Para qué | Comando |
|---|---|
| Ver el estado de los contenedores | `docker compose ps -a` |
| Ver los últimos mensajes de un servicio | `docker compose logs ms-auth --tail 50` |
| Reiniciar un servicio | `docker compose restart ms-auth` |
| Apagar todo y **conservar** los datos | `docker compose down` |
| Apagar todo y **borrar** los datos (base de datos nueva) | `docker compose down -v` |

**Problemas frecuentes**

- `no matching manifest for windows(...)`: Docker está en modo Windows. Usa el comando `-SwitchLinuxEngine` del paso 1.
- Los servicios se reinician y los logs dicen `Access denied for user 'auth_app'...`, o en la base solo existen `mysql`, `sys` y similares: el script `docker/mariadb-init/01-crear-bases.sh` no se ejecutó.
  En el log de la base aparece `cannot execute: required file not found`, porque Windows guardó el archivo con saltos de línea `CRLF`.
  Se arregla convirtiéndolo a `LF` y creando la base de nuevo (el script solo corre al crear el volumen):

```powershell
$f = "docker\mariadb-init\01-crear-bases.sh"
[IO.File]::WriteAllText("$PWD\$f", ((Get-Content $f -Raw) -replace "`r`n","`n"), (New-Object Text.UTF8Encoding $false))
docker compose down -v
docker compose up -d
```

  El archivo `.gitattributes` del repositorio fuerza `LF` en los `.sh`, para que esto no se repita al clonar en Windows.
- `port is already allocated` o `address already in use` en el puerto 80: apaga Apache de XAMPP. En los puertos 8082 al 8088: ejecuta el comando del paso 2.

### Despliegue con Docker Compose

Todo el sistema (7 microservicios, MariaDB con una base y un usuario por servicio, y el frontend con Nginx) se levanta con Docker Compose:

```bash
cp .env.example .env     # completar secretos; .env no se sube a Git
docker compose up -d --build
```

- El contenedor `web` publica el puerto 80. Los microservicios (8082–8088) publican su Swagger según `SWAGGER_BIND` en `.env`: `127.0.0.1` (por defecto) solo desde la propia máquina; `0.0.0.0` para abrirlos a internet, como en el servidor de AWS.
- La base de datos es `mariadb:10.11`. Con MariaDB 11.x el conector MySQL con Hibernate no inicia.
- `docker/mariadb-init/01-crear-bases.sh` crea las 7 bases y sus usuarios la primera vez.
- El frontend se toma de `FRONTEND_DIR` (por defecto `../VeterinariaSanMarcosReact`), que debe contener el `Dockerfile` del proyecto React.
- Variables de entorno de cada servicio: `*_DB_URL`, `*_DB_USER`, `*_DB_PASSWORD`, `JWT_SECRET`, `APP_CORS_ALLOWED_ORIGIN`, `SERVER_ADDRESS` (opcional).

## Despliegue en AWS

El sistema está publicado en una instancia **EC2** (t3.large, Ubuntu, 30 GiB) con **Elastic IP**, ejecutando los 9 contenedores del `docker-compose.yml` (MariaDB, 7 microservicios y el frontend con Nginx).

- **Sitio:** <http://34.203.108.95>
- **Swagger:** `http://34.203.108.95:<puerto>/swagger-ui/index.html`, con los puertos de la tabla de [Documentación Swagger](#documentación-swagger--openapi).
- **Grupo de seguridad:** 22 (solo la IP del estudiante), 80 y 8082–8088. La base de datos (3306) no se publica.
- **Secretos:** en el archivo `.env` del servidor, que no se sube a GitHub (`SWAGGER_BIND=0.0.0.0` publica los Swagger).
- **Actualizar tras un cambio en GitHub:** `git pull && docker compose up -d --build` (o el nombre de un solo servicio, por ejemplo `ms-productos`).

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 17+ (imágenes Docker con Java 21), Spring Boot 4.1, Spring Web, Spring Data JPA, Spring Security, Bean Validation, Lombok |
| Seguridad | JWT (JJWT 0.12.6), roles TUTOR / RECEPCIONISTA / ADMINISTRADOR |
| Documentación | springdoc-openapi (Swagger UI) |
| Base de datos | MariaDB 10.11 (una base y un usuario por microservicio); MySQL/MariaDB de XAMPP en la ejecución local |
| Frontend | React + Vite, servido por Nginx |
| Despliegue | Docker, Docker Compose, AWS EC2 |
| Pruebas | JUnit (backend), Vitest + Testing Library (frontend) |