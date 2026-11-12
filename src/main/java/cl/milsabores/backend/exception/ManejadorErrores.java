package cl.milsabores.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

// Convierte las excepciones en respuestas HTTP con el código correcto y un mensaje en JSON.
// Así ningún controlador necesita try/catch.
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> noEncontrado(RecursoNoEncontradoException ex) {
        return error(ex.getMessage());
    }

    @ExceptionHandler({ReglaNegocioException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> reglaNegocio(RuntimeException ex) {
        return error(ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> credenciales(CredencialesInvalidasException ex) {
        return error(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> sinPermiso(AccessDeniedException ex) {
        return Map.of("error", "No tienes permisos para esta acción");
    }

    private static Map<String, String> error(String mensaje) {
        return Map.of("error", mensaje == null ? "Solicitud inválida" : mensaje);
    }

    // @Valid falló: devuelve el error de cada campo, por ejemplo {"nombre": "El nombre es obligatorio"}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return errores;
    }

    // El cuerpo de la petición no es un JSON válido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> jsonInvalido(HttpMessageNotReadableException ex) {
        return Map.of("error", "El cuerpo de la petición no es un JSON válido");
    }
}
