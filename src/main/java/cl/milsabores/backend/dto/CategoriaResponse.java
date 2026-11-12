package cl.milsabores.backend.dto;

import cl.milsabores.backend.model.Categoria;

public record CategoriaResponse(Long id, String nombre) {

    public static CategoriaResponse de(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre());
    }
}
