package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// Validaciones de los formularios. Si un dato está mal, lanzan IllegalArgumentException con un mensaje que el usuario pueda entender.
public final class Validador {

    // Largo máximo que aceptan las columnas VARCHAR(100) de la base de datos.
    private static final int LARGO_MAXIMO = 100;

    private Validador() {
    }

    // Revisa que el texto no esté vacío ni sea más largo que la columna.
    public static String textoObligatorio(String valor, String campo) {
        String limpio = valor == null ? "" : valor.trim();

        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        if (limpio.length() > LARGO_MAXIMO) {
            throw new IllegalArgumentException("El campo " + campo + " no puede superar los " + LARGO_MAXIMO + " caracteres.");
        }
        return limpio;
    }

    // Un nombre solo puede llevar letras, espacios, puntos, apóstrofes y guiones.
    public static String nombre(String valor) {
        String limpio = textoObligatorio(valor, "Nombre");

        if (!limpio.matches("\\p{L}[\\p{L} .'-]*")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras, espacios, puntos, apóstrofes y guiones.");
        }
        return limpio;
    }

    // Convierte un texto con formato DD-MM-AAAA en fecha.
    public static LocalDate fecha(String valor) {
        String limpio = textoObligatorio(valor, "Fecha");

        try {
            return LocalDate.parse(limpio, Formato.FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha no es válida. Use el formato DD-MM-AAAA, por ejemplo 05-10-2026.");
        }
    }

    // Convierte un texto con formato HH:mm o HH:mm:ss en hora.
    public static LocalTime hora(String valor) {
        String limpio = textoObligatorio(valor, "Hora");

        try {
            return LocalTime.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La hora no es válida. Use el formato HH:mm, por ejemplo 14:30.");
        }
    }

    // Revisa que el usuario haya elegido algo en un combo.
    public static <T> T seleccion(T elegido, String campo) {
        if (elegido == null) {
            throw new IllegalArgumentException("Debe seleccionar un " + campo + ". Si la lista está vacía, primero regístrelo en su pestaña.");
        }
        return elegido;
    }
}
