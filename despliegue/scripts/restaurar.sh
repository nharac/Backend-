#!/usr/bin/env bash
# Paso 17: restaura un respaldo en una base de prueba NUEVA (no toca la base real).
# Uso: bash scripts/restaurar.sh respaldos/saesu-AAAAMMDD-HHMM.dump [nombre_base_prueba]
set -euo pipefail
cd "$(dirname "$0")/.."
set -a; . ./.env; set +a
F="${1:?Uso: restaurar.sh ARCHIVO.dump [base_prueba]}"
B="${2:-saesu_restaurada}"
U="${POSTGRES_USER:-saesu}"
docker compose exec -T db psql -U "$U" -d postgres -c "DROP DATABASE IF EXISTS $B;" -c "CREATE DATABASE $B;"
docker compose exec -T db pg_restore -U "$U" -d "$B" --no-owner < "$F"
echo "Restaurado en la base '$B'. Comparación de conteos:"
for t in usuarios preguntas respuestas_estudiante publicaciones; do
  a=$(docker compose exec -T db psql -U "$U" -d "${POSTGRES_DB:-saesu}" -tAc "SELECT COUNT(*) FROM $t")
  b=$(docker compose exec -T db psql -U "$U" -d "$B" -tAc "SELECT COUNT(*) FROM $t")
  printf "  %-24s real=%-6s restaurada=%s\n" "$t" "$a" "$b"
done
