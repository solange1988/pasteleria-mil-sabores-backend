package cl.milsabores.backend.service;

import cl.milsabores.backend.model.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

// Reglas de descuento del caso Pastelería Mil Sabores (antes vivían en el frontend, en utils/descuentos.js):
//  - 50 años o más: 50% de descuento.
//  - Código FELICES50 al registrarse: 10% de por vida.
//  - Estudiantes Duoc (correo institucional): torta gratis el día de su cumpleaños.
// Los descuentos NO se acumulan: se aplica el mayor.
// Recibe la fecha "hoy" como parámetro para poder probarlo con cualquier fecha.
@Service
public class DescuentoService {

    public static final String CODIGO_PROMOCIONAL = "FELICES50";
    public static final String REGALO_CUMPLEANOS = "Torta de cumpleaños gratis (estudiante Duoc)";
    private static final List<String> DOMINIOS_DUOC = List.of("@duoc.cl", "@profesor.duoc.cl");

    public record Descuento(int porcentaje, String motivo) {
        public static final Descuento NINGUNO = new Descuento(0, "");
    }

    public Descuento descuentoPara(Usuario usuario, LocalDate hoy) {
        if (usuario == null) return Descuento.NINGUNO;
        if (usuario.getFechaNacimiento() != null && calcularEdad(usuario.getFechaNacimiento(), hoy) >= 50) {
            return new Descuento(50, "Descuento por tener 50 años o más");
        }
        if (usuario.getCodigo() != null && CODIGO_PROMOCIONAL.equalsIgnoreCase(usuario.getCodigo().trim())) {
            return new Descuento(10, "Código FELICES50 (10% de por vida)");
        }
        return Descuento.NINGUNO;
    }

    public boolean tieneTortaGratis(Usuario usuario, LocalDate hoy) {
        return usuario != null && esCorreoDuoc(usuario.getCorreo()) && esCumpleanos(usuario.getFechaNacimiento(), hoy);
    }

    public static int calcularEdad(LocalDate nacimiento, LocalDate hoy) {
        return Period.between(nacimiento, hoy).getYears();
    }

    static boolean esCumpleanos(LocalDate nacimiento, LocalDate hoy) {
        return nacimiento != null && nacimiento.getMonth() == hoy.getMonth()
                && nacimiento.getDayOfMonth() == hoy.getDayOfMonth();
    }

    static boolean esCorreoDuoc(String correo) {
        String texto = correo == null ? "" : correo.trim().toLowerCase();
        return DOMINIOS_DUOC.stream().anyMatch(texto::endsWith);
    }
}
