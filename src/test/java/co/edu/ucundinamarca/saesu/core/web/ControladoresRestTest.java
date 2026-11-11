package co.edu.ucundinamarca.saesu.core.web;

import co.edu.ucundinamarca.saesu.practica.adapters.input.rest.MiAvanceController;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ActividadResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.MetaSemanalResponse;
import co.edu.ucundinamarca.saesu.practica.ports.input.MiAvanceUseCase;
import co.edu.ucundinamarca.saesu.core.exception.GlobalExceptionHandler;
import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.foro.adapters.input.rest.ForoController;
import co.edu.ucundinamarca.saesu.foro.application.service.EliminarPublicacionService;
import co.edu.ucundinamarca.saesu.foro.ports.input.CrearPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.input.ListarPublicacionesUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.input.ResponderPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.input.VerDetallePublicacionUseCase;
import co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest.LogrosController;
import co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest.RankingController;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.RankingResponse;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.LogrosUseCase;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.RankingUseCase;
import co.edu.ucundinamarca.saesu.practica.adapters.input.rest.ChatController;
import co.edu.ucundinamarca.saesu.practica.adapters.input.rest.PracticaController;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.PistaResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.LimiteChatExcedidoException;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import co.edu.ucundinamarca.saesu.practica.ports.input.EnviarMensajeChatUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ListarPreguntasUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ObtenerPistaUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ObtenerProgresoUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ValidarRespuestaUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import co.edu.ucundinamarca.saesu.quiz.adapters.input.rest.QuizController;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizHistorialResponse;
import co.edu.ucundinamarca.saesu.quiz.domain.exception.QuizYaFinalizadoException;
import co.edu.ucundinamarca.saesu.quiz.ports.input.QuizUseCase;
import co.edu.ucundinamarca.saesu.usuario.adapters.input.rest.AuthController;
import co.edu.ucundinamarca.saesu.usuario.adapters.input.rest.PerfilController;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.RegistroResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.application.service.RecuperarPasswordService;
import co.edu.ucundinamarca.saesu.usuario.application.service.SubirFotoPerfilService;
import co.edu.ucundinamarca.saesu.usuario.application.service.VerificarCorreoService;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CredencialesInvalidasException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.input.ActualizarPerfilUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.input.LoginUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.input.ObtenerPerfilUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.input.RegistrarUsuarioUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de los adaptadores de entrada REST: rutas, códigos HTTP, validación de los datos
 * y traducción de las excepciones de dominio (GlobalExceptionHandler). Los casos de uso son mocks,
 * así que no se necesita base de datos ni Spring completo.
 */
class ControladoresRestTest {

