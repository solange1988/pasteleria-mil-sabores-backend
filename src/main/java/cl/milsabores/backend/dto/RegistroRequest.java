package cl.milsabores.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Registro público: siempre crea un usuario con rol Cliente
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
        String contrasena,

        String fechaNacimiento,
        String codigo,
        String telefono,
        String region,
        String comuna) {
}
