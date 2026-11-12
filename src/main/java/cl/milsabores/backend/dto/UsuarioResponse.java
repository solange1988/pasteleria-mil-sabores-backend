package cl.milsabores.backend.dto;

import cl.milsabores.backend.model.Usuario;

// Lo que la API muestra de un usuario. Nunca incluye la contraseña.
public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String rol,
        String fechaNacimiento,
        String codigo,
        String telefono,
        String region,
        String comuna) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol().getEtiqueta(),
                u.getFechaNacimiento() == null ? "" : u.getFechaNacimiento().toString(),
                vacio(u.getCodigo()), vacio(u.getTelefono()), vacio(u.getRegion()), vacio(u.getComuna()));
    }

    private static String vacio(String texto) {
        return texto == null ? "" : texto;
    }
}
