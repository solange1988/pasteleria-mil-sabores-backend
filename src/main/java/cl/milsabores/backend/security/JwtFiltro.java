package cl.milsabores.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Se ejecuta en cada petición, antes de llegar al controlador:
// 1. Lee el encabezado "Authorization: Bearer <token>".
// 2. Si el token es válido, busca al usuario en la base de datos (así un cambio de rol se aplica al tiro)
//    y lo deja como "usuario autenticado" de esta petición.
// 3. Si no hay token o no es válido, sigue como visitante: las rutas protegidas responderán 401.
// No lleva @Component a propósito: se crea en SecurityConfig para que corra solo dentro de Spring Security.
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtFiltro(JwtService jwtService, UsuarioDetailsService usuarioDetailsService) {
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String encabezado = request.getHeader("Authorization");
        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            String correo = jwtService.correoSiEsValido(encabezado.substring(7), JwtService.TIPO_ACCESO);
            if (correo != null) {
                try {
                    UserDetails usuario = usuarioDetailsService.loadUserByUsername(correo);
                    var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                } catch (UsernameNotFoundException e) {
                    // el usuario fue eliminado: se trata como visitante
                }
            }
        }
        chain.doFilter(request, response);
    }
}
