package cl.milsabores.backend.service;

import cl.milsabores.backend.dto.BoletaRequest;
import cl.milsabores.backend.dto.BoletaResponse;
import cl.milsabores.backend.dto.LineaRequest;
import cl.milsabores.backend.exception.RecursoNoEncontradoException;
import cl.milsabores.backend.model.Boleta;
import cl.milsabores.backend.model.Categoria;
import cl.milsabores.backend.model.Producto;
import cl.milsabores.backend.model.Rol;
import cl.milsabores.backend.model.Usuario;
import cl.milsabores.backend.repository.BoletaRepository;
import cl.milsabores.backend.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Pruebas de la compra. Los repositorios se reemplazan por dobles (@Mock):
// se prueba solo la lógica del servicio, sin base de datos.
@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;
    @Mock
    private ProductoRepository productoRepository;

    private BoletaService servicio;
    private Producto torta;
    private Producto brownie;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-12T15:00:00Z"), ZoneId.of("America/Santiago"));
        servicio = new BoletaService(boletaRepository, productoRepository, new DescuentoService(), reloj);
        Categoria categoria = new Categoria("Tortas");
        torta = producto(1L, "Torta de Manjar", 42000, 37800, 12, categoria);
        brownie = producto(2L, "Brownie", 4000, null, 1, categoria);
    }

    private Producto producto(Long id, String nombre, int precio, Integer oferta, int stock, Categoria categoria) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setPrecioOferta(oferta);
        p.setStock(stock);
        p.setCategoria(categoria);
        return p;
    }

    private BoletaRequest compra(LineaRequest... lineas) {
        return new BoletaRequest("cliente@gmail.com", "Ana Pérez", "Av. Siempre Viva 123, Santiago", List.of(lineas));
    }

    private void guardarDevuelveLaBoleta() {
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(llamada -> {
            Boleta b = llamada.getArgument(0);
            b.setId(1001L);
            return b;
        });
    }

    @Test
    void compraConStock_quedaPagada_usaPrecioDeOferta_yDescuentaStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(torta));
        guardarDevuelveLaBoleta();

        BoletaResponse boleta = servicio.crear(compra(new LineaRequest(1L, 2)), null);

        assertEquals(Boleta.PAGADA, boleta.estado());
        assertEquals(75600, boleta.subtotal());   // 2 x 37.800 (precio de oferta)
        assertEquals(75600, boleta.total());      // visitante: sin descuento
        assertEquals(10, torta.getStock());       // 12 - 2
    }

    @Test
    void compraSinStockSuficiente_quedaRechazada_yNoDescuentaStock() {
        when(productoRepository.findById(2L)).thenReturn(Optional.of(brownie));
        guardarDevuelveLaBoleta();

        BoletaResponse boleta = servicio.crear(compra(new LineaRequest(2L, 3)), null);

        assertEquals(Boleta.RECHAZADA, boleta.estado());
        assertEquals(List.of("Brownie"), boleta.faltantes());
        assertEquals(1, brownie.getStock());
    }

    @Test
    void lineasDelMismoProducto_seSuman() {
        when(productoRepository.findById(2L)).thenReturn(Optional.of(brownie));
        guardarDevuelveLaBoleta();

        // 1 + 1 = 2 brownies, pero solo hay 1 en stock
        BoletaResponse boleta = servicio.crear(compra(new LineaRequest(2L, 1), new LineaRequest(2L, 1)), null);

        assertEquals(Boleta.RECHAZADA, boleta.estado());
        assertEquals(1, boleta.lineas().size());
    }

    @Test
    void clienteMayorDe50_recibe50PorCiento() {
        Usuario cliente = new Usuario("Rosa", "rosa@gmail.com", "x", Rol.CLIENTE);
        cliente.setFechaNacimiento(LocalDate.of(1960, 1, 1));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(torta));
        guardarDevuelveLaBoleta();

        BoletaResponse boleta = servicio.crear(compra(new LineaRequest(1L, 1)), cliente);

        assertEquals(37800, boleta.subtotal());
        assertEquals(18900, boleta.descuento());
        assertEquals(18900, boleta.total());
    }

    @Test
    void productoQueNoExiste_lanza404() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.crear(compra(new LineaRequest(99L, 1)), null));
    }
}
