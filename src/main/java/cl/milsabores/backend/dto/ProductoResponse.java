package cl.milsabores.backend.dto;

import cl.milsabores.backend.model.Producto;

// Misma forma que usaba el frontend: { id, nombre, categoria, precio, precioOferta, stock, imagen, descripcion }
public record ProductoResponse(
        Long id,
        String nombre,
        String categoria,
        Long categoriaId,
        Integer precio,
        Integer precioOferta,
        Integer stock,
        String imagen,
        String descripcion) {

    public static ProductoResponse de(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getCategoria().getNombre(), p.getCategoria().getId(),
                p.getPrecio(), p.getPrecioOferta(), p.getStock(), p.getImagen(), p.getDescripcion());
    }
}
