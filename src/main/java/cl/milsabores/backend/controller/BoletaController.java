package cl.milsabores.backend.controller;

import cl.milsabores.backend.dto.BoletaRequest;
import cl.milsabores.backend.dto.BoletaResponse;
import cl.milsabores.backend.dto.EstadoRequest;
import cl.milsabores.backend.security.UsuarioActual;
import cl.milsabores.backend.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "Boletas", description = "Órdenes de compra con su detalle")
public class BoletaController {

    private final BoletaService boletaService;
    private final UsuarioActual usuarioActual;

    public BoletaController(BoletaService boletaService, UsuarioActual usuarioActual) {
        this.boletaService = boletaService;
        this.usuarioActual = usuarioActual;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Comprar (público)",
            description = "Crea la boleta. Si hay token, se asocia al usuario y aplica su descuento. "
                    + "Si falta stock, la boleta queda como 'Pago rechazado' y 'faltantes' indica los productos.")
    public BoletaResponse crear(@Valid @RequestBody BoletaRequest datos) {
        return boletaService.crear(datos, usuarioActual.buscar().orElse(null));
    }

    @GetMapping("/mias")
    @Operation(summary = "Mis compras (cualquier usuario con sesión)")
    public List<BoletaResponse> mias() {
        return boletaService.listarDe(usuarioActual.obtener());
    }

    @GetMapping
    @Operation(summary = "Listar todas las boletas (Administrador y Vendedor)")
    public List<BoletaResponse> listar() {
        return boletaService.listar();
    }

    @GetMapping("/{numero}")
    @Operation(summary = "Detalle de una boleta (Administrador y Vendedor)")
    public BoletaResponse obtener(@PathVariable Long numero) {
        return boletaService.obtener(numero);
    }

    @PatchMapping("/{numero}/estado")
    @Operation(summary = "Cambiar el estado de una boleta (Administrador)",
            description = "Estados: Pagada, En preparación, Enviada, Entregada, Pago rechazado")
    public BoletaResponse cambiarEstado(@PathVariable Long numero, @Valid @RequestBody EstadoRequest datos) {
        return boletaService.cambiarEstado(numero, datos.estado());
    }

    @DeleteMapping("/{numero}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una boleta (Administrador)")
    public void eliminar(@PathVariable Long numero) {
        boletaService.eliminar(numero);
    }
}
