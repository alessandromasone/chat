import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientGui extends Thread {
    //Sezione dichiarazioni
    final JTextPane jtextFilDiscu = new JTextPane();
    final JTextPane jtextListUsers = new JTextPane();
    final JTextField jtextInputChat = new JTextField();
    private String oldMsg = "";
    private Thread read;
    private String serverName;
    private int PORT;
    private String name;
    BufferedReader input;
    PrintWriter output;
    Socket server;

    //Interfaccia dell'utente
    public ClientGui() {
        //Variabili di inserimento da parte dell'utente (è possibile inizializzarle con dei valori preferiti)
        this.serverName = "workstation.local";
        this.PORT = 1024;
        this.name = "nickname";

        //Impostazione del font
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        //Creazione della finestra
        final JFrame jfr = new JFrame("Chat");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Modulo dello storico messaggi
        jtextFilDiscu.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setFont(font);
        jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
        jtextFilDiscu.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);

        jtextFilDiscu.setContentType("text/html");
        jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        //Modulo elenco altri utenti
        jtextListUsers.setBounds(520, 25, 156, 320);
        jtextListUsers.setEditable(false);
        jtextListUsers.setFont(font);
        jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
        JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
        jsplistuser.setBounds(520, 25, 156, 320);

        jtextListUsers.setContentType("text/html");
        jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        //Modulo Input Box
        jtextInputChat.setBounds(0, 350, 400, 50);
        jtextInputChat.setFont(font);
        jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
        jtextInputChatSP.setBounds(25, 350, 650, 50);

        //Tasto virtuale Invio del messaggio
        final JButton jsbtn = new JButton("Invio");
        jsbtn.setFont(font);
        jsbtn.setBounds(575, 410, 100, 35);

        //Tasto virtuale di disconnessione
        final JButton jsbtndeco = new JButton("Esci");
        jsbtndeco.setFont(font);
        jsbtndeco.setBounds(25, 410, 130, 35);

        //associazione tasti tasitera
        jtextInputChat.addKeyListener(new KeyAdapter() {
            //Gestione dei tasti fisici della tastiera
            public void keyPressed(KeyEvent e) {
                //Premi invio per inviare
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
                //Click Freccia su per l'ultimo messaggio inserito
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = oldMsg;
                    jtextInputChat.setText(currentMessage);
                }
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) { //Click Freccia giù per pulire l'input Box
                    String currentMessage = "";
                    jtextInputChat.setText(currentMessage);
                }
            }
        });

        //Click sul tasto virtuale Invia
        jsbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        //Visualizza connessione
        final JTextField jtfName = new JTextField(this.name);
        final JTextField jtfport = new JTextField(Integer.toString(this.PORT));
        final JTextField jtfAddr = new JTextField(this.serverName);
        final JButton jcbtn = new JButton("Connettiti");

        //Controlla attivo dei campi
        jtfName.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr, jcbtn));
        jtfport.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr, jcbtn));
        jtfAddr.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr, jcbtn));

        //Posizione dei moduli
        jcbtn.setFont(font);
        jtfAddr.setBounds(25, 380, 135, 40);
        jtfName.setBounds(375, 380, 135, 40);
        jtfport.setBounds(200, 380, 135, 40);
        jcbtn.setBounds(575, 380, 100, 40);

        //Colori di sfondo per lista utenti e storico chat
        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);

        //Aggiunta degli elementi alla finestra
        jfr.add(jcbtn);
        jfr.add(jtextFilDiscuSP);
        jfr.add(jsplistuser);
        jfr.add(jtfName);
        jfr.add(jtfport);
        jfr.add(jtfAddr);
        jfr.setVisible(true);

        //Info comandi della chat all'avvio
        appendToPane(jtextFilDiscu, 
                "<h4>Comandi e impostazioni disponibili:</h4>" +
                "<ul>"+
                    "<li><b>@nickname</b> per inviare un messaggio privato all'utente 'nickname'</li>"+
                    "<li><b>#d3961b</b> per cambiare il colore del soprannome nel codice esadecimale indicato</li>"+
                    "<li><b>;)</b> alcuni smiley sono implementati</li>"+
                    "<li><b>freccia su</b> per riprendere l'ultimo messaggio digitato</li>"+
                "</ul>"+
                "<br/>"
        );

        //Al click su connettiti
        jcbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    //Presa dei valori dai campi
                    name = jtfName.getText();
                    String port = jtfport.getText();
                    serverName = jtfAddr.getText();
                    PORT = Integer.parseInt(port);
                    
                    //Messagi di avviso per l'utente all'interno del campo storico chat
                    appendToPane(jtextFilDiscu, "<span>Connessione in corso...</span>");
                    server = new Socket(serverName, PORT);
                    appendToPane(jtextFilDiscu, "<span>Connessione stabilita con successo <img src='https://emojiapi.dev/api/v1/slightly_smiling_face/32.png'></span>");
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);

                    //Invio del nome al server
                    output.println(name);

                    //Gestione della lettura degli altri messaggi
                    read = new Read();
                    read.start();
                    jfr.remove(jtfName);
                    jfr.remove(jtfport);
                    jfr.remove(jtfAddr);
                    jfr.remove(jcbtn);
                    jfr.add(jsbtn);
                    jfr.add(jtextInputChatSP);
                    jfr.add(jsbtndeco);
                    jfr.revalidate();
                    jfr.repaint();
                    jtextFilDiscu.setBackground(Color.WHITE);
                    jtextListUsers.setBackground(Color.WHITE);
                    jfr.setTitle("Chat: " + name);
                } catch (Exception ex) {
                    appendToPane(jtextFilDiscu, "<span>Impossibile connettersi al server</span>");
                    JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
            }
            

        });

        //Attesa di azioni da parte dell'utente
        jsbtndeco.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jfr.add(jtfName);
                jfr.add(jtfport);
                jfr.add(jtfAddr);
                jfr.add(jcbtn);
                jfr.remove(jsbtn);
                jfr.remove(jtextInputChatSP);
                jfr.remove(jsbtndeco);
                jfr.revalidate();
                jfr.repaint();
                read.interrupt();
                jtextListUsers.setText(null);
                jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
                jtextListUsers.setBackground(Color.LIGHT_GRAY);
                appendToPane(jtextFilDiscu, "<span>Disconnessione effetuata.</span>");
                output.close();
            }
        });

    }

    //Aggiornamento della pagina a livello grafico
    public class TextListener implements DocumentListener {
        JTextField jtf1;
        JTextField jtf2;
        JTextField jtf3;
        JButton jcbtn;

        public TextListener(JTextField jtf1, JTextField jtf2, JTextField jtf3, JButton jcbtn) {
            this.jtf1 = jtf1;
            this.jtf2 = jtf2;
            this.jtf3 = jtf3;
            this.jcbtn = jcbtn;
        }

        public void changedUpdate(DocumentEvent e) {}

        public void removeUpdate(DocumentEvent e) {
            if (jtf1.getText().trim().equals("") ||
                    jtf2.getText().trim().equals("") ||
                    jtf3.getText().trim().equals("")) {
                jcbtn.setEnabled(false);
            } else {
                jcbtn.setEnabled(true);
            }
        }

        public void insertUpdate(DocumentEvent e) {
            if (jtf1.getText().trim().equals("") ||
                    jtf2.getText().trim().equals("") ||
                    jtf3.getText().trim().equals("")) {
                jcbtn.setEnabled(false);
            } else {
                jcbtn.setEnabled(true);
            }
        }

    }

    //Invio dei messaggi
    public void sendMessage() {
        try {
            String message = jtextInputChat.getText().trim();
            if (message.equals("")) {
                return;
            }
            this.oldMsg = message;
            output.println(message);
            jtextInputChat.requestFocus();
            jtextInputChat.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }
    
    //Corpo principale del programma
    public static void main(String[] args) throws Exception {
        new ClientGui();
    }

    //Lettura per i nuovi messaggi in arrivo
    class Read extends Thread {
        public void run() {
            String message;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message = input.readLine();
                    if (message != null) {
                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length() - 1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(message.split(", ")));
                            jtextListUsers.setText(null);
                            for (String user : ListUser) {
                                appendToPane(jtextListUsers, "@" + user);
                            }
                        } else {
                            appendToPane(jtextFilDiscu, message);
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Impossibile analizzare il  messaggio in arrivo");
                }
            }
        }
    }

    //Aggiunta di valori stringa al pannello di visualizzazione della chat
    private void appendToPane(JTextPane tp, String msg) {
        HTMLDocument doc = (HTMLDocument) tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
