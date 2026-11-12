package cl.milsabores.backend.dto;

// Respuesta del login: el token de acceso (corto), el de renovación (largo) y los datos del usuario
public record AuthResponse(String token, String tokenRenovacion, UsuarioResponse usuario) {
}
