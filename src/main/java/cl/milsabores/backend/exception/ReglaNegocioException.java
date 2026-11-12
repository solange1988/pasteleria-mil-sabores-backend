package cl.milsabores.backend.exception;

// Se lanza cuando los datos son correctos en forma pero rompen una regla del negocio
// (correo repetido, código promocional inválido, producto con ventas...) -> 400
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
