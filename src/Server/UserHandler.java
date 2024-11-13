package Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Gestione dell'utente
public class UserHandler implements Runnable {

    // Sezione dichiarazione
    private final Server server;
    private final User user;
    private static final Map<String, String> EMOJI_MAP;

    static {
        // Mappa per le emoji, per una gestione più pulita e scalabile
        EMOJI_MAP = new HashMap<>();
        EMOJI_MAP.put(":)", "<img src='https://emojiapi.dev/api/v1/slightly_smiling_face/32.png'>");
        EMOJI_MAP.put(":D", "<img src='https://emojiapi.dev/api/v1/grinning_face/32.png'>");
        EMOJI_MAP.put(":d", "<img src='https://emojiapi.dev/api/v1/grinning_face_with_big_eyes/32.png'>");
        EMOJI_MAP.put(":(", "<img src='https://emojiapi.dev/api/v1/slightly_frowning_face/32.png'>");
        EMOJI_MAP.put("-_-", "<img src='https://emojiapi.dev/api/v1/expressionless_face/32.png'>");
        EMOJI_MAP.put(";)", "<img src='https://emojiapi.dev/api/v1/winking_face/32.png'>");
        EMOJI_MAP.put(":P", "<img src='https://emojiapi.dev/api/v1/face_with_tongue/32.png'>");
        EMOJI_MAP.put(":p", "<img src='https://emojiapi.dev/api/v1/squinting_face_with_tongue/32.png'>");
        EMOJI_MAP.put(":o", "<img src='https://emojiapi.dev/api/v1/hushed_face/32.png'>");
        EMOJI_MAP.put(":O", "<img src='https://emojiapi.dev/api/v1/face_with_open_mouth/32.png'>");
    }

    // Costruttore
    public UserHandler(Server server, User user) {
        this.server = server;
        this.user = user;
        this.server.broadcastAllUsers();
    }

    // Avvio del thread
    public void run() {
        try (Scanner sc = new Scanner(this.user.getInputStream())) {
            while (sc.hasNextLine()) {
                String message = sc.nextLine();
                message = replaceEmojis(message);

                if (message.startsWith("@")) {
                    handlePrivateMessage(message);
                } else if (message.startsWith("#")) {
                    handleColorChange(message);
                } else {
                    server.broadcastMessages(message, user);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nella gestione del messaggio dell'utente " + user.getNickname() + ": " + e.getMessage());
        } finally {
            try {
                disconnectUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Gestisce il cambiamento di colore
    private void handleColorChange(String message) {
        user.changeColor(message);
        server.broadcastAllUsers();
    }

    // Gestisce il messaggio privato
    private void handlePrivateMessage(String message) {
        int firstSpace = message.indexOf(" ");
        if (firstSpace > 1) {
            String userPrivate = message.substring(1, firstSpace);
            String privateMessage = message.substring(firstSpace + 1);
            server.sendMessageToUser(privateMessage, user, userPrivate);
        } else {
            user.getOutStream().println("<b>Formato messaggio privato non corretto!</b>");
        }
    }

    // Sostituisce le faccine nel messaggio con le emoji corrispondenti
    private String replaceEmojis(String message) {
        for (Map.Entry<String, String> entry : EMOJI_MAP.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    // Gestisce la disconnessione dell'utente
    private void disconnectUser() throws IOException {
        server.removeUser(user);
        server.broadcastAllUsers();
        user.close();
        System.out.println("Utente " + user.getNickname() + " disconnesso.");
    }
}
