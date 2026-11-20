#!/usr/bin/env bash
# OPCIONAL: copia los datos actuales de Supabase a la base del contenedor (una sola vez,
# ANTES de levantar los backends la primera vez, para no perder usuarios ni progreso).
# Uso: SUPABASE_URL='postgresql://usuario:clave@host:5432/postgres' bash scripts/migrar-desde-supabase.sh
set -euo pipefail
cd "$(dirname "$0")/.."
set -a; . ./.env; set +a
: "${SUPABASE_URL:?Pon la cadena de conexión de Supabase en SUPABASE_URL (no la guardes en el repo)}"
docker compose up -d db
echo "Esperando a PostgreSQL..."; until docker compose exec -T db pg_isready -U "${POSTGRES_USER:-saesu}" >/dev/null; do sleep 2; done
# Solo el esquema public (tablas de SAESU y el historial de Flyway)
docker compose exec -T db pg_dump "$SUPABASE_URL" --schema=public --no-owner --no-privileges -Fc > /tmp/supabase.dump
docker compose exec -T db pg_restore -U "${POSTGRES_USER:-saesu}" -d "${POSTGRES_DB:-saesu}" --no-owner --no-privileges < /tmp/supabase.dump
rm -f /tmp/supabase.dump
echo "Listo. Ahora: docker compose up -d"
