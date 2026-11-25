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
Para ejecutar sin Docker también se pueden entregar como variables de entorno (ver «Ejecución local»).

## Ejecución local sin Docker (orden para la demostración)

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

**4. Probar la API.** Swagger de cada servicio: `http://localhost:<puerto>/swagger-ui/index.html`.
En `ms-auth` (8082), `POST /api/auth/login` entrega el token; con el botón **Authorize** se usa en los demás servicios.
Cuentas iniciales: `admin@veterinariasanmarcos.cl` / `Admin123!` y `recepcion@veterinariasanmarcos.cl` / `Recepcion123!`.

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

Guía completa para AWS EC2: [DESPLIEGUE_AWS.md](DESPLIEGUE_AWS.md).
