# SAESU · Guía para la review interina

Dónde está cada cosa que pide la review, con la ruta exacta en el repositorio.

| Pide el profesor | Dónde se ve | Sección |
|---|---|---|
| SonarQube | `docker-compose-sonar.yml`, `pom.xml` (JaCoCo + propiedades `sonar.*`) | [1](#1-sonarqube) |
| API externa | `practica/adapters/output/ia/GeminiAdapter.java` (Google Gemini) | [2](#2-api-externa) |
| Nuevas funcionalidades | módulo `quiz`, reporte Excel, estado "Nuevo", Sumi | [3](#3-nuevas-funcionalidades) |
| 12 factores | `application.properties`, `.env.example`, `Dockerfile`, `docker-compose.yml` | [4](#4-los-12-factores) |
| Logs | `logback-spring.xml`, `core/logging/RequestLoggingFilter.java`, carpeta `logs/` del servidor | [5](#5-logs) |
| Principios SOLID | puertos y adaptadores de cada módulo | [6](#6-principios-solid) |

Rutas de código relativas a `src/main/java/co/edu/ucundinamarca/saesu/`.

---

## 1. SonarQube

**Levantar el servidor** (la primera vez tarda 1-2 minutos):

```bash
docker network create saesu-network   # solo si no existe
docker compose -f docker-compose-sonar.yml up -d
```

En Windows con Docker Desktop, si SonarQube se cae al arrancar, falta memoria virtual para Elasticsearch:

```powershell
wsl -d docker-desktop sysctl -w vm.max_map_count=262144
```

1. Entrar a http://localhost:9000 (usuario `admin`, contraseña `admin`; pide cambiarla).
2. *My Account → Security → Generate Token* (tipo *Global Analysis Token*).

**Analizar el backend** (corre las pruebas, mide cobertura con JaCoCo y sube el análisis):

```bash
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.token=EL_TOKEN
```

- La cobertura queda también en `target/site/jacoco/index.html` (se abre en el navegador).
- DTOs, configuración, entidades JPA y modelos de dominio no cuentan para la cobertura (`sonar.coverage.exclusions` en el `pom.xml`): no tienen lógica.

**Analizar el frontend**: ver `sonar-project.properties` en el repositorio del frontend.

## 2. API externa

| API | Para qué | Dónde |
|---|---|---|
| **Google Gemini** (IA generativa) | Tutor socrático del chat: guía al estudiante con preguntas sin darle la respuesta | `practica/adapters/output/ia/GeminiAdapter.java` |
| AWS S3 | Fotos de perfil e imágenes del foro | `core/storage/StorageService.java`, `core/config/AwsConfig.java` |
| Gmail SMTP | Códigos de verificación y recuperación, avisos del foro | `core/notificacion/EmailService.java` |

Cómo está hecha la integración con Gemini:

- **Puerto**: `practica/ports/output/IaServicePort.java`. El caso de uso del chat no sabe que es Gemini; cambiar de proveedor es escribir otro adaptador.
- **Cliente HTTP**: `WebClient` contra `generativelanguage.googleapis.com/v1beta/models/{modelo}:generateContent`, con un tiempo límite de 30 s.
- **Reglas del tutor**: van en `systemInstruction`, separadas del mensaje del estudiante, para que el estudiante no pueda cambiarlas.
- **Fallos**: reintenta con espera exponencial (1 s, 2 s, 4 s) solo si Gemini responde 503 o 429. Si no responde, lanza `IaNoDisponibleException` y el usuario recibe un mensaje claro.
- **Costo**: límite de 50 mensajes diarios por estudiante (`chat.limite.diario`), contexto de 20 mensajes y 2048 tokens de salida.
- **Configuración**: la clave y el modelo vienen de variables de entorno (`GEMINI_API_KEY`, `GEMINI_MODEL`). Nunca están en el código.

## 3. Nuevas funcionalidades

| Funcionalidad | Backend | Frontend |
|---|---|---|
| Quiz cronometrado (5-20 preguntas, 90 s cada una, calificado en el servidor) | `quiz/` · migración `V20__quiz.sql` · `/api/quiz/*` | `features/quiz/` |
| El docente ve los quices de cada estudiante y sus respuestas | `DocentePanelController` `GET /estudiantes/{id}/quices/{intentoId}` | `estudiante-detalle` |
| Estado "Nuevo": 7 días de gracia antes de marcar "En riesgo" | `EstadisticasDocenteService` (`DIAS_COMO_NUEVO`) | filtro "Nuevos" en estudiantes |
| Reporte Excel de dos hojas (estudiantes y resumen del grupo) | `core/excel/LibroExcel.java`, `docente/adapters/output/excel/` | botón Descargar |
| Foro, notificaciones y panel docente sin consultas N+1 | `PublicacionResumenJdbcAdapter`, índices en `V19` | — |
| Sumi, la mascota, con expresiones según el resultado | — | `shared/components/mascota`, `sumi-dice` |
| Logs con id de petición (ver sección 5) | `core/logging/` | — |

## 4. Los 12 factores

| # | Factor | Cómo lo cumple SAESU | Dónde |
|---|---|---|---|
| I | Código base | Un repositorio por aplicación (backend y frontend) en GitHub. Ramas `desarrollo` → `preproduccion` → `main` | GitHub `nharac/Backend-`, `nharac/FrontEnd` |
| II | Dependencias | Declaradas en Maven y npm, con versiones fijas en el `package-lock.json` | `pom.xml`, `package.json` |
| III | Configuración | Todo lo que cambia entre entornos sale de variables de entorno. `.env` no se sube; `.env.example` documenta cada variable | `application.properties` (`${DB_URL}`, `${JWT_SECRET}`…), `.env.example`, `.gitignore` |
| IV | Servicios de respaldo | PostgreSQL (Supabase), S3, Gmail y Gemini se conectan solo por URL y credenciales: se cambian sin tocar código | `.env` |
| V | Construir, publicar, ejecutar | Build en una etapa del `Dockerfile` (`mvn package`); la imagen final solo tiene el JRE y el `.jar` | `Dockerfile` (multi-stage) |
| VI | Procesos sin estado | La sesión es un JWT; el servidor no guarda nada en memoria entre peticiones. Los archivos van a S3, no al disco | `JwtAuthenticationFilter`, `StorageService` |
| VII | Asignación de puertos | `server.port=${SERVER_PORT:8080}`; Docker publica el puerto | `application.properties`, `docker-compose.yml` |
| VIII | Concurrencia | Varias instancias pueden correr detrás de nginx, porque no hay estado en memoria (salvo cachés de lectura que se reconstruyen) | `docker-compose.yml` |
| IX | Desechabilidad | Arranque en segundos, cierre ordenado (`server.shutdown=graceful`, 20 s para terminar las peticiones en curso) y `restart: unless-stopped` | `application.properties`, `docker-compose.yml` |
| X | Paridad dev/prod | El mismo Docker y las mismas migraciones Flyway en todos los entornos; solo cambia el `.env` y el perfil `prod` | `db/migration/V1…V20`, `application-prod.properties` |
| XI | Logs | Se escriben como flujo a la consola (`docker logs`); en producción también a archivos rotativos | `logback-spring.xml` |
| XII | Procesos de administración | Las migraciones Flyway corren solas al arrancar, son versionadas y nunca se editan | `db/migration/` |

## 5. Logs

**Configuración**: `src/main/resources/logback-spring.xml`.

- **Consola**: siempre, en color. Es lo que muestra `docker logs -f saesu-backend`.
- **Archivo** (perfil `prod`): `logs/saesu.log`. Rota cada día o al llegar a 10 MB, guarda 14 días y máximo 200 MB. Se escribe en otro hilo para no frenar las peticiones.
- **Errores** (perfil `prod`): `logs/errores.log` guarda solo WARN y ERROR, durante 30 días.
- En el servidor la carpeta queda en `./logs` junto al `docker-compose.yml` (volumen `./logs:/app/logs`), así que sobrevive a reconstruir el contenedor.

**Id de petición**: `core/logging/RequestLoggingFilter.java`.

- Cada petición a `/api/**` recibe un id corto, que también se devuelve en la cabecera `X-Request-Id`.
- Todas las líneas que escribe esa petición llevan el mismo id, incluso el correo que se envía en segundo plano (`AsyncConfig` copia el contexto al hilo del correo).
- El usuario sale con el correo enmascarado (`la***@ucundinamarca.edu.co`).
- Al final se registra método, ruta, código HTTP y duración. Las peticiones de más de 1,5 s se marcan `LENTA`.

Ejemplo real:

```
22:56:35.498 INFO  [25795a78 -] saesu.http : POST /api/auth/recuperar -> 200 (336 ms)
22:56:35.600 INFO  [e43bcc68 la***@ucundinamarca.edu.co] QuizService : Quiz 42 iniciado: tema derivadas, 5 preguntas
22:56:35.611 INFO  [25795a78 -] EmailService : Correo "Recupera tu contraseña" enviado a la***@ucundinamarca.edu.co
22:56:35.759 INFO  [945ad9c0 la***@ucundinamarca.edu.co] QuizService : Quiz 42 entregado: 0/5 correctas (0%)
22:55:53.002 WARN  [6f5931d3 -] LoginService : Login fallido: contraseña incorrecta para la***@ucundinamarca.edu.co
22:55:53.029 WARN  [8972b320 -] saesu.http : GET /api/docente/estudiantes -> 403 (6 ms)
```

**Qué se registra**: logins correctos y fallidos, límite de peticiones superado (429), quices iniciados y entregados, publicaciones creadas, reportes Excel, correos enviados o fallidos, errores de Gemini y errores no controlados con su traza.

**Qué nunca se registra**: contraseñas, tokens, códigos de verificación (por eso no se registra el asunto de los correos) ni correos completos.

**Ver los logs en el servidor**:

```bash
docker logs -f saesu-backend          # en vivo
tail -f logs/saesu.log                # archivo
grep 25795a78 logs/saesu.log          # todo lo de una petición
```

## 6. Principios SOLID

La arquitectura hexagonal es la base. Cada módulo (`usuario`, `practica`, `foro`, `docente`, `quiz`, `gamificacion`) tiene `domain`, `application`, `ports` (`input`/`output`) y `adapters` (`input/rest`, `output/jpa|jdbc|ia|excel`).

| Principio | Ejemplo en SAESU |
|---|---|
| **S** · Responsabilidad única | `ExportarEstudiantesService` junta los datos y `ReporteEstudiantesExcelAdapter` arma el archivo; `LibroExcel` solo sabe escribir xlsx. `RequestLoggingFilter` solo registra peticiones y `RateLimitFilter` solo limita. Cada caso de uso de usuario es una clase: `LoginService`, `RegistrarUsuarioService`, `RecuperarPasswordService`… |
| **O** · Abierto/cerrado | Para cambiar Gemini por otro proveedor se escribe un adaptador nuevo de `IaServicePort` sin tocar `EnviarMensajeChatService`. Un logro nuevo es una constante más en el enum `Logro` y su condición. Agregar un formato de reporte es otro adaptador de `ReporteEstudiantesPort`. |
| **L** · Sustitución de Liskov | Los adaptadores reemplazan al puerto sin cambiar el comportamiento esperado. En las pruebas, `QuizRepositoryPort`, `LogrosPort` o `RankingPort` se sustituyen por mocks y los servicios funcionan igual (127 pruebas). |
| **I** · Segregación de interfaces | Puertos pequeños y específicos: `PublicacionResumenPort` (solo listar) separado de `PublicacionRepositoryPort` (guardar/editar); `CodigoRecuperacionPort` y `RegistroPendientePort` aparte de `UsuarioRepositoryPort`; `RankingUseCase` y `LogrosUseCase` en vez de una sola interfaz de gamificación. |
| **D** · Inversión de dependencias | Los servicios dependen de interfaces (`ports`), no de JPA ni de SQL: `QuizService → QuizRepositoryPort ← QuizJdbcAdapter`, `LogrosService → LogrosPort ← LogrosJdbcRepository`. Los controladores dependen de puertos de entrada (`RankingController → RankingUseCase`). Spring inyecta la implementación por constructor. |

**Pendiente de mejora** (dicho con honestidad para la review): `PuntosService` y `RachaSchedulerService` todavía usan repositorios JPA directamente, y `StorageService` (S3) no tiene un puerto propio. El siguiente paso es pasarlos detrás de `PuntosPort` y `AlmacenamientoPort`, como se hizo con ranking y logros.
