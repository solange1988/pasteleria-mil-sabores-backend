package cl.milsabores.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Una línea del carrito: qué producto y cuántas unidades. El precio NO viene del frontend:
// lo pone el backend desde la base de datos, para que nadie pueda cambiarlo.
public record LineaRequest(
        @NotNull(message = "El producto es obligatorio") Long productoId,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
}
