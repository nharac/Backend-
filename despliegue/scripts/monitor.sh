#!/usr/bin/env bash
# Paso 15: reporte del estado del servidor y de SAESU. Uso: bash scripts/monitor.sh [URL]
# Guarda el reporte en reportes/monitor-HOST-FECHA.txt
cd "$(dirname "$0")/.."
URL="${1:-http://localhost}"
mkdir -p reportes
R="reportes/monitor-$(hostname)-$(date +%Y%m%d-%H%M).txt"
{
  echo "===== SAESU · monitor ====="
  echo "Fecha:     $(date '+%Y-%m-%d %H:%M:%S %Z')"
  echo "Host:      $(hostname)  ($(hostname -I 2>/dev/null | awk '{print $1}'))"
  echo "Sistema:   $(. /etc/os-release; echo "$PRETTY_NAME") · kernel $(uname -r)"
  echo; echo "--- CPU (carga 1/5/15 min, núcleos: $(nproc)) ---"; uptime
  echo; echo "--- Memoria ---"; free -h
  echo; echo "--- Disco ---"; df -h / | tail -1
  echo; echo "--- Contenedores ---"; docker compose ps --format 'table {{.Name}}\t{{.State}}\t{{.Status}}'
  echo; echo "--- Consumo Docker ---"; docker stats --no-stream --format 'table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}'
  echo; echo "--- Pruebas HTTP ---"
  echo "Frontend $URL/            → $(curl -s -o /dev/null -w '%{http_code} en %{time_total}s' "$URL/")"
  echo "API      $URL/api/instancia → $(curl -s -o /dev/null -w '%{http_code} en %{time_total}s' "$URL/api/instancia") ($(curl -s -D - -o /dev/null "$URL/api/instancia" | tr -d '\r' | awk -F': ' 'tolower($1)=="x-instancia"{print $2}'))"
  echo; echo "--- Volumen de datos ---"; docker volume inspect saesu-pgdata --format '{{.Name}} → {{.Mountpoint}}' 2>/dev/null
} | tee "$R"
echo; echo "Reporte guardado en $R"
