package cl.milsabores.backend.service;

import cl.milsabores.backend.dto.AuthResponse;
import cl.milsabores.backend.dto.LoginRequest;
import cl.milsabores.backend.dto.RegistroRequest;
import cl.milsabores.backend.dto.UsuarioResponse;
import cl.milsabores.backend.exception.CredencialesInvalidasException;
import cl.milsabores.backend.exception.ReglaNegocioException;
import cl.milsabores.backend.model.Rol;
import cl.milsabores.backend.model.Usuario;
import cl.milsabores.backend.repository.UsuarioRepository;
import cl.milsabores.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Registro, inicio de sesión y renovación del token
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // El registro público siempre crea un Cliente: nadie puede registrarse solo como Administrador
    @Transactional
    public AuthResponse registrar(RegistroRequest datos) {
        String correo = Validaciones.correoPermitido(datos.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ese correo ya está registrado");
        }
        Usuario usuario = new Usuario(datos.nombre().trim(), correo, passwordEncoder.encode(datos.contrasena()), Rol.CLIENTE);
        usuario.setFechaNacimiento(Validaciones.fechaNacimiento(datos.fechaNacimiento()));
        usuario.setCodigo(Validaciones.codigo(datos.codigo()));
        usuario.setTelefono(Validaciones.opcional(datos.telefono()));
        usuario.setRegion(Validaciones.opcional(datos.region()));
        usuario.setComuna(Validaciones.opcional(datos.comuna()));
        return respuestaCon(usuarioRepository.save(usuario));
    }

    // Mismo mensaje si el correo no existe o si la contraseña está mal: así no se revela qué correos existen
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(datos.correo().trim())
                .filter(u -> passwordEncoder.matches(datos.contrasena(), u.getContrasena()))
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));
        return respuestaCon(usuario);
    }

    // Con un token de renovación válido se entrega un token de acceso nuevo (y uno de renovación nuevo)
    @Transactional(readOnly = true)
    public AuthResponse renovar(String tokenRenovacion) {
        String correo = jwtService.correoSiEsValido(tokenRenovacion, JwtService.TIPO_RENOVACION);
        if (correo == null) {
            throw new CredencialesInvalidasException("La sesión expiró. Inicia sesión nuevamente.");
        }
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new CredencialesInvalidasException("La sesión expiró. Inicia sesión nuevamente."));
        return respuestaCon(usuario);
    }

    private AuthResponse respuestaCon(Usuario usuario) {
        return new AuthResponse(jwtService.generarTokenAcceso(usuario), jwtService.generarTokenRenovacion(usuario),
                UsuarioResponse.de(usuario));
    }
}
