# Despliegue de SAESU · arquitectura del proyecto integrador

```
                 Internet / red local
                         │  80 (y 443 en cloud)
                ┌────────▼─────────┐
                │  nginx           │  proxy inverso + balanceador (único puerto público)
                └───┬──────────┬───┘
            /       │          │  /api/  (round robin, reintenta en la otra si una cae)
       ┌────────────▼─┐   ┌────▼───────────┐ ┌────────────────┐
       │  frontend    │   │  backend1      │ │  backend2      │  Spring Boot (sin puertos públicos)
       │  Angular     │   │  tareas prog.  │ │                │
       └──────────────┘   └───────┬────────┘ └───────┬────────┘
                                  └──────┬───────────┘
                                  ┌──────▼───────┐
                                  │  db          │  PostgreSQL 17 + volumen saesu-pgdata
                                  └──────────────┘  (sin puertos públicos)
                  red Docker interna: saesu-interna
```

| Servicio | Imagen | Puerto | ¿Público? |
|---|---|---|---|
| nginx | nginx:1.27-alpine | 80 (443 con HTTPS) | **Sí, el único** |
| frontend | `../../FrontEnd/Dockerfile` | 80 interno | No |
| backend1, backend2 | `../Dockerfile` | 8080 interno | No |
| db | postgres:17-alpine | 5432 interno | No |

El mismo `compose.yaml` se usa en **on-premise** (VirtualBox + Ubuntu Server) y en **cloud** (AWS EC2).
Lo único que cambia es el `.env` de esta carpeta (se copia de `.env.example`).

---

## 6. Replicación en cloud (AWS EC2)

### Paso 11 · VM, llave SSH y reglas de red
1. **Instancia:** Ubuntu Server 24.04, `t3.micro` (1 GB, capa gratuita) con swap, o `t3.small` (2 GB, más holgada).
   Antes de crearla revisa límites y costos en *Billing → Free Tier* y crea una alerta de presupuesto.
2. **Llave SSH:** par de llaves `.pem` creado en AWS; en tu PC `chmod 400 saesu.pem` y
   `ssh -i saesu.pem ubuntu@IP`. No se usa contraseña.
3. **Security Group (reglas mínimas de entrada):**

   | Puerto | Origen | Para qué |
   |---|---|---|
   | 22 (SSH) | **Solo tu IP** (`Mi IP`) | Administración |
   | 80 (HTTP) | 0.0.0.0/0 | Página y API (por NGINX) |
   | 443 (HTTPS) | 0.0.0.0/0 | Igual, cifrado |

   **No** abrir 8080 ni 5432. Si existen reglas para esos puertos, bórralas.

### Paso 12 · Código, Docker y el mismo compose
```bash
ssh -i saesu.pem ubuntu@IP
# Docker y Compose (si no están):
curl -fsSL https://get.docker.com | sudo sh && sudo usermod -aG docker $USER && newgrp docker
# Código (repos lado a lado):
cd ~ && git clone https://github.com/nharac/Backend-.git && git clone https://github.com/nharac/FrontEnd.git
cd ~/Backend-/despliegue
cp .env.example .env && nano .env          # llenar claves (copiar del .env actual del backend)
sudo bash scripts/swap.sh                  # solo en t3.micro
# Apagar los contenedores de la versión anterior (liberan los puertos 80 y 8080):
(cd ~/FrontEnd && docker compose down); (cd ~/Backend- && docker compose down)
# Opcional, para conservar los datos actuales de Supabase (ANTES del primer arranque):
#   SUPABASE_URL='postgresql://...' bash scripts/migrar-desde-supabase.sh
docker compose build
docker compose up -d
docker compose ps
```
Para HTTPS con el dominio `saesu.duckdns.org`, agrega al `.env`: `COMPOSE_FILE=compose.yaml:compose.https.yaml` y vuelve a `docker compose up -d`.

