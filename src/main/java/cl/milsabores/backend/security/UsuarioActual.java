package cl.milsabores.backend.security;

import cl.milsabores.backend.exception.CredencialesInvalidasException;
import cl.milsabores.backend.model.Usuario;
import cl.milsabores.backend.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Ayuda a los servicios a saber quién hizo la petición (según el token que validó JwtFiltro)
@Component
public class UsuarioActual {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActual(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Vacío si la petición viene de un visitante sin sesión
    public Optional<Usuario> buscar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return usuarioRepository.findByCorreoIgnoreCase(auth.getName());
    }

    public Usuario obtener() {
        return buscar().orElseThrow(() -> new CredencialesInvalidasException("Debes iniciar sesión"));
    }
}
