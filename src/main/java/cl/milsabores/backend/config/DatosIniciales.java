package cl.milsabores.backend.config;

import cl.milsabores.backend.model.Categoria;
import cl.milsabores.backend.model.Producto;
import cl.milsabores.backend.model.Rol;
import cl.milsabores.backend.model.Usuario;
import cl.milsabores.backend.repository.CategoriaRepository;
import cl.milsabores.backend.repository.ProductoRepository;
import cl.milsabores.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

// Carga los datos iniciales la PRIMERA vez que arranca la aplicación (si las tablas están vacías).
// Como MySQL guarda los datos, en los siguientes arranques no hace nada.
@Component
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(CategoriaRepository categoriaRepository, ProductoRepository productoRepository,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            crearUsuario("Administrador Pastelería", "admin@duoc.cl", "admin123", Rol.ADMINISTRADOR);
            crearUsuario("Vendedor Pastelería", "vendedor@duoc.cl", "vende123", Rol.VENDEDOR);
            crearUsuario("Cliente de Prueba", "cliente@gmail.com", "cliente123", Rol.CLIENTE);
        }
        if (productoRepository.count() == 0) {
            cargarProductos();
        }
    }

    private void crearUsuario(String nombre, String correo, String contrasena, Rol rol) {
        usuarioRepository.save(new Usuario(nombre, correo, passwordEncoder.encode(contrasena), rol));
    }

    private final Map<String, Categoria> categorias = new HashMap<>();

    private void producto(String nombre, String categoria, int precio, Integer oferta, int stock, String imagen,
                          String descripcion) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setCategoria(categorias.computeIfAbsent(categoria, n -> categoriaRepository.findByNombreIgnoreCase(n)
                .orElseGet(() -> categoriaRepository.save(new Categoria(n)))));
        p.setPrecio(precio);
        p.setPrecioOferta(oferta);
        p.setStock(stock);
        p.setImagen(imagen);
        p.setDescripcion(descripcion);
        productoRepository.save(p);
    }

    // Los mismos 16 productos que tenía el frontend en data/productos.js
    private void cargarProductos() {
        producto("Torta Cuadrada de Chocolate", "Tortas Cuadradas", 45000, null, 10, "/img/torta-chocolate.jpg",
                "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas.");
        producto("Torta Cuadrada de Frutas", "Tortas Cuadradas", 50000, 42500, 8, "/img/torta-frutas.jpg",
                "Mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla.");
        producto("Torta Circular de Vainilla", "Tortas Circulares", 40000, null, 6, "/img/torta-vainilla.jpg",
                "Bizcocho de vainilla clásico relleno con crema pastelera y glaseado dulce.");
        producto("Torta Circular de Manjar", "Tortas Circulares", 42000, 37800, 12, "/img/torta-manjar.jpg",
                "Torta tradicional chilena con manjar y nueces, un deleite clásico.");
        producto("Mousse de Chocolate", "Postres Individuales", 5000, null, 30, "/img/mousse-chocolate.jpg",
                "Postre individual cremoso y suave, hecho con chocolate de alta calidad.");
        producto("Tiramisú Clásico", "Postres Individuales", 5500, null, 25, "/img/tiramisu.jpg",
                "Postre italiano individual con capas de café, mascarpone y cacao.");
        producto("Torta Sin Azúcar de Naranja", "Sin Azúcar", 48000, null, 4, "/img/torta-naranja.jpg",
                "Torta ligera y deliciosa, endulzada naturalmente.");
        producto("Cheesecake Sin Azúcar", "Sin Azúcar", 47000, null, 0, "/img/cheesecake.jpg",
                "Suave y cremoso, una opción perfecta para disfrutar sin culpa.");
        producto("Empanada de Manzana", "Pastelería Tradicional", 3000, 2400, 40, "/img/empanada-manzana.jpg",
                "Pastelería tradicional rellena de manzanas especiadas.");
        producto("Tarta de Santiago", "Pastelería Tradicional", 6000, null, 15, "/img/tarta-santiago.jpg",
                "Tradicional tarta española hecha con almendras, azúcar y huevos.");
        producto("Brownie Sin Gluten", "Sin Gluten", 4000, null, 20, "/img/brownie.jpg",
                "Rico y denso, perfecto para quienes evitan el gluten sin sacrificar sabor.");
        producto("Pan Sin Gluten", "Sin Gluten", 3500, 2800, 18, "/img/pan-sin-gluten.jpg",
                "Suave y esponjoso, ideal para sándwiches o para acompañar cualquier comida.");
        producto("Torta Vegana de Chocolate", "Veganos", 50000, null, 5, "/img/torta-vegana.jpg",
                "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal.");
        producto("Galletas Veganas de Avena", "Veganos", 4500, 3600, 35, "/img/galletas-avena.jpg",
                "Crujientes y sabrosas, excelente opción para un snack saludable y vegano.");
        producto("Torta Especial de Cumpleaños", "Tortas Especiales", 55000, null, 7, "/img/torta-cumpleanos.jpg",
                "Diseñada especialmente para celebraciones, personalizable con mensajes únicos.");
        producto("Torta Especial de Boda", "Tortas Especiales", 60000, null, 3, "/img/torta-boda.jpg",
                "Elegante y deliciosa, diseñada para ser el centro de atención en cualquier boda.");
    }
}
