import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Color;
import java.net.DatagramSocket;

public class Server {

    //Dichiarazione variabili
    private int port;
    private List<User> clients;
    private ServerSocket server;

    //Funzione principale di avvio
    public static void main(String[] args) throws IOException {
        //Controllo porta inserita
        int port_check = (args.length > 0 && available(Integer.parseInt(args[0]))) ? Integer.parseInt(args[0]) : findFreePort();
        new Server(port_check).run(); //Creazione del server
    }

    //Return della prima porta libera
    public static int findFreePort() {
        for (int i = 1024; i <= 49151; i++) {
            if (available(i)) {
                return i;
            }
        }
        throw new RuntimeException("Nessuna porta disponbile per il server");
    }

    //Controllo validatà della porta (Funzione presa da qualche sito)
    private static boolean available(final int port) {
        ServerSocket serverSocket = null;
        DatagramSocket dataSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            dataSocket = new DatagramSocket(port);
            dataSocket.setReuseAddress(true);
            return true;
        } catch (final IOException e) {
            return false;
        } finally {
            if (dataSocket != null) {
                dataSocket.close();
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (final IOException e) {} //Non dovrebbe poter accadare, ma doveva essere gestito
            }
        }
    }

    // Costruttore, assegnazione porta e creazione di un recipiente per tutti gli utenti
    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }

    // Avvio del server, creazione del socket
    public void run() throws IOException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println("In ascolto sulla porta: " + port);

        //Controllo infinito per nnuove richiesta in entrata
        while (true) {
            //Accettazione di un nuovo client
            Socket client = server.accept();

            //Ritorno del nome del nuovo utente che si connette
            String nickname = (new Scanner(client.getInputStream())).nextLine();

            //Pulizia nel nome per gli spazi
            nickname = nickname.replace(" ", "_");
            System.out.println("Nuovo utente: \"" + nickname + "\" IP: " + client.getInetAddress().getHostAddress());

            //Instanziamento un nuovo utente
            User newUser = new User(client, nickname);

            //Aggiunta di un nuovo utente alla lista
            this.clients.add(newUser);

            //Messaggio di benvenuto
            newUser.getOutStream().println("<b>Benvenuto:</b> " + newUser.toString());

            //Impostazione del client come Thread, per la gestione dei messaggi in arrivo
            new Thread(new UserHandler(this, newUser)).start();
        }
    }

    //Eliminazione dell'utente dalla lista
    public void removeUser(User user) {
        this.clients.remove(user);
    }

    //Invio dei messaggi a tutti gli utenti
    public void broadcastMessages(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(
                    userSender.toString() + "<span>: " + msg + "</span>");
        }
    }

    //Invio della lista di tutti gli utente agli utenti
    public void broadcastAllUsers() {
        for (User client : this.clients) {
            client.getOutStream().println(this.clients);
        }
    }

    //Invio di un messaggio a un singolo utente
    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getNickname().equals(user) && client != userSender) {
                find = true;
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() + ": " + msg);
                client.getOutStream().println("(<b>Privato</b>)" + userSender.toString() + "<span>: " + msg + "</span>");
            }
        }
        if (!find) { //Se non trova l'utente
            userSender.getOutStream().println(userSender.toString() + " -> (<b>Utente non trovato!</b>): " + msg);
        }
    }
}

//Gestione dell'utente
class UserHandler implements Runnable {

    //Sezione dichiarazione
    private Server server;
    private User user;

    //Costruttore
    public UserHandler(Server server, User user) {
        this.server = server;
        this.user = user;
        this.server.broadcastAllUsers();
    }

    //Avvio
    public void run() {
        String message;

        //Arrivo di un nuovo messaggio
        Scanner sc = new Scanner(this.user.getInputStream());
        while (sc.hasNextLine()) {
            message = sc.nextLine();
            //Gestione delle faccine personalizzate (eventuali caratteri da personalizzare vanno qui)
            message = message.replace(":)", "<img src='https://emojiapi.dev/api/v1/slightly_smiling_face/32.png'>");
            message = message.replace(":D", "<img src='https://emojiapi.dev/api/v1/grinning_face/32.png'>");
            message = message.replace(":d","<img src='https://emojiapi.dev/api/v1/grinning_face_with_big_eyes/32.png'>");
            message = message.replace(":(", "<img src='https://emojiapi.dev/api/v1/slightly_frowning_face/32.png'>");
            message = message.replace("-_-", "<img src='https://emojiapi.dev/api/v1/expressionless_face/32.png'>");
            message = message.replace(";)", "<img src='https://emojiapi.dev/api/v1/winking_face/32.png'>");
            message = message.replace(":P", "<img src='https://emojiapi.dev/api/v1/face_with_tongue/32.png'>");
            message = message.replace(":p", "<img src='https://emojiapi.dev/api/v1/squinting_face_with_tongue/32.png'>");
            message = message.replace(":o", "<img src='https://emojiapi.dev/api/v1/hushed_face/32.png'>");
            message = message.replace(":O", "<img src='https://emojiapi.dev/api/v1/face_with_open_mouth/32.png'>");

            //Gestione del messaggio privato
            if (message.charAt(0) == '@') {
                if (message.contains(" ")) {
                    System.out.println("Messaggio privato: " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate = message.substring(1, firstSpace);
                    server.sendMessageToUser(message.substring(firstSpace + 1, message.length()),user, userPrivate);
                }
            } else if (message.charAt(0) == '#') { //Gestione dei cambiamenti
                user.changeColor(message);
                //Avviso del cambiamento del colore a tutti gli utenti
                this.server.broadcastAllUsers();
            } else {
                //Aggiornamento lista utenti
                server.broadcastMessages(message, user);
            }
        }

        //Fine del thread
        server.removeUser(user);
        this.server.broadcastAllUsers();
        sc.close();
    }
}

//Classe del client
class User {
    //Dichiarazione variabili
    private static int nbUser = 0;
    private int userId;
    private PrintStream streamOut;
    private InputStream streamIn;
    private String nickname;
    private String color;

    //Construttore
    public User(Socket client, String name) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.nickname = name;
        this.userId = nbUser;
        this.color = ColorInt.getColor(this.userId);
        nbUser += 1;
    }

    //Cambia colore all'utente
    public void changeColor(String hexColor) {
        //Controllo validità del codice Hex del colore
        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
        Matcher m = colorPattern.matcher(hexColor);
        if (m.matches()) {
            Color c = Color.decode(hexColor);
            //Se il colore è troppo chiaro non cambia
            double luma = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue(); // per ITU-R BT.709
            if (luma > 160) {
                this.getOutStream().println("<b>Colore troppo chiaro!</b>");
                return;
            }
            this.color = hexColor;
            this.getOutStream().println("<b>Colore cambiato con successo!</b> " + this.toString());
            return;
        }
        this.getOutStream().println("<b>Errore nel cambiamento del colore</b>");
    }

    //Stampa di un messaggio a video nel Box
    public PrintStream getOutStream() {
        return this.streamOut;
    }

    //Input di un valore da un Box
    public InputStream getInputStream() {
        return this.streamIn;
    }

    //Ritorno del Nickname
    public String getNickname() {
        return this.nickname;
    }

    //Stampa dell'utente con il colore
    public String toString() {
        return "<u><span style='color:" + this.color+ "'>" + this.getNickname() + "</span></u>";

    }
}
//Classe gestione dei colori
class ColorInt {
    public static String[] mColors = {
            "#3079ab", // dark blue
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
            "#4d7358", // green
    };

    public static String getColor(int i) {
        return mColors[i % mColors.length];
    }
}
