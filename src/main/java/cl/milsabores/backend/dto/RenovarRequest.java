package cl.milsabores.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RenovarRequest(@NotBlank(message = "El token de renovación es obligatorio") String tokenRenovacion) {
}
