# Pruebas de carga y estrés de SAESU (JMeter)

Los 9 escenarios obligatorios están en `escenarios/`, uno por archivo. Cada uno se puede abrir en la interfaz de JMeter para mostrarlo, pero **se corre en modo consola** con `correr-escenario.ps1`, que genera el reporte HTML y registra el consumo del backend.

| # | Archivo | Tipo | Qué hace en SAESU | Valores por defecto |
|---|---|---|---|---|
| 1 | `01-login-concurrente` | Carga | `POST /api/auth/login`; valida 200, token JWT, tiempo y ausencia de 5xx | 30 usuarios, rampa 10 s, 5 vueltas |
| 2 | `02-consulta-masiva` | Carga | Perfil, preguntas, foro, ranking, logros, progreso, historial de quices | 50 usuarios, rampa 15 s, 10 vueltas |
| 3 | `03-registro-concurrente` | Carga | Crea una publicación con título único, verifica que se guardó igual y la elimina | 30 usuarios, rampa 10 s |
| 4 | `04-flujo-completo` | Carga | Login → consulta → responde una pregunta → publica → responde → consulta final → elimina | 15 usuarios, 5 vueltas |
| 5 | `05-saturacion-login` | Estrés | Login con 50, 100, 200 y 300 usuarios (4 fases automáticas) | 3 vueltas por fase |
| 6 | `06-sobrecarga-consultas` | Estrés | Lecturas sin pausas | 200 usuarios, rampa 20 s, 5 vueltas |
| 7 | `07-sobrecarga-crud` | Estrés | Crear, actualizar, consultar, eliminar y comprobar 404 | 80 usuarios, 20 vueltas |
| 8 | `08-resistencia` | Estrés | Carga constante con lecturas y práctica | 50 usuarios durante 10 min |
| 9 | `09-falla-controlada` | Estrés | 400 usuarios sin pausas 3 min y luego 10 usuarios 1 min para medir la recuperación | — |

> El escenario 3 de la guía sugiere "creación de usuarios". En SAESU el registro envía un correo real con un código por Gmail, así que 40 registros simultáneos bloquearían la cuenta de correo. La escritura concurrente se mide con publicaciones del foro, que pasan por la misma capa de persistencia (JPA + PostgreSQL).

## 1. Preparar (una vez)

1. Instalar **Java 17** (ya lo tienen) y **JMeter 5.6.3**: descargar el `.zip` de https://jmeter.apache.org/download_jmeter.cgi y descomprimirlo, por ejemplo en `C:\apache-jmeter-5.6.3`.
2. En PowerShell: `$env:JMETER_HOME = "C:\apache-jmeter-5.6.3"`
3. Copiar `usuarios.ejemplo.csv` como `usuarios.csv` y poner cuentas de **estudiante de prueba** (verificadas). Con una sola cuenta funciona; con varias es más realista. Este archivo no se sube a git.

## 2. Subir el límite de peticiones SOLO durante las pruebas

El backend limita a 10 logins y 120 peticiones por minuto por IP (protección contra fuerza bruta). Como JMeter manda todo desde un solo PC, sin subirlo casi todo daría `429 Too Many Requests`.

En el `.env` del backend agregar:

```
SAESU_RATE_LIMIT_AUTH=100000
SAESU_RATE_LIMIT_API=1000000
```

y reiniciar: `docker compose up -d --build`. En el log debe salir `Límite de peticiones por minuto: autenticación=100000, API=1000000`.

**Al terminar todas las pruebas, borrar esas dos líneas y volver a correr `docker compose up -d`.**

> Opcional para el informe: correr el escenario 1 una vez **sin** subir el límite. Los `429` demuestran que la protección funciona.

## 3. Correr

```powershell
cd backend\jmeter
.\correr-escenario.ps1 todos                  # escenarios 1 a 4 (carga)
.\correr-escenario.ps1 05-saturacion-login    # 4 fases: 50, 100, 200, 300
.\correr-escenario.ps1 06-sobrecarga-consultas
.\correr-escenario.ps1 07-sobrecarga-crud
.\correr-escenario.ps1 08-resistencia         # 10 min (-Duracion 900 para 15)
.\correr-escenario.ps1 09-falla-controlada
```

Cada ejecución crea `resultados\<escenario>-<fecha>\` con:
- `reporte\index.html`: el reporte HTML de JMeter (se abre solo). Las gráficas están en *Charts* → *Over Time* (Response Times Over Time, Transactions per Second) y *Throughput*.
- `resultados.jtl`: datos crudos.
- `docker-stats.csv`: CPU y memoria del backend cada 2 s.

Se pueden cambiar valores: `.\correr-escenario.ps1 02-consulta-masiva -Usuarios 80 -Vueltas 10`.

## 4. Qué anotar de cada reporte

En la pestaña *Statistics* del reporte: **Samples, Average, 90th/95th pct, Max, Error %, Throughput (req/s)**. En *Errors*: qué códigos aparecieron (401, 429, 500, timeouts). En `docker-stats.csv`: CPU y memoria máximos.

## 5. Limpieza

Los escenarios 3, 4 y 7 eliminan lo que crean. Si alguna petición falla a mitad, quedan publicaciones `JMETER-…`; `limpiar-datos-jmeter.sql` las revisa y borra desde el editor SQL de Supabase.
