# JAVA-Chat

**JAVA-Chat** è un'applicazione di chat client-server implementata in Java. Il progetto include una GUI per il client e una versione basata su console per il server. L'applicazione consente agli utenti di comunicare tra loro in tempo reale tramite una connessione di rete.

## Funzionalità

- Connessione client-server per consentire la comunicazione tra gli utenti.
- Interfaccia utente intuitiva per il client, che include un elenco degli utenti online e un'area per visualizzare e inviare messaggi.
- Comandi speciali per eseguire azioni come inviare messaggi privati e cambiare il colore del nome utente.
- Supporto per emoticon personalizzate per arricchire le conversazioni.
- Gestione di più utenti contemporaneamente attraverso il server.

## Istruzioni per l'uso

1. Assicurati di avere installato Java Development Kit (JDK) sul tuo sistema.
2. Clona il repository del progetto "JAVA-Chat" sul tuo computer o scarica l'archivio ZIP e decomprimilo.
3. Apri un terminale o prompt dei comandi e posizionati nella directory del progetto.
4. Compila i file sorgente del progetto eseguendo il seguente comando:

   ```
   javac *.java
   ```

5. Avvia il server eseguendo il comando:

   ```
   java Server [porta]
   ```

   Sostituisci `[porta]` con il numero di porta desiderato per il server (es. 1024). Se non viene specificata una porta, verrà utilizzata la prima porta disponibile nel range di 1024-49151.

6. Avvia l'applicazione client eseguendo il comando:

   ```
   java ClientGui
   ```

7. Nella finestra del client, inserisci il tuo nome utente, l'indirizzo IP del server e la porta del server corrispondente. Fare clic sul pulsante "Connettiti" per avviare la connessione al server.

8. Utilizza l'area di input per digitare i messaggi che desideri inviare. Premi Invio o fai clic sul pulsante "Invio" per inviare il messaggio a tutti gli utenti. Puoi anche utilizzare comandi speciali come `@nickname` per inviare un messaggio privato a un utente specifico o `#colore` per cambiare il colore del tuo nome utente.

9. Fai clic sul pulsante "Esci" per disconnetterti dal server e chiudere l'applicazione client.

## Requisiti di sistema

- Java Development Kit (JDK) 8 o versione successiva.

## Contributi

Le segnalazioni di bug e i contributi al progetto sono benvenuti. Se desideri contribuire, apri una nuova issue o invia una richiesta pull con le tue modifiche proposte.

## Licenza

Questo progetto è concesso in licenza con i termini della [Licenza MIT](link-licenza).