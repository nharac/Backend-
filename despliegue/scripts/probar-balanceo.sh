#!/usr/bin/env bash
# Paso 9: hace N peticiones al endpoint de prueba y cuenta qué réplica respondió cada una.
# Uso: bash scripts/probar-balanceo.sh [URL] [N]
#   bash scripts/probar-balanceo.sh http://localhost 20
#   bash scripts/probar-balanceo.sh https://saesu.duckdns.org 20
URL="${1:-http://localhost}"
N="${2:-20}"
echo "Probando $URL/api/instancia ($N peticiones)"
for i in $(seq 1 "$N"); do
  r=$(curl -s -o /dev/null -D - "$URL/api/instancia" | tr -d '\r' | awk -F': ' 'tolower($1)=="x-instancia"{print $2}')
  echo "$(date +%T)  petición $i → ${r:-SIN RESPUESTA}"
done | tee /tmp/balanceo.txt
echo "--- Resumen ---"
awk '{print $NF}' /tmp/balanceo.txt | sort | uniq -c
