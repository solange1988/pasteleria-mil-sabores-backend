package cl.milsabores.backend.service;

import cl.milsabores.backend.dto.PerfilRequest;
import cl.milsabores.backend.dto.UsuarioAdminRequest;
import cl.milsabores.backend.dto.UsuarioResponse;
import cl.milsabores.backend.exception.RecursoNoEncontradoException;
import cl.milsabores.backend.exception.ReglaNegocioException;
import cl.milsabores.backend.model.Rol;
import cl.milsabores.backend.model.Usuario;
import cl.milsabores.backend.repository.BoletaRepository;
import cl.milsabores.backend.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BoletaRepository boletaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BoletaRepository boletaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.boletaRepository = boletaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- Administración (solo rol Administrador) ----------

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by("id")).stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.de(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioAdminRequest datos) {
        String correo = Validaciones.correoPermitido(datos.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ese correo ya está registrado");
        }
        Validaciones.contrasena(datos.contrasena());
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(datos.contrasena()));
        copiar(datos, usuario);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    // El correo no cambia (identifica a la persona). La contraseña solo cambia si viene escrita.
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioAdminRequest datos, Usuario quienEdita) {
        Usuario usuario = buscar(id);
        Rol rolNuevo = Rol.desdeTexto(datos.rol());
        if (usuario.getId().equals(quienEdita.getId()) && rolNuevo != Rol.ADMINISTRADOR) {
            throw new ReglaNegocioException("No puedes quitarte a ti mismo el rol de Administrador");
        }
        if (datos.contrasena() != null && !datos.contrasena().isBlank()) {
            Validaciones.contrasena(datos.contrasena());
            usuario.setContrasena(passwordEncoder.encode(datos.contrasena()));
        }
        copiar(datos, usuario);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    // Las boletas del usuario se conservan (quedan como compra sin cuenta, con su correo)
    @Transactional
    public void eliminar(Long id, Usuario quienElimina) {
        Usuario usuario = buscar(id);
        if (usuario.getId().equals(quienElimina.getId())) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta");
        }
        boletaRepository.findByUsuarioIdOrderByIdAsc(id).forEach(b -> b.setUsuario(null));
        usuarioRepository.delete(usuario);
    }

    // ---------- Perfil (cualquier usuario con sesión, sobre sí mismo) ----------

    @Transactional
    public UsuarioResponse actualizarPerfil(Usuario usuario, PerfilRequest datos) {
        usuario.setNombre(datos.nombre().trim());
        usuario.setFechaNacimiento(Validaciones.fechaNacimiento(datos.fechaNacimiento()));
        usuario.setCodigo(Validaciones.codigo(datos.codigo()));
        usuario.setTelefono(Validaciones.opcional(datos.telefono()));
        usuario.setRegion(Validaciones.opcional(datos.region()));
        usuario.setComuna(Validaciones.opcional(datos.comuna()));
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id));
    }

    private void copiar(UsuarioAdminRequest datos, Usuario usuario) {
        usuario.setNombre(datos.nombre().trim());
        usuario.setRol(Rol.desdeTexto(datos.rol()));
        usuario.setFechaNacimiento(Validaciones.fechaNacimiento(datos.fechaNacimiento()));
        usuario.setCodigo(Validaciones.codigo(datos.codigo()));
        usuario.setTelefono(Validaciones.opcional(datos.telefono()));
        usuario.setRegion(Validaciones.opcional(datos.region()));
        usuario.setComuna(Validaciones.opcional(datos.comuna()));
    }
}
