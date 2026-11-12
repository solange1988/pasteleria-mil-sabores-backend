package cl.milsabores.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Reglas de seguridad: quién puede llamar a cada endpoint.
// Las reglas se revisan de arriba hacia abajo y gana la primera que coincide.
@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String VENDEDOR = "VENDEDOR";

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService,
                                       UsuarioDetailsService usuarioDetailsService) throws Exception {
        JwtFiltro jwtFiltro = new JwtFiltro(jwtService, usuarioDetailsService);
        http
                // API sin sesión de servidor: cada petición trae su token, por eso no se usa CSRF
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        // Públicos: documentación, login y registro
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/registro",
                                "/api/v1/auth/renovar").permitAll()

                        // Productos y categorías: la tienda los muestra a todos; solo el admin los modifica
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/criticos").hasAnyRole(ADMIN, VENDEDOR)
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**").permitAll()
                        .requestMatchers("/api/v1/productos/**", "/api/v1/categorias/**").hasRole(ADMIN)

                        // Boletas: cualquiera puede comprar; cada usuario ve las suyas;
                        // admin y vendedor ven todas; solo el admin las cambia o elimina
                        .requestMatchers(HttpMethod.POST, "/api/v1/boletas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/mias").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/**").hasAnyRole(ADMIN, VENDEDOR)
                        .requestMatchers("/api/v1/boletas/**").hasRole(ADMIN)

                        // Usuarios: cada uno edita su perfil; el resto es solo del admin
                        .requestMatchers("/api/v1/usuarios/perfil").authenticated()
                        .requestMatchers("/api/v1/usuarios/**").hasRole(ADMIN)

                        .anyRequest().authenticated())
                // Respuestas en JSON cuando falta el token (401) o el rol no alcanza (403)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                responderJson(res, 401, "Debes iniciar sesión (token ausente, inválido o vencido)"))
                        .accessDeniedHandler((req, res, ex) ->
                                responderJson(res, 403, "No tienes permisos para esta acción")))
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static void responderJson(jakarta.servlet.http.HttpServletResponse res, int estado, String mensaje)
            throws java.io.IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }

    // BCrypt: cifra las contraseñas con "sal" aleatoria. No se pueden descifrar, solo comparar.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS: permite que el frontend (otro puerto) llame a la API desde el navegador
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origenes}") List<String> origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", config);
        return fuente;
    }
}
