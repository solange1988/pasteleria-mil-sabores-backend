package cl.milsabores.backend.controller;

import cl.milsabores.backend.dto.PerfilRequest;
import cl.milsabores.backend.dto.UsuarioAdminRequest;
import cl.milsabores.backend.dto.UsuarioResponse;
import cl.milsabores.backend.security.UsuarioActual;
import cl.milsabores.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Perfil propio (con sesión) y administración de usuarios (Administrador)")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioActual usuarioActual;

    public UsuarioController(UsuarioService usuarioService, UsuarioActual usuarioActual) {
        this.usuarioService = usuarioService;
        this.usuarioActual = usuarioActual;
    }

    // ---------- Perfil propio ----------

    @GetMapping("/perfil")
    @Operation(summary = "Ver mi perfil (con sesión)", description = "El frontend lo usa al recargar la página para validar la sesión")
    public UsuarioResponse miPerfil() {
        return UsuarioResponse.de(usuarioActual.obtener());
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar mi perfil (con sesión)")
    public UsuarioResponse actualizarPerfil(@Valid @RequestBody PerfilRequest datos) {
        return usuarioService.actualizarPerfil(usuarioActual.obtener(), datos);
    }

    // ---------- Administración ----------

    @GetMapping
    @Operation(summary = "Listar usuarios (Administrador)")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario (Administrador)")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario con cualquier rol, por ejemplo un Vendedor (Administrador)")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioAdminRequest datos) {
        return usuarioService.crear(datos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario y su rol (Administrador)", description = "Si la contraseña va vacía, se mantiene")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioAdminRequest datos) {
        return usuarioService.actualizar(id, datos, usuarioActual.obtener());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario (Administrador)")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id, usuarioActual.obtener());
    }
}
