package cl.milsabores.backend.model;

import java.util.Arrays;

// Los tres roles del sistema. En la API se muestran con su etiqueta ("Administrador", "Vendedor", "Cliente"),
// la misma que ya usaba el frontend.
public enum Rol {
    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    CLIENTE("Cliente");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // "Vendedor" -> VENDEDOR. Acepta también "VENDEDOR" o "vendedor".
    public static Rol desdeTexto(String texto) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(texto) || r.etiqueta.equalsIgnoreCase(texto))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + texto));
    }
}
