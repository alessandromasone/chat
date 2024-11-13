package Server;

import java.util.List;

public class ColorInt {

    // green
    // Lista immutabile di colori
    private static final List<String> COLORS = List.of("#3079ab", // dark blue
            "#e15258", // red
            "#f9845b", // orange
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#f092b0", // pink
            "#e8d174", // yellow
            "#e39e54", // orange
            "#d64d4d", // red
            "#4d7358");

    // Ritorna il colore in base all'indice fornito, gestisce anche numeri negativi
    public static String getColor(int i) {
        return COLORS.get(Math.abs(i) % COLORS.size());
    }
}
