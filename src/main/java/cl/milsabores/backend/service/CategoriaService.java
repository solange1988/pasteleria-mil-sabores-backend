package cl.milsabores.backend.service;

import cl.milsabores.backend.dto.CategoriaRequest;
import cl.milsabores.backend.dto.CategoriaResponse;
import cl.milsabores.backend.exception.RecursoNoEncontradoException;
import cl.milsabores.backend.exception.ReglaNegocioException;
import cl.milsabores.backend.model.Categoria;
import cl.milsabores.backend.repository.CategoriaRepository;
import cl.milsabores.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return CategoriaResponse.de(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest datos) {
        validarNombreUnico(datos.nombre(), null);
        return CategoriaResponse.de(categoriaRepository.save(new Categoria(datos.nombre().trim())));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest datos) {
        Categoria categoria = buscar(id);
        validarNombreUnico(datos.nombre(), id);
        categoria.setNombre(datos.nombre().trim());
        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    // Regla: no se elimina una categoría que todavía tiene productos
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos");
        }
        categoriaRepository.delete(categoria);
    }

    // Busca la categoría por nombre y, si no existe, la crea (la usa ProductoService)
    @Transactional
    public Categoria buscarOCrear(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre.trim())
                .orElseGet(() -> categoriaRepository.save(new Categoria(nombre.trim())));
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría " + id));
    }

    private void validarNombreUnico(String nombre, Long idActual) {
        categoriaRepository.findByNombreIgnoreCase(nombre.trim())
                .filter(c -> !c.getId().equals(idActual))
                .ifPresent(c -> {
                    throw new ReglaNegocioException("Ya existe la categoría " + c.getNombre());
                });
    }
}
