package cl.milsabores.backend.service;

import cl.milsabores.backend.exception.ReglaNegocioException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

// Reglas de validación que se repiten en registro, perfil y administración de usuarios.
// Son las mismas del frontend: el backend las vuelve a revisar porque no puede confiar en el navegador.
final class Validaciones {

    private static final Pattern DOMINIOS_PERMITIDOS =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$");

    private Validaciones() {
    }

    static String correoPermitido(String correo) {
        String texto = correo.trim().toLowerCase();
        if (!DOMINIOS_PERMITIDOS.matcher(texto).matches()) {
            throw new ReglaNegocioException("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com");
        }
        return texto;
    }

    static void contrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 4 || contrasena.length() > 10) {
            throw new ReglaNegocioException("La contraseña debe tener entre 4 y 10 caracteres");
        }
    }

    // "" -> null. Si viene, debe ser una fecha real (aaaa-mm-dd), no futura y desde 1900.
    static LocalDate fechaNacimiento(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            LocalDate fecha = LocalDate.parse(texto.trim());
            if (fecha.isAfter(LocalDate.now()) || fecha.getYear() < 1900) {
                throw new ReglaNegocioException("La fecha de nacimiento no es válida");
            }
            return fecha;
        } catch (DateTimeException e) {
            throw new ReglaNegocioException("La fecha de nacimiento no es válida");
        }
    }

    // "" -> null. Si viene, solo existe el código FELICES50.
    static String codigo(String texto) {
        if (texto == null || texto.isBlank()) return null;
        if (!DescuentoService.CODIGO_PROMOCIONAL.equalsIgnoreCase(texto.trim())) {
            throw new ReglaNegocioException("El código promocional no es válido");
        }
        return DescuentoService.CODIGO_PROMOCIONAL;
    }

    static String opcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
