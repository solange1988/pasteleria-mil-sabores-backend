package cl.milsabores.backend.service;

import cl.milsabores.backend.dto.BoletaRequest;
import cl.milsabores.backend.dto.BoletaResponse;
import cl.milsabores.backend.dto.LineaRequest;
import cl.milsabores.backend.exception.RecursoNoEncontradoException;
import cl.milsabores.backend.exception.ReglaNegocioException;
import cl.milsabores.backend.model.Boleta;
import cl.milsabores.backend.model.DetalleBoleta;
import cl.milsabores.backend.model.Producto;
import cl.milsabores.backend.model.Usuario;
import cl.milsabores.backend.repository.BoletaRepository;
import cl.milsabores.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Lógica de la compra (checkout). Es la parte más importante del negocio:
// 1. Los precios se leen de la base de datos (no se confía en lo que manda el navegador).
// 2. Si falta stock de algún producto, la boleta se guarda como "Pago rechazado" y NO se descuenta stock.
// 3. Si hay stock, se aplica el descuento del usuario, se descuenta el stock y la boleta queda "Pagada".
// Todo ocurre en una sola transacción: o se guarda todo, o nada.
@Service
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;
    private final DescuentoService descuentoService;
    private final Clock reloj;

    // @Autowired: hay dos constructores, este es el que usa Spring
    @Autowired
    public BoletaService(BoletaRepository boletaRepository, ProductoRepository productoRepository,
                         DescuentoService descuentoService) {
        this(boletaRepository, productoRepository, descuentoService, Clock.systemDefaultZone());
    }

    // Constructor para las pruebas: permite fijar la fecha
    BoletaService(BoletaRepository boletaRepository, ProductoRepository productoRepository,
                  DescuentoService descuentoService, Clock reloj) {
        this.boletaRepository = boletaRepository;
        this.productoRepository = productoRepository;
        this.descuentoService = descuentoService;
        this.reloj = reloj;
    }

    // usuario puede ser null (compra de un visitante sin sesión)
    @Transactional
    public BoletaResponse crear(BoletaRequest datos, Usuario usuario) {
        Boleta boleta = new Boleta();
        boleta.setFecha(LocalDateTime.now(reloj));
        boleta.setUsuario(usuario);
        boleta.setCorreo(datos.correo().trim());
        boleta.setCliente(datos.cliente().trim());
        boleta.setDireccion(datos.direccion().trim());

        // Junta las líneas del mismo producto y arma el detalle con el precio vigente de la base de datos
        List<String> faltantes = new ArrayList<>();
        for (Map.Entry<Long, Integer> linea : agruparPorProducto(datos.lineas()).entrySet()) {
            Producto producto = productoRepository.findById(linea.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + linea.getKey()));
            if (producto.getStock() < linea.getValue()) {
                faltantes.add(producto.getNombre());
            }
            boleta.agregarDetalle(new DetalleBoleta(producto, linea.getValue()));
        }

        int subtotal = boleta.getDetalles().stream().mapToInt(DetalleBoleta::getSubtotal).sum();
        LocalDate hoy = LocalDate.now(reloj);
        DescuentoService.Descuento descuento = descuentoService.descuentoPara(usuario, hoy);
        int montoDescuento = (int) Math.round(subtotal * descuento.porcentaje() / 100.0);

        boleta.setSubtotal(subtotal);
        boleta.setPorcentajeDescuento(descuento.porcentaje());
        boleta.setDescuento(montoDescuento);
        boleta.setMotivoDescuento(descuento.motivo());
        boleta.setTotal(subtotal - montoDescuento);
        boleta.setRegalo(descuentoService.tieneTortaGratis(usuario, hoy) ? DescuentoService.REGALO_CUMPLEANOS : null);

        if (faltantes.isEmpty()) {
            // Pago aceptado: se descuenta el stock de cada producto
            boleta.getDetalles().forEach(d -> d.getProducto().setStock(d.getProducto().getStock() - d.getCantidad()));
            boleta.setEstado(Boleta.PAGADA);
        } else {
            boleta.setEstado(Boleta.RECHAZADA);
        }
        return BoletaResponse.de(boletaRepository.save(boleta), faltantes);
    }

    @Transactional(readOnly = true)
    public List<BoletaResponse> listar() {
        return boletaRepository.findAllByOrderByIdAsc().stream().map(BoletaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<BoletaResponse> listarDe(Usuario usuario) {
        return boletaRepository.findByUsuarioIdOrderByIdAsc(usuario.getId()).stream().map(BoletaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public BoletaResponse obtener(Long numero) {
        return BoletaResponse.de(buscar(numero));
    }

    @Transactional
    public BoletaResponse cambiarEstado(Long numero, String estado) {
        if (!Boleta.ESTADOS.contains(estado)) {
            throw new ReglaNegocioException("Estado no válido. Usa uno de: " + String.join(", ", Boleta.ESTADOS));
        }
        Boleta boleta = buscar(numero);
        boleta.setEstado(estado);
        return BoletaResponse.de(boletaRepository.save(boleta));
    }

    @Transactional
    public void eliminar(Long numero) {
        boletaRepository.delete(buscar(numero));
    }

    private Boleta buscar(Long numero) {
        return boletaRepository.findById(numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la boleta " + numero));
    }

    private static Map<Long, Integer> agruparPorProducto(List<LineaRequest> lineas) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        lineas.forEach(l -> cantidades.merge(l.productoId(), l.cantidad(), Integer::sum));
        return cantidades;
    }
}
