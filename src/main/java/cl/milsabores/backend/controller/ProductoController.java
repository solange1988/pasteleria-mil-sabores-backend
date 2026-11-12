package cl.milsabores.backend.controller;

import cl.milsabores.backend.dto.ProductoRequest;
import cl.milsabores.backend.dto.ProductoResponse;
import cl.milsabores.backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Catálogo de la tienda. Lectura pública; modificar solo Administrador")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar productos (público)", description = "Opcional: ?categoriaId=1 filtra por categoría")
    public List<ProductoResponse> listar(@RequestParam(required = false) Long categoriaId) {
        return productoService.listar(categoriaId);
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Listar productos en oferta (público)")
    public List<ProductoResponse> ofertas() {
        return productoService.listarOfertas();
    }

    @GetMapping("/criticos")
    @Operation(summary = "Productos con stock crítico, 5 o menos (Administrador y Vendedor)")
    public List<ProductoResponse> criticos() {
        return productoService.listarCriticos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de un producto (público)")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear producto (Administrador)")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest datos) {
        return productoService.crear(datos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto (Administrador)")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest datos) {
        return productoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar producto sin ventas (Administrador)")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
