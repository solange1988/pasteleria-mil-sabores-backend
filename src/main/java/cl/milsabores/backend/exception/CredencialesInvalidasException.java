package cl.milsabores.backend.exception;

// Correo o contraseña incorrectos, o token de renovación inválido -> 401
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
