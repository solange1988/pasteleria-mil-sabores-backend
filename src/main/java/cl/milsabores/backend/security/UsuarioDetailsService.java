package cl.milsabores.backend.security;

import cl.milsabores.backend.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// Le dice a Spring Security cómo buscar un usuario y qué rol tiene.
// El rol se convierte en la autoridad "ROLE_ADMINISTRADOR", "ROLE_VENDEDOR" o "ROLE_CLIENTE".
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .map(u -> new User(u.getCorreo(), u.getContrasena(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name()))))
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + correo));
    }
}
