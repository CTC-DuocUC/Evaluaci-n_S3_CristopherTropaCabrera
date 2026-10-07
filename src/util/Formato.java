package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

// Formato en que se escriben y se muestran las fechas: DD-MM-AAAA.
public final class Formato {

    // Con uuuu y STRICT se rechazan fechas imposibles, como 30-02-2026.
    public static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formato() {
    }

    // Convierte una fecha en texto DD-MM-AAAA. Si viene null devuelve texto vacío.
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }
}