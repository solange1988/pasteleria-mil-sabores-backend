package cl.milsabores.backend.dto;

import cl.milsabores.backend.model.Boleta;
import cl.milsabores.backend.model.DetalleBoleta;

import java.util.List;

// Misma forma que la "orden" del frontend: { numero, fecha, correo, cliente, direccion, lineas, subtotal,
// descuento, motivo, total, regalo, estado }. "faltantes" lista los productos sin stock suficiente.
public record BoletaResponse(
        Long numero,
        String fecha,
        String correo,
        String cliente,
        String direccion,
        List<Linea> lineas,
        Integer subtotal,
        Integer porcentaje,
        Integer descuento,
        String motivo,
        Integer total,
        String regalo,
        String estado,
        List<String> faltantes) {

    public record Linea(Long id, String nombre, Integer precio, Integer cantidad, Integer subtotal) {

        static Linea de(DetalleBoleta d) {
            return new Linea(d.getProducto().getId(), d.getNombreProducto(), d.getPrecioUnitario(),
                    d.getCantidad(), d.getSubtotal());
        }
    }

    public static BoletaResponse de(Boleta b) {
        return de(b, List.of());
    }

    public static BoletaResponse de(Boleta b, List<String> faltantes) {
        return new BoletaResponse(b.getId(), b.getFecha().toString(), b.getCorreo(), b.getCliente(), b.getDireccion(),
                b.getDetalles().stream().map(Linea::de).toList(),
                b.getSubtotal(), b.getPorcentajeDescuento(), b.getDescuento(), nulo(b.getMotivoDescuento()),
                b.getTotal(), nulo(b.getRegalo()), b.getEstado(), faltantes);
    }

    private static String nulo(String texto) {
        return texto == null ? "" : texto;
    }
}
