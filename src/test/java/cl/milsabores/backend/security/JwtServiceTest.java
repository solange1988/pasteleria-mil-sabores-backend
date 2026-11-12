package cl.milsabores.backend.security;

import cl.milsabores.backend.model.Rol;
import cl.milsabores.backend.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwt = new JwtService("clave-de-prueba-con-mas-de-32-caracteres-123", 30, 7);
    private final Usuario admin = new Usuario("Admin", "admin@duoc.cl", "x", Rol.ADMINISTRADOR);

    @Test
    void tokenValido_devuelveElCorreo() {
        String token = jwt.generarTokenAcceso(admin);
        assertEquals("admin@duoc.cl", jwt.correoSiEsValido(token, JwtService.TIPO_ACCESO));
    }

    @Test
    void tokenAlterado_esRechazado() {
        String token = jwt.generarTokenAcceso(admin);
        String alterado = token.substring(0, token.length() - 2) + "xx";
        assertNull(jwt.correoSiEsValido(alterado, JwtService.TIPO_ACCESO));
    }

    @Test
    void tokenDeRenovacion_noSirveComoTokenDeAcceso() {
        String renovacion = jwt.generarTokenRenovacion(admin);
        assertNull(jwt.correoSiEsValido(renovacion, JwtService.TIPO_ACCESO));
        assertEquals("admin@duoc.cl", jwt.correoSiEsValido(renovacion, JwtService.TIPO_RENOVACION));
    }

    @Test
    void tokenFirmadoConOtraClave_esRechazado() {
        JwtService otro = new JwtService("otra-clave-distinta-con-mas-de-32-caracteres", 30, 7);
        assertNull(jwt.correoSiEsValido(otro.generarTokenAcceso(admin), JwtService.TIPO_ACCESO));
    }

    @Test
    void tokenVencido_esRechazado() {
        JwtService vencido = new JwtService("clave-de-prueba-con-mas-de-32-caracteres-123", -1, 7);
        assertNull(jwt.correoSiEsValido(vencido.generarTokenAcceso(admin), JwtService.TIPO_ACCESO));
    }
}