    private static final String LAURA = "laura@ucundinamarca.edu.co";
    private static final UsernamePasswordAuthenticationToken AUTH =
            new UsernamePasswordAuthenticationToken(LAURA, null, List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE")));

    private static MockMvc mvc(Object controlador) {
        return MockMvcBuilders.standaloneSetup(controlador).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Nested
    class Autenticacion {
        RegistrarUsuarioUseCase registrar = mock(RegistrarUsuarioUseCase.class);
        LoginUseCase login = mock(LoginUseCase.class);
        ObtenerPerfilUseCase perfil = mock(ObtenerPerfilUseCase.class);
        ActualizarPerfilUseCase actualizar = mock(ActualizarPerfilUseCase.class);
        JwtService jwt = mock(JwtService.class);
        UsuarioRepositoryPort usuarios = mock(UsuarioRepositoryPort.class);
        RecuperarPasswordService recuperar = mock(RecuperarPasswordService.class);
        VerificarCorreoService verificar = mock(VerificarCorreoService.class);
        MockMvc mvc;

        @BeforeEach
        void setUp() {
            mvc = mvc(new AuthController(registrar, login, perfil, actualizar, jwt, usuarios, recuperar, verificar));
        }

        @Test
        @DisplayName("POST /registro válido: 201 con el correo y los minutos del código")
        void registro() throws Exception {
            when(registrar.registrar(any())).thenReturn(new RegistroResponse(LAURA, "Revisa tu correo", 15));

            mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombre\":\"Laura Gómez\",\"correo\":\"" + LAURA + "\",\"password\":\"Clave#2026\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.correo").value(LAURA))
                    .andExpect(jsonPath("$.minutosVigencia").value(15));
        }

        @Test
        @DisplayName("POST /registro con correo que no es institucional: 400 sin llamar al caso de uso")
        void registroCorreoInvalido() throws Exception {
            mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombre\":\"Laura\",\"correo\":\"laura@gmail.com\",\"password\":\"Clave#2026\"}"))
                    .andExpect(status().isBadRequest());
            verify(registrar, never()).registrar(any());
        }

        @Test
        @DisplayName("POST /login: 200 con token; credenciales malas: 401")
        void login() throws Exception {
            when(login.login(any())).thenReturn(new AuthResponse(new UsuarioResponse(1L, "Laura", LAURA, "ESTUDIANTE", null), "jwt", "refresh"));
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"correo\":\"" + LAURA + "\",\"password\":\"Clave#2026\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt"));

            when(login.login(any())).thenThrow(new CredencialesInvalidasException("Credenciales inválidas"));
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"correo\":\"" + LAURA + "\",\"password\":\"mala\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
        }

        @Test
        @DisplayName("GET /perfil usa el correo del JWT")
        void perfil() throws Exception {
            when(perfil.obtenerPerfil(LAURA)).thenReturn(new UsuarioResponse(1L, "Laura", LAURA, "ESTUDIANTE", null));
            mvc.perform(get("/api/auth/perfil").principal(AUTH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Laura"));
        }

        @Test
        @DisplayName("POST /refresh: token válido renueva; inválido responde 401")
        void refresh() throws Exception {
            when(jwt.validarToken("r1")).thenReturn(true);
            when(jwt.esRefreshToken("r1")).thenReturn(true);
            when(jwt.extraerCorreo("r1")).thenReturn(LAURA);
            when(usuarios.buscarPorCorreo(LAURA)).thenReturn(Optional.of(new Usuario(1L, "Laura", LAURA, "h", "ESTUDIANTE", LocalDateTime.now(), null)));
            when(jwt.generarToken(LAURA, "ESTUDIANTE")).thenReturn("nuevo");

            mvc.perform(post("/api/auth/refresh").param("refreshToken", "r1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("nuevo"))
                    .andExpect(jsonPath("$.refreshToken").value("r1"));

            mvc.perform(post("/api/auth/refresh").param("refreshToken", "falso"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Recuperación y verificación: responden el mismo mensaje exista o no la cuenta")
        void recuperacionYVerificacion() throws Exception {
            mvc.perform(post("/api/auth/recuperar").contentType(MediaType.APPLICATION_JSON).content("{\"correo\":\"" + LAURA + "\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.mensaje").exists());
            verify(recuperar).solicitar(LAURA);

            mvc.perform(post("/api/auth/recuperar/validar").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"correo\":\"" + LAURA + "\",\"codigo\":\"123456\"}"))
                    .andExpect(status().isOk());
            verify(recuperar).validarCodigo(LAURA, "123456");

            mvc.perform(post("/api/auth/recuperar/validar").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"correo\":\"" + LAURA + "\",\"codigo\":\"12\"}"))
                    .andExpect(status().isBadRequest());

            mvc.perform(post("/api/auth/reenviar-verificacion").contentType(MediaType.APPLICATION_JSON).content("{\"correo\":\"" + LAURA + "\"}"))
                    .andExpect(status().isOk());
            verify(verificar).reenviar(LAURA);
        }
    }

    @Nested
    class Quiz {
        QuizUseCase quiz = mock(QuizUseCase.class);
        MockMvc mvc = mvc(new QuizController(quiz));

        @Test
        @DisplayName("Iniciar con menos de 5 preguntas: 400")
        void cantidadInvalida() throws Exception {
            mvc.perform(post("/api/quiz/iniciar").principal(AUTH).contentType(MediaType.APPLICATION_JSON).content("{\"tema\":\"limites\",\"cantidad\":3}"))
                    .andExpect(status().isBadRequest());
            verify(quiz, never()).iniciar(any(), any());
        }

        @Test
        @DisplayName("Entregar dos veces: 409")
        void dobleEntrega() throws Exception {
            when(quiz.finalizar(eq(LAURA), eq(5L), any())).thenThrow(new QuizYaFinalizadoException("Este quiz ya fue entregado"));
            mvc.perform(post("/api/quiz/5/finalizar").principal(AUTH).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"respuestas\":[{\"preguntaId\":1,\"letra\":\"A\"}]}"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Historial del estudiante autenticado")
        void historial() throws Exception {
            when(quiz.historial(LAURA)).thenReturn(new QuizHistorialResponse(3, 60, 80, List.of()));
            mvc.perform(get("/api/quiz/historial").principal(AUTH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mejor").value(80));
        }
    }

    @Nested
    class PracticaYChat {
        ListarPreguntasUseCase listar = mock(ListarPreguntasUseCase.class);
        ValidarRespuestaUseCase validar = mock(ValidarRespuestaUseCase.class);
        ObtenerPistaUseCase pista = mock(ObtenerPistaUseCase.class);
        ObtenerProgresoUseCase progreso = mock(ObtenerProgresoUseCase.class);
        EnviarMensajeChatUseCase enviar = mock(EnviarMensajeChatUseCase.class);
        ChatMensajeRepositoryPort chat = mock(ChatMensajeRepositoryPort.class);
        UsuarioConsultaPort usuarios = mock(UsuarioConsultaPort.class);

        @Test
        @DisplayName("Listar preguntas con valores por defecto y pedir la pista")
        void practica() throws Exception {
            MockMvc mvc = mvc(new PracticaController(listar, validar, pista, progreso));
            when(listar.listar("limites", null, 0, 10)).thenReturn(List.of());
            mvc.perform(get("/api/practica/preguntas").param("tema", "limites")).andExpect(status().isOk());
            verify(listar).listar("limites", null, 0, 10);

            when(listar.sesion(eq("limites"), eq(null), eq(10), any())).thenReturn(List.of());
            mvc.perform(get("/api/practica/preguntas/sesion").param("tema", "limites").principal(AUTH)).andExpect(status().isOk());
            verify(listar).sesion(eq("limites"), eq(null), eq(10), any());

            when(pista.obtenerPista(7L)).thenReturn(new PistaResponse(7L, "Factoriza", "Pista cargada correctamente"));
            mvc.perform(get("/api/practica/preguntas/7/pista"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pista").value("Factoriza"));
        }

        @Test
        @DisplayName("Reto del día: consultar, responder y 409 si ya respondió")
        void retoDelDia() throws Exception {
            co.edu.ucundinamarca.saesu.practica.application.service.RetoDiarioService reto =
                    mock(co.edu.ucundinamarca.saesu.practica.application.service.RetoDiarioService.class);
            MockMvc mvc = mvc(new co.edu.ucundinamarca.saesu.practica.adapters.input.rest.RetoDiarioController(reto));
            var respuesta = new co.edu.ucundinamarca.saesu.practica.application.dto.response.RetoDiarioResponse(
                    java.time.LocalDate.of(2026, 11, 8), "pendiente", null, null, null, null, 30);
            when(reto.hoy(LAURA)).thenReturn(respuesta);
            mvc.perform(get("/api/practica/reto").principal(AUTH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value("pendiente"))
                    .andExpect(jsonPath("$.puntos").value(30));

            when(reto.responder(LAURA, 300L)).thenThrow(new co.edu.ucundinamarca.saesu.practica.domain.exception.RetoYaRespondidoException());
            mvc.perform(post("/api/practica/reto/responder").principal(AUTH).contentType(MediaType.APPLICATION_JSON).content("{\"opcionId\":300}"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Ya respondiste el reto de hoy. Vuelve mañana por otro."));
            mvc.perform(post("/api/practica/reto/responder").principal(AUTH).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Chat: límite diario superado responde 429; historial y borrado usan el usuario del JWT")
        void chat() throws Exception {
            MockMvc mvc = mvc(new ChatController(enviar, chat, usuarios));
            when(enviar.enviarMensaje(any(), eq(LAURA))).thenThrow(new LimiteChatExcedidoException("Has alcanzado el límite diario de 50 mensajes."));
            mvc.perform(post("/api/chat/mensaje").principal(AUTH).contentType(MediaType.APPLICATION_JSON).content("{\"mensaje\":\"Hola\"}"))
                    .andExpect(status().isTooManyRequests());

            when(usuarios.obtenerIdPorCorreo(LAURA)).thenReturn(1L);
            when(chat.listarHistorialLibre(eq(1L), anyInt())).thenReturn(List.of(
                    new ChatMensaje(1L, 1L, null, RolEmisor.IA, "¿En qué tema?", LocalDateTime.now())));
            mvc.perform(get("/api/chat/historial").principal(AUTH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensajes[0].rolEmisor").value("ia"));

            mvc.perform(delete("/api/chat/historial").principal(AUTH)).andExpect(status().isNoContent());
            verify(chat).eliminarHistorialLibre(1L);
        }
    }

    @Nested
    class ForoYGamificacion {
        @Test
        @DisplayName("Foro: listar con orden por defecto y eliminar responde 204")
        void foro() throws Exception {
            ListarPublicacionesUseCase listar = mock(ListarPublicacionesUseCase.class);
            EliminarPublicacionService eliminar = mock(EliminarPublicacionService.class);
            MockMvc mvc = mvc(new ForoController(listar, mock(CrearPublicacionUseCase.class), mock(VerDetallePublicacionUseCase.class),
                    mock(ResponderPublicacionUseCase.class), eliminar));
            when(listar.listar(isNull(), isNull(), eq("reciente"), eq(0), eq(10))).thenReturn(List.of());

            mvc.perform(get("/api/foro/publicaciones")).andExpect(status().isOk());
            mvc.perform(delete("/api/foro/publicaciones/10").principal(AUTH)).andExpect(status().isNoContent());
            verify(eliminar).ejecutar(10L, LAURA);
        }

        @Test
        @DisplayName("Vestidor de Sumi: estado, comprar, vestir e historial de monedas")
        void vestidor() throws Exception {
            var servicio = mock(co.edu.ucundinamarca.saesu.gamificacion.application.service.VestidorService.class);
            MockMvc mvc = mvc(new co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest.VestidorController(servicio));
            var estado = new co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.VestidorResponse(
                    30, false, List.of("sombrero:gorra"), java.util.Map.of("sombrero", "gorra"));
            when(servicio.estado(LAURA)).thenReturn(estado);
            when(servicio.comprar(eq(LAURA), any())).thenReturn(estado);
            when(servicio.vestir(eq(LAURA), any())).thenReturn(estado);
            when(servicio.historial(LAURA)).thenReturn(List.of(
                    new co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MovimientoMonedaResponse(3, "Foro", java.time.LocalDateTime.now())));

            mvc.perform(get("/api/sumi/vestidor").principal(AUTH))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.monedas").value(30));
            mvc.perform(post("/api/sumi/vestidor/comprar").principal(AUTH).contentType("application/json")
                            .content("{\"categoria\":\"sombrero\",\"prenda\":\"gorra\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.inventario[0]").value("sombrero:gorra"));
            mvc.perform(post("/api/sumi/vestidor/comprar").principal(AUTH).contentType("application/json").content("{}"))
                    .andExpect(status().isBadRequest());
            mvc.perform(put("/api/sumi/vestidor/estilo").principal(AUTH).contentType("application/json")
                            .content("{\"sombrero\":\"gorra\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.estilo.sombrero").value("gorra"));
            mvc.perform(get("/api/sumi/monedas/historial").principal(AUTH))
                    .andExpect(status().isOk()).andExpect(jsonPath("$[0].cantidad").value(3));

            when(servicio.comprar(eq(LAURA), any())).thenThrow(
                    new co.edu.ucundinamarca.saesu.gamificacion.domain.exception.CompraNoValidaException("Te faltan 5 Sumi Monedas"));
            mvc.perform(post("/api/sumi/vestidor/comprar").principal(AUTH).contentType("application/json")
                            .content("{\"categoria\":\"sombrero\",\"prenda\":\"corona\"}"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Foro: \"Me sirvió\" de una publicación y marcar una respuesta")
        void meSirvio() throws Exception {
            var utiles = mock(co.edu.ucundinamarca.saesu.foro.application.service.UtilesService.class);
            MockMvc mvc = mvc(new co.edu.ucundinamarca.saesu.foro.adapters.input.rest.UtilesController(utiles));
            when(utiles.dePublicacion(1L, LAURA)).thenReturn(
                    new co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilesResponse(java.util.Map.of(7L, 2L), List.of(7L)));
            when(utiles.alternar(7L, LAURA)).thenReturn(
                    new co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilResponse(7L, 3L, true));

            mvc.perform(get("/api/foro/publicaciones/1/utiles").principal(AUTH))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.mias[0]").value(7));
            mvc.perform(post("/api/foro/respuestas/7/util").principal(AUTH))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.utiles").value(3)).andExpect(jsonPath("$.marcada").value(true));
        }

        @Test
        @DisplayName("Ranking y logros pasan el correo del JWT a sus puertos de entrada")
        void gamificacion() throws Exception {
            RankingUseCase ranking = mock(RankingUseCase.class);
            LogrosUseCase logros = mock(LogrosUseCase.class);
            when(ranking.top("semana")).thenReturn(List.of(new RankingResponse(1L, "Laura", null, 195L, 2)));
            when(logros.listar(LAURA)).thenReturn(List.of(LogroResponse.builder().codigo("PRIMER_ACIERTO").nombre("Primer paso").descripcion("Primera respuesta correcta").icono("rocket_launch").obtenido(true).build()));

            mvc(new RankingController(ranking)).perform(get("/api/ranking").param("periodo", "semana"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].puntosTotales").value(195));
            mvc(new LogrosController(logros)).perform(get("/api/logros").principal(AUTH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].codigo").value("PRIMER_ACIERTO"));
        }

        @Test
        @DisplayName("Mi avance: actividad, análisis y meta usan el correo del JWT; la meta se valida (5 a 300)")
        void miAvance() throws Exception {
            MiAvanceUseCase avance = mock(MiAvanceUseCase.class);
            MetaSemanalResponse meta = new MetaSemanalResponse(30, 12, 9, java.time.LocalDate.of(2026, 11, 2),
                    java.time.LocalDate.of(2026, 11, 8), 5, false);
            when(avance.actividad(LAURA, 126)).thenReturn(new ActividadResponse(List.of(), 0, 0, 0, null));
            when(avance.analisis(LAURA)).thenReturn(new AnalisisResponse(List.of(), List.of(), List.of(), null));
            when(avance.meta(LAURA)).thenReturn(meta);
            when(avance.cambiarMeta(LAURA, 30)).thenReturn(meta);
            MockMvc mvc = mvc(new MiAvanceController(avance));

            mvc.perform(get("/api/practica/actividad").principal(AUTH)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.diasActivos").value(0));
            mvc.perform(get("/api/practica/analisis").principal(AUTH)).andExpect(status().isOk());
            mvc.perform(get("/api/practica/meta").principal(AUTH)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.meta").value(30)).andExpect(jsonPath("$.diasRestantes").value(5));
            mvc.perform(put("/api/practica/meta").principal(AUTH).contentType(MediaType.APPLICATION_JSON).content("{\"meta\":30}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.respondidas").value(12));
            mvc.perform(put("/api/practica/meta").principal(AUTH).contentType(MediaType.APPLICATION_JSON).content("{\"meta\":2}"))
                    .andExpect(status().isBadRequest());
            verify(avance, never()).cambiarMeta(LAURA, 2);
        }

        @Test
        @DisplayName("Foto de perfil: devuelve la URL de S3")
        void fotoPerfil() throws Exception {
            SubirFotoPerfilService subir = mock(SubirFotoPerfilService.class);
            when(subir.ejecutar(eq(LAURA), any())).thenReturn("https://s3/yo.png");

            mvc(new PerfilController(subir)).perform(multipart("/api/perfil/foto")
                            .file(new MockMultipartFile("archivo", "yo.png", "image/png", new byte[]{1}))
                            .principal(AUTH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.url").value("https://s3/yo.png"));
        }
    }
}
