#!/usr/bin/env bash
# Paso 17: respaldo de la base de datos del contenedor (formato custom de pg_dump, comprimido).
# Uso: bash scripts/backup.sh   → deja respaldos/saesu-AAAAMMDD-HHMM.dump
set -euo pipefail
cd "$(dirname "$0")/.."
set -a; . ./.env; set +a
mkdir -p respaldos
f="respaldos/saesu-$(date +%Y%m%d-%H%M).dump"
docker compose exec -T db pg_dump -U "${POSTGRES_USER:-saesu}" -d "${POSTGRES_DB:-saesu}" -Fc > "$f"
echo "Respaldo creado: $f ($(du -h "$f" | cut -f1))"
# Verificación: listar el contenido del respaldo
docker compose exec -T db pg_restore --list < "$f" | grep -c "TABLE DATA" | xargs echo "Tablas con datos en el respaldo:"
# Conservar solo los 7 más recientes
ls -1t respaldos/*.dump | tail -n +8 | xargs -r rm --
