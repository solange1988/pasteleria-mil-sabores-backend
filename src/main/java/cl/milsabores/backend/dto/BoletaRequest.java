package cl.milsabores.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BoletaRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String cliente,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
        String direccion,

        @NotEmpty(message = "El carrito está vacío")
        List<@Valid LineaRequest> lineas) {
}
