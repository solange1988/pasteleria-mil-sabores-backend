package cl.milsabores.backend.model;

import jakarta.persistence.*;

// Tabla PRODUCTO: cada producto pertenece a una categoría (relación muchos a uno)
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // Precios en pesos chilenos (sin decimales)
    @Column(nullable = false)
    private Integer precio;

    // Opcional: si es menor que el precio normal, el producto está en oferta
    private Integer precioOferta;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 200)
    private String imagen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public Producto() {
    }

    // El precio que se cobra: el de oferta si es válido; si no, el normal
    public int precioVigente() {
        return enOferta() ? precioOferta : precio;
    }

    public boolean enOferta() {
        return precioOferta != null && precioOferta > 0 && precioOferta < precio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public Integer getPrecioOferta() { return precioOferta; }
    public void setPrecioOferta(Integer precioOferta) { this.precioOferta = precioOferta; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
