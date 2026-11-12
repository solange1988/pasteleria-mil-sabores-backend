package cl.milsabores.backend.security;

import cl.milsabores.backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

// Crea y verifica los tokens JWT.
// Un JWT tiene 3 partes: encabezado.contenido.firma. La firma se hace con una clave secreta que solo
// conoce el backend: si alguien cambia el contenido (por ejemplo, el rol), la firma deja de coincidir.
@Service
public class JwtService {

    public static final String TIPO_ACCESO = "acceso";
    public static final String TIPO_RENOVACION = "renovacion";

    private final SecretKey clave;
    private final Duration duracionAcceso;
    private final Duration duracionRenovacion;

    public JwtService(@Value("${jwt.secreto}") String secreto,
                      @Value("${jwt.minutos-acceso}") long minutosAcceso,
                      @Value("${jwt.dias-renovacion}") long diasRenovacion) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionAcceso = Duration.ofMinutes(minutosAcceso);
        this.duracionRenovacion = Duration.ofDays(diasRenovacion);
    }

    // Token de acceso: se envía en cada petición. Dura poco (30 minutos).
    public String generarTokenAcceso(Usuario usuario) {
        return construir(usuario, TIPO_ACCESO, duracionAcceso);
    }

    // Token de renovación: solo sirve para pedir un token de acceso nuevo. Dura más (7 días).
    public String generarTokenRenovacion(Usuario usuario) {
        return construir(usuario, TIPO_RENOVACION, duracionRenovacion);
    }

    private String construir(Usuario usuario, String tipo, Duration duracion) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("rol", usuario.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(duracion)))
                .signWith(clave)
                .compact();
    }

    // Devuelve el correo del token si la firma es válida, no expiró y es del tipo pedido; si no, null
    public String correoSiEsValido(String token, String tipoEsperado) {
        try {
            Claims datos = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            return tipoEsperado.equals(datos.get("tipo", String.class)) ? datos.getSubject() : null;
        } catch (JwtException | IllegalArgumentException e) {
            return null; // firma alterada, token vencido o mal formado
        }
    }

    public long segundosAcceso() {
        return duracionAcceso.toSeconds();
    }
}
