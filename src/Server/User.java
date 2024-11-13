package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.regex.Pattern;
import java.awt.Color;

// Classe del client
public class User {
    // Dichiarazione variabili
    private static int nbUser = 0;
    private final PrintStream streamOut;
    private final InputStream streamIn;
    private final String nickname;
    private String color;

    // Pattern statico per il colore HEX
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

    // Costruttore
    public User(Socket client, String name) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream(), true);
        this.streamIn = client.getInputStream();
        this.nickname = name;

        int userId;
        synchronized (User.class) {
            userId = nbUser++;
        }

        this.color = ColorInt.getColor(userId);
    }

    // Cambia colore all'utente
    public void changeColor(String hexColor) {
        if (isValidHexColor(hexColor)) {
            Color colorCandidate = Color.decode(hexColor);
            if (isColorTooLight(colorCandidate)) {
                this.streamOut.println("<b>Colore troppo chiaro!</b>");
            } else {
                this.color = hexColor;
                this.streamOut.println("<b>Colore cambiato con successo!</b> " + this);
            }
        } else {
            this.streamOut.println("<b>Errore nel cambiamento del colore</b>");
        }
    }

    // Controllo del formato HEX
    private boolean isValidHexColor(String hexColor) {
        return HEX_COLOR_PATTERN.matcher(hexColor).matches();
    }

    // Controllo della luminosità del colore
    private boolean isColorTooLight(Color color) {
        double luma = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue(); // ITU-R BT.709
        return luma > 160;
    }

    // Accesso all'output stream
    public PrintStream getOutStream() {
        return this.streamOut;
    }

    // Accesso all'input stream
    public InputStream getInputStream() {
        return this.streamIn;
    }

    // Ritorna il nickname
    public String getNickname() {
        return this.nickname;
    }

    // Rappresentazione dell'utente con il colore
    @Override
    public String toString() {
        return "<u><span style='color:" + this.color + "'>" + this.nickname + "</span></u>";
    }

    // Metodo per chiudere i flussi
    public void close() {
        try {
            if (streamOut != null) streamOut.close();
            if (streamIn != null) streamIn.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura dei flussi per l'utente " + nickname + ": " + e.getMessage());
        }
    }
}
