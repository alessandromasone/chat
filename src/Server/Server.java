package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;

public class Server {

    // Dichiarazione variabili
    private final int port;
    private final List<User> clients;

    // Funzione principale di avvio
    public static void main(String[] args) {
        try {
            int port_check = (args.length > 0 && available(Integer.parseInt(args[0]))) ? Integer.parseInt(args[0]) : findFreePort();
            new Server(port_check).run(); // Creazione del server
        } catch (IOException e) {
            System.err.println("Errore durante l'avvio del server: " + e.getMessage());
        }
    }

    // Ritorna la prima porta libera
    public static int findFreePort() {
        for (int i = 1024; i <= 49151; i++) {
            if (available(i)) {
                return i;
            }
        }
        throw new RuntimeException("Nessuna porta disponibile per il server");
    }

    // Controllo validità della porta
    private static boolean available(final int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);
             DatagramSocket dataSocket = new DatagramSocket(port)) {
            serverSocket.setReuseAddress(true);
            dataSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Costruttore, assegnazione porta e creazione di un recipiente per tutti gli utenti
    public Server(int port) {
        this.port = port;
        this.clients = Collections.synchronizedList(new ArrayList<>());
    }

    // Avvio del server, creazione del socket
    public void run() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("In ascolto sulla porta: " + port);

            // Controllo infinito per nuove richieste in entrata
            while (true) {
                Socket client = server.accept();

                // Ritorno del nome del nuovo utente che si connette
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String nickname = in.readLine().trim().replace(" ", "_");
                System.out.println("Nuovo utente: \"" + nickname + "\" IP: " + client.getInetAddress().getHostAddress());

                // Instanziamento nuovo utente
                User newUser = new User(client, nickname);

                // Aggiunta di un nuovo utente alla lista
                this.clients.add(newUser);

                // Messaggio di benvenuto
                newUser.getOutStream().println("<b>Benvenuto:</b> " + newUser);

                // Impostazione del client come Thread per la gestione dei messaggi in arrivo
                new Thread(new UserHandler(this, newUser)).start();
            }
        } catch (IOException e) {
            System.err.println("Errore nella creazione del server socket: " + e.getMessage());
        }
    }

    // Eliminazione dell'utente dalla lista
    public synchronized void removeUser(User user) {
        this.clients.remove(user);
    }

    // Invio dei messaggi a tutti gli utenti
    public void broadcastMessages(String msg, User userSender) {
        synchronized (clients) {
            for (User client : this.clients) {
                client.getOutStream().println(userSender + "<span>: " + msg + "</span>");
            }
        }
    }

    // Invio della lista di tutti gli utenti
    public void broadcastAllUsers() {
        synchronized (clients) {
            for (User client : this.clients) {
                client.getOutStream().println(this.clients);
            }
        }
    }

    // Invio di un messaggio a un singolo utente
    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        synchronized (clients) {
            for (User client : this.clients) {
                if (client.getNickname().equals(user) && client != userSender) {
                    find = true;
                    userSender.getOutStream().println(userSender + " -> " + client + ": " + msg);
                    client.getOutStream().println("(<b>Privato</b>)" + userSender + "<span>: " + msg + "</span>");
                }
            }
        }
        if (!find) {
            userSender.getOutStream().println(userSender + " -> (<b>Utente non trovato!</b>): " + msg);
        }
    }
}
