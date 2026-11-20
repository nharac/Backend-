#!/usr/bin/env bash
# Paso 13: comprueba desde OTRO equipo (tu PC) qué puertos responden en el servidor.
# Solo deben responder 80 (y 443 si hay HTTPS). 8080 (backend) y 5432 (PostgreSQL) deben estar cerrados.
# Uso: bash scripts/verificar-puertos.sh IP-O-DOMINIO
H="${1:?Uso: verificar-puertos.sh IP-O-DOMINIO}"
for p in 80 443 8080 5432; do
  if timeout 4 bash -c "</dev/tcp/$H/$p" 2>/dev/null; then estado="ABIERTO"; else estado="cerrado"; fi
  case $p in 80|443) esperado="(debe estar abierto)";; *) esperado="(debe estar cerrado)";; esac
  printf "Puerto %-5s %-8s %s\n" "$p" "$estado" "$esperado"
done
echo "Frontend:  $(curl -s -o /dev/null -w '%{http_code}' "http://$H/")"
echo "API:       $(curl -s -o /dev/null -w '%{http_code}' "http://$H/api/instancia")"
