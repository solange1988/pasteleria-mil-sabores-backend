package cl.milsabores.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Tabla BOLETA: la orden de compra. Tiene muchos detalles (una línea por producto).
// La relación con USUARIO es opcional (línea punteada del modelo): un visitante sin cuenta también puede comprar.
@Entity
@Table(name = "boleta")
public class Boleta {

    public static final String PAGADA = "Pagada";
    public static final String RECHAZADA = "Pago rechazado";
    public static final List<String> ESTADOS = List.of("Pagada", "En preparación", "Enviada", "Entregada", "Pago rechazado");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // null si compró un visitante sin iniciar sesión
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false, length = 150)
    private String cliente;

    @Column(nullable = false, length = 300)
    private String direccion;

    @Column(nullable = false)
    private Integer subtotal;

    @Column(nullable = false)
    private Integer porcentajeDescuento;

    @Column(nullable = false)
    private Integer descuento;

    @Column(length = 100)
    private String motivoDescuento;

    @Column(nullable = false)
    private Integer total;

    @Column(length = 100)
    private String regalo;

    @Column(nullable = false, length = 30)
    private String estado;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleBoleta> detalles = new ArrayList<>();

    public Boleta() {
    }

    // Mantiene los dos lados de la relación sincronizados
    public void agregarDetalle(DetalleBoleta detalle) {
        detalle.setBoleta(this);
        detalles.add(detalle);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Integer getSubtotal() { return subtotal; }
    public void setSubtotal(Integer subtotal) { this.subtotal = subtotal; }
    public Integer getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(Integer porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
    public Integer getDescuento() { return descuento; }
    public void setDescuento(Integer descuento) { this.descuento = descuento; }
    public String getMotivoDescuento() { return motivoDescuento; }
    public void setMotivoDescuento(String motivoDescuento) { this.motivoDescuento = motivoDescuento; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public String getRegalo() { return regalo; }
    public void setRegalo(String regalo) { this.regalo = regalo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<DetalleBoleta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleBoleta> detalles) { this.detalles = detalles; }
}
