package cl.milsabores.backend.controller;

import cl.milsabores.backend.dto.AuthResponse;
import cl.milsabores.backend.dto.LoginRequest;
import cl.milsabores.backend.dto.RegistroRequest;
import cl.milsabores.backend.dto.RenovarRequest;
import cl.milsabores.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y renovación del token JWT")
@SecurityRequirements // estos endpoints no necesitan token
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un cliente nuevo", description = "Crea un usuario con rol Cliente y devuelve sus tokens")
    public AuthResponse registrar(@Valid @RequestBody RegistroRequest datos) {
        return authService.registrar(datos);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve el token de acceso (30 min), el de renovación (7 días) y el usuario")
    public AuthResponse login(@Valid @RequestBody LoginRequest datos) {
        return authService.login(datos);
    }

    @PostMapping("/renovar")
    @Operation(summary = "Renovar el token", description = "Con el token de renovación entrega un token de acceso nuevo")
    public AuthResponse renovar(@Valid @RequestBody RenovarRequest datos) {
        return authService.renovar(datos.tokenRenovacion());
    }
}
