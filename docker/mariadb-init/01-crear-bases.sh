#!/bin/bash
# Se ejecuta una sola vez, al crear el volumen de MariaDB.
# Crea UNA base y UN usuario por microservicio (7 en total); cada usuario solo ve su propia base.
set -e

crear() {
  local base="$1" usuario="$2" clave="$3"
  mariadb -uroot -p"${MARIADB_ROOT_PASSWORD}" <<SQL
CREATE DATABASE IF NOT EXISTS \`${base}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${usuario}'@'%' IDENTIFIED BY '${clave}';
GRANT ALL PRIVILEGES ON \`${base}\`.* TO '${usuario}'@'%';
FLUSH PRIVILEGES;
SQL
}

crear veterinaria_auth      auth_app      "${AUTH_DB_PASSWORD}"
crear veterinaria_servicios servicios_app "${SERVICIOS_DB_PASSWORD}"
crear veterinaria_mascotas  mascotas_app  "${MASCOTAS_DB_PASSWORD}"
crear veterinaria_citas     citas_app     "${CITAS_DB_PASSWORD}"
crear veterinaria_fichas    fichas_app    "${FICHAS_DB_PASSWORD}"
crear veterinaria_productos  productos_app  "${PRODUCTOS_DB_PASSWORD}"
crear veterinaria_pedidos    pedidos_app    "${PEDIDOS_DB_PASSWORD}"
