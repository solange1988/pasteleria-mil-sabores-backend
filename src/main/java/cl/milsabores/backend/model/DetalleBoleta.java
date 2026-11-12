package cl.milsabores.backend.model;

import jakarta.persistence.*;

// Tabla DETALLE_BOLETA: une BOLETA con PRODUCTO (una línea por producto comprado).
// Guarda el nombre y el precio del momento de la compra: si después el producto cambia de precio,
// la boleta antigua no se altera.
@Entity
@Table(name = "detalle_boleta")
public class DetalleBoleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boleta_id")
    private Boleta boleta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false, length = 100)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer subtotal;

    public DetalleBoleta() {
    }

    public DetalleBoleta(Producto producto, int cantidad) {
        this.producto = producto;
        this.nombreProducto = producto.getNombre();
        this.precioUnitario = producto.precioVigente();
        this.cantidad = cantidad;
        this.subtotal = precioUnitario * cantidad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Boleta getBoleta() { return boleta; }
    public void setBoleta(Boleta boleta) { this.boleta = boleta; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Integer getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Integer precioUnitario) { this.precioUnitario = precioUnitario; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getSubtotal() { return subtotal; }
    public void setSubtotal(Integer subtotal) { this.subtotal = subtotal; }
}
