package cl.milsabores.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Lo que el propio usuario puede cambiar de su perfil (no puede cambiar su correo ni su rol)
public record PerfilRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,
        String fechaNacimiento,
        String codigo,
        String telefono,
        String region,
        String comuna) {
}