**CD automático:** en *Settings → Secrets → Actions* del repo **FrontEnd** agrega
`EC2_RUTA_DESPLIEGUE=/home/ubuntu/Backend-/despliegue`. Desde ese momento cada push a `main`
reconstruye con esta arquitectura (el backend la usa sola en cuanto existe `despliegue/.env`).

### Paso 13 · Acceso remoto y puertos
Desde **tu PC** (no desde el servidor):
```bash
bash scripts/verificar-puertos.sh saesu.duckdns.org   # 80/443 abiertos, 8080/5432 cerrados
bash scripts/probar-balanceo.sh https://saesu.duckdns.org 20
```
En el informe **no** pongas: el `.env`, la llave `.pem`, la cadena de conexión ni las claves de API.

### Paso 14 · Comparación on-premise vs cloud
Llena con lo que mida `scripts/monitor.sh` en cada ambiente y `time docker compose up -d --build`:

| Aspecto | On-premise (VirtualBox) | Cloud (AWS EC2) |
|---|---|---|
| IP y red | IP privada (puente/NAT), solo red local | IP pública + DNS `saesu.duckdns.org` |
| CPU | núcleos asignados a la VM | 2 vCPU (t3, créditos de ráfaga) |
| RAM | GB asignados a la VM | 1 GB + 2 GB swap (t3.micro) / 2 GB (t3.small) |
| Almacenamiento | disco virtual (.vdi) | EBS gp3 (8–20 GB) |
| Tiempo de despliegue | primer build / rebuild | primer build / CD automático |
| Latencia observada | `curl -w %{time_total}` en la LAN | mismo comando desde Fusagasugá |
| Costo estimado | $0 (hardware propio) | capa gratuita 12 meses; después ≈ USD 8–17/mes |
| Riesgos | se apaga si se apaga el PC; sin IP pública | costo si se pasa de la capa gratuita; exposición a internet |
| Recuperación | snapshot de VirtualBox + `backup.sh` | snapshot EBS / AMI + `backup.sh` + CD reconstruye en minutos |

---

## Pruebas del anexo (pasos 6–10, 15–17)

| Paso | Cómo |
|---|---|
| 6 · Caso de uso extremo a extremo | Registrarse → responder una pregunta en Práctica → `docker compose exec db psql -U saesu -c "SELECT * FROM respuestas_estudiante ORDER BY id DESC LIMIT 1;"` |
| 7 · Persistencia | `docker compose down && docker compose up -d` → el dato sigue (volumen `saesu-pgdata`). Respaldo: `scripts/backup.sh` |
| 9 · Balanceo | `bash scripts/probar-balanceo.sh http://localhost 20` → mitad backend1, mitad backend2 |
| 10 · Falla de una réplica | `docker compose stop backend2` → repetir la prueba: todo lo atiende backend1 → `docker compose start backend2` |
| 15 · Monitoreo | `bash scripts/monitor.sh http://localhost` → `reportes/` |
| 16 · Incidente | `docker compose kill backend1`; diagnóstico con `docker compose ps`, `docker compose logs backend1`, `docker stats`, `curl` |
| 17 · Respaldo y restauración | `bash scripts/backup.sh` y `bash scripts/restaurar.sh respaldos/ARCHIVO.dump` (restaura en una base de prueba aparte) |

## Consideraciones de varias réplicas
- **Sesiones:** JWT sin estado → cualquier réplica atiende cualquier petición.
- **Archivos:** las fotos e imágenes del foro van a S3, no al disco del contenedor.
- **Tareas programadas** (rachas, torneo, recordatorios): solo `backend1` las ejecuta (`SAESU_TAREAS_PROGRAMADAS`).
- **Migraciones Flyway:** las dos réplicas arrancan a la vez; Flyway usa un bloqueo en PostgreSQL y las aplica una sola vez.
- **Límite de peticiones por IP:** se cuenta en memoria de cada réplica, así que con 2 réplicas el límite efectivo es el doble. Para hacerlo exacto habría que guardarlo en Redis.
- **Base de datos:** una sola instancia (no se replica). Dos réplicas en la misma VM no protegen contra la caída de la VM ni de NGINX.
