package cl.milsabores.backend.service;

import cl.milsabores.backend.model.Rol;
import cl.milsabores.backend.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas de las reglas de descuento. No necesitan base de datos ni Spring: es lógica pura.
class DescuentoServiceTest {

    private final DescuentoService servicio = new DescuentoService();
    private final LocalDate hoy = LocalDate.of(2026, 11, 12);

    private Usuario usuario(String correo, LocalDate nacimiento, String codigo) {
        Usuario u = new Usuario("Prueba", correo, "x", Rol.CLIENTE);
        u.setFechaNacimiento(nacimiento);
        u.setCodigo(codigo);
        return u;
    }

    @Test
    void sinUsuario_noHayDescuento() {
        assertEquals(0, servicio.descuentoPara(null, hoy).porcentaje());
    }

    @Test
    void mayorDe50_tiene50PorCiento() {
        Usuario u = usuario("ana@gmail.com", LocalDate.of(1970, 1, 1), null);
        assertEquals(50, servicio.descuentoPara(u, hoy).porcentaje());
    }

    @Test
    void cumple50Manana_todaviaNoTieneDescuento() {
        Usuario u = usuario("ana@gmail.com", LocalDate.of(1976, 11, 13), null);
        assertEquals(0, servicio.descuentoPara(u, hoy).porcentaje());
    }

    @Test
    void codigoFelices50_tiene10PorCiento() {
        Usuario u = usuario("ana@gmail.com", LocalDate.of(2000, 5, 5), "felices50");
        assertEquals(10, servicio.descuentoPara(u, hoy).porcentaje());
    }

    @Test
    void losDescuentosNoSeAcumulan_seAplicaElMayor() {
        Usuario u = usuario("ana@gmail.com", LocalDate.of(1960, 5, 5), "FELICES50");
        assertEquals(50, servicio.descuentoPara(u, hoy).porcentaje());
    }

    @Test
    void estudianteDuocDeCumpleanos_tieneTortaGratis() {
        Usuario u = usuario("sol@duocuc.cl", LocalDate.of(2004, 11, 12), null);
        assertFalse(servicio.tieneTortaGratis(u, hoy), "duocuc.cl no es un dominio Duoc válido para el beneficio");
        u.setCorreo("sol@duoc.cl");
        assertTrue(servicio.tieneTortaGratis(u, hoy));
    }

    @Test
    void estudianteDuocQueNoEstaDeCumpleanos_noTieneTortaGratis() {
        Usuario u = usuario("sol@duoc.cl", LocalDate.of(2004, 3, 1), null);
        assertFalse(servicio.tieneTortaGratis(u, hoy));
    }
}
