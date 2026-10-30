package co.edu.ucundinamarca.saesu.testutil;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JdbcTemplate falso para las pruebas unitarias de los adaptadores JDBC.
 *
 * No hay base de datos: cada consulta devuelve las filas registradas con {@link #filas(String, List)}
 * (se elige la primera cuyo fragmento aparezca en el SQL) y las filas se entregan al RowMapper
 * del adaptador a través de un ResultSet falso. Así se prueba el mapeo y la lógica del adaptador
 * (agrupar, ordenar, convertir fechas...) sin depender de PostgreSQL. Las consultas reales se
 * prueban en las pruebas de integración.
 */
public class JdbcFalso extends JdbcTemplate {

    /** Una llamada hecha por el adaptador: el SQL y sus parámetros. */
    public record Llamada(String sql, Object[] args) {}

    private final Map<String, List<Map<String, Object>>> respuestas = new LinkedHashMap<>();
    public final List<Llamada> consultas = new ArrayList<>();
    public final List<Llamada> actualizaciones = new ArrayList<>();
    public final List<List<Object[]>> lotes = new ArrayList<>();
    /** Lo que devuelve update(...): filas afectadas. */
    public int filasAfectadas = 1;
    /** Llave generada en los INSERT con KeyHolder. */
    public long llaveGenerada = 99L;

    /** Registra las filas que devuelve cualquier consulta cuyo SQL contenga {@code fragmento}. */
    public JdbcFalso filas(String fragmento, List<Map<String, Object>> filas) {
        respuestas.put(fragmento, filas);
        return this;
    }

    private List<Map<String, Object>> buscar(String sql, Object[] args) {
        consultas.add(new Llamada(sql, args));
        for (Map.Entry<String, List<Map<String, Object>>> e : respuestas.entrySet()) {
            if (sql.contains(e.getKey())) return e.getValue();
        }
        return List.of();
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        List<Map<String, Object>> filas = buscar(sql, args);
        List<T> res = new ArrayList<>();
        try {
            for (int i = 0; i < filas.size(); i++) res.add(rowMapper.mapRow(resultSet(List.of(filas.get(i)), true), i));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return res;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return query(sql, rowMapper, new Object[0]);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch, Object... args) {
        try {
            for (Map<String, Object> f : buscar(sql, args)) rch.processRow(resultSet(List.of(f), true));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void query(String sql, RowCallbackHandler rch) {
        query(sql, rch, new Object[0]);
    }

    /** Lo usa NamedParameterJdbcTemplate (consultas con ":nombre"); el SQL sale del SqlProvider. */
    @Override
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) {
        String sql = psc instanceof SqlProvider p ? p.getSql() : "";
        try {
            for (Map<String, Object> f : buscar(sql, new Object[0])) rch.processRow(resultSet(List.of(f), true));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) {
        try {
            return rse.extractData(resultSet(buscar(sql, args), false));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        List<T> r = query(sql, rowMapper, args);
        if (r.isEmpty()) throw new EmptyResultDataAccessException(1);
        return r.get(0);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
        List<Map<String, Object>> filas = buscar(sql, args);
        if (filas.isEmpty()) throw new EmptyResultDataAccessException(1);
        Object v = filas.get(0).values().iterator().next();
        return convertir(v, requiredType);
    }

    @Override
    public int update(String sql, Object... args) {
        actualizaciones.add(new Llamada(sql, args));
        return filasAfectadas;
    }

    @Override
    public int update(String sql) {
        return update(sql, new Object[0]);
    }

    @Override
    public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) {
        try {
            psc.createPreparedStatement(conexion());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        actualizaciones.add(new Llamada("INSERT", new Object[0]));
        generatedKeyHolder.getKeyList().add(Map.of("id", llaveGenerada));
        return 1;
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        actualizaciones.add(new Llamada(sql, new Object[0]));
        lotes.add(batchArgs);
        return new int[batchArgs.size()];
    }

    /** Fila para usar con {@link #filas}: fila("id", 1L, "nombre", "Laura"). */
    public static Map<String, Object> fila(Object... claveValor) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < claveValor.length; i += 2) m.put((String) claveValor[i], claveValor[i + 1]);
        return m;
    }

    // ------------------------------------------------------------------

    private static Connection conexion() {
        PreparedStatement ps = (PreparedStatement) Proxy.newProxyInstance(JdbcFalso.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, (p, m, a) -> valorPorDefecto(m.getReturnType()));
        return (Connection) Proxy.newProxyInstance(JdbcFalso.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (p, m, a) -> m.getName().equals("prepareStatement") ? ps : valorPorDefecto(m.getReturnType()));
    }

    /** ResultSet falso sobre las filas dadas. Si {@code posicionado}, ya está en la primera fila (como en un RowMapper). */
    static ResultSet resultSet(List<Map<String, Object>> filas, boolean posicionado) {
        int[] cursor = {posicionado ? 0 : -1};
        boolean[] ultimoNulo = {false};
        return (ResultSet) Proxy.newProxyInstance(JdbcFalso.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                (proxy, m, a) -> {
                    switch (m.getName()) {
                        case "next": return ++cursor[0] < filas.size();
                        case "wasNull": return ultimoNulo[0];
                        case "close": return null;
                        default: break;
                    }
                    if (m.getName().startsWith("get") && a != null && a.length >= 1) {
                        Map<String, Object> fila = filas.get(cursor[0]);
                        Object v = a[0] instanceof Integer idx ? new ArrayList<>(fila.values()).get(idx - 1) : fila.get((String) a[0]);
                        ultimoNulo[0] = v == null;
                        Class<?> tipo = m.getName().equals("getObject") && a.length == 2 ? (Class<?>) a[1] : m.getReturnType();
                        if (m.getName().equals("getObject") && a.length == 1) return v;
                        return convertir(v, tipo);
                    }
                    return valorPorDefecto(m.getReturnType());
                });
    }

    @SuppressWarnings("unchecked")
    static <T> T convertir(Object v, Class<T> tipo) {
        if (v == null) return (T) valorPorDefecto(tipo);
        if (tipo.isInstance(v)) return (T) v;
        if (tipo == long.class || tipo == Long.class) return (T) Long.valueOf(((Number) v).longValue());
        if (tipo == int.class || tipo == Integer.class) return (T) Integer.valueOf(((Number) v).intValue());
        if (tipo == double.class || tipo == Double.class) return (T) Double.valueOf(((Number) v).doubleValue());
        if (tipo == BigDecimal.class) return (T) new BigDecimal(v.toString());
        if (tipo == boolean.class || tipo == Boolean.class) return (T) v;
        if (tipo == String.class) return (T) v.toString();
        LocalDateTime ldt = v instanceof LocalDateTime l ? l : v instanceof Instant i ? LocalDateTime.ofInstant(i, ZoneOffset.UTC)
                : v instanceof OffsetDateTime o ? o.toLocalDateTime() : v instanceof LocalDate d ? d.atStartOfDay() : null;
        if (ldt != null) {
            if (tipo == Timestamp.class) return (T) Timestamp.valueOf(ldt);
            if (tipo == Date.class) return (T) Date.valueOf(ldt.toLocalDate());
            if (tipo == LocalDate.class) return (T) ldt.toLocalDate();
            if (tipo == LocalDateTime.class) return (T) ldt;
            if (tipo == OffsetDateTime.class) return (T) ldt.atOffset(ZoneOffset.UTC);
            if (tipo == Instant.class) return (T) ldt.toInstant(ZoneOffset.UTC);
        }
        return (T) v;
    }

    private static Object valorPorDefecto(Class<?> tipo) {
        if (tipo == long.class) return 0L;
        if (tipo == int.class) return 0;
        if (tipo == double.class) return 0.0;
        if (tipo == float.class) return 0f;
        if (tipo == short.class) return (short) 0;
        if (tipo == byte.class) return (byte) 0;
        if (tipo == boolean.class) return false;
        return null;
    }
}
