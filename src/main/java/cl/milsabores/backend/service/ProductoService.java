package cl.milsabores.backend.service;

import cl.milsabores.backend.dto.ProductoRequest;
import cl.milsabores.backend.dto.ProductoResponse;
import cl.milsabores.backend.exception.RecursoNoEncontradoException;
import cl.milsabores.backend.exception.ReglaNegocioException;
import cl.milsabores.backend.model.Producto;
import cl.milsabores.backend.repository.DetalleBoletaRepository;
import cl.milsabores.backend.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    // Stock igual o menor a este número se considera crítico
    public static final int STOCK_CRITICO = 5;

    private final ProductoRepository productoRepository;
    private final DetalleBoletaRepository detalleBoletaRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, DetalleBoletaRepository detalleBoletaRepository,
                           CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.detalleBoletaRepository = detalleBoletaRepository;
        this.categoriaService = categoriaService;
    }

    // Si viene categoriaId, filtra por esa categoría
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(Long categoriaId) {
        List<Producto> productos = categoriaId == null
                ? productoRepository.findAll(Sort.by("id"))
                : productoRepository.findByCategoriaIdOrderByIdAsc(categoriaId);
        return productos.stream().map(ProductoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarOfertas() {
        return productoRepository.findAll(Sort.by("id")).stream()
                .filter(Producto::enOferta)
                .map(ProductoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarCriticos() {
        return productoRepository.findByStockLessThanEqualOrderByStockAsc(STOCK_CRITICO).stream()
                .map(ProductoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.de(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest datos) {
        Producto producto = new Producto();
        copiar(datos, producto);
        return ProductoResponse.de(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest datos) {
        Producto producto = buscar(id);
        copiar(datos, producto);
        return ProductoResponse.de(productoRepository.save(producto));
    }

    // Regla: un producto que ya se vendió no se elimina (las boletas lo necesitan); se deja con stock 0
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscar(id);
        if (detalleBoletaRepository.existsByProductoId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar: el producto aparece en boletas. Puedes dejar su stock en 0.");
        }
        productoRepository.delete(producto);
    }

    Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + id));
    }

    private void copiar(ProductoRequest datos, Producto producto) {
        Integer oferta = datos.precioOferta() == null || datos.precioOferta() == 0 ? null : datos.precioOferta();
        if (oferta != null && oferta >= datos.precio()) {
            throw new ReglaNegocioException("La oferta debe ser menor al precio normal");
        }
        producto.setNombre(datos.nombre().trim());
        producto.setCategoria(categoriaService.buscarOCrear(datos.categoria()));
        producto.setPrecio(datos.precio());
        producto.setPrecioOferta(oferta);
        producto.setStock(datos.stock());
        producto.setImagen(datos.imagen());
        producto.setDescripcion(datos.descripcion());
    }
}
