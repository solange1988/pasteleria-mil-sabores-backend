package cl.milsabores.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Datos para crear o editar un producto. La categoría se indica por su nombre,
// igual que en el formulario del panel de administración.
public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "La categoría es obligatoria")
        String categoria,

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 1, message = "El precio debe ser mayor que 0")
        Integer precio,

        @Min(value = 0, message = "El precio de oferta no puede ser negativo")
        Integer precioOferta,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Size(max = 200, message = "La ruta de la imagen no puede superar los 200 caracteres")
        String imagen,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion) {
}
