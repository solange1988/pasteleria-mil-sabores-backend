package cl.milsabores.backend.exception;

// Se lanza cuando se pide algo que no existe -> 404
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
