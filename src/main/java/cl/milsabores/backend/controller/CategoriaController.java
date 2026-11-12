package cl.milsabores.backend.controller;

import cl.milsabores.backend.dto.CategoriaRequest;
import cl.milsabores.backend.dto.CategoriaResponse;
import cl.milsabores.backend.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Lectura pública; crear, editar y eliminar solo Administrador")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar categorías (público)")
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoría (público)")
    public CategoriaResponse obtener(@PathVariable Long id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear categoría (Administrador)")
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest datos) {
        return categoriaService.crear(datos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría (Administrador)")
    public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest datos) {
        return categoriaService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar categoría sin productos (Administrador)")
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }
}
