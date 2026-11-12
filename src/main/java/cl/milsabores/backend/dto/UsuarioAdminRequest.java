package cl.milsabores.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// El administrador crea o edita usuarios de cualquier rol (por ejemplo, un Vendedor).
// Al editar, la contraseña es opcional: si viene vacía, se mantiene la actual.
public record UsuarioAdminRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        String contrasena,

        @NotBlank(message = "El rol es obligatorio")
        String rol,

        String fechaNacimiento,
        String codigo,
        String telefono,
        String region,
        String comuna) {
}
