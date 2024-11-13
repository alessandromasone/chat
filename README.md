# Java Chat

Un'applicazione di chat multicliente scritta in Java che consente la comunicazione in tempo reale tra più utenti. Gli utenti possono connettersi al server, inviare messaggi pubblici o privati, cambiare il proprio colore del testo e utilizzare emoji nei messaggi.

## Funzionalità

- **Connessione multiutente**: Il server supporta più utenti connessi simultaneamente.
- **Messaggi pubblici**: Gli utenti possono inviare messaggi che vengono broadcastati a tutti i partecipanti.
- **Messaggi privati**: Gli utenti possono inviare messaggi privati ad altri utenti specificando il nickname.
- **Cambio colore**: Ogni utente può cambiare il colore del proprio nome nella chat utilizzando un codice colore HEX.
- **Emoji**: Supporta l'inserimento di emoji nei messaggi tramite una sintassi semplice (es. `:)`, `:D`, `:(`, ecc.).
- **Gestione degli utenti**: Ogni utente ha un nickname unico, un colore personalizzabile e può essere disconnesso dal server.

## Requisiti

- Java 8 o superiore.
- IntelliJ IDEA o un altro IDE compatibile con Java.
- Connessione di rete attiva per il server e i client.

## Setup e Avvio del Progetto

### 1. Clona il repository

Clona il repository nel tuo ambiente locale:

```bash
git clone https://github.com/alessandromasone/java-chat.git
```

### 2. Importa il progetto in IntelliJ IDEA

1. Apri IntelliJ IDEA.
2. Seleziona **"Open"** e scegli la cartella del progetto appena clonato.
3. IntelliJ IDEA rileverà automaticamente il progetto come un progetto Java.

### 3. Compilazione e Esecuzione

Il progetto è pronto per essere eseguito direttamente da IntelliJ IDEA. Segui questi passaggi per avviare il server:

1. Apri la classe **`Server`** che si trova nel package `Server`.
2. Esegui la classe come una normale applicazione Java. Puoi farlo cliccando con il tasto destro sulla classe e selezionando **"Run 'Server.main()'"** o utilizzando il comando di esecuzione in IntelliJ.

### 4. Avvio del Server

Il server ascolta su una porta specificata. Se non fornisci una porta, il server cercherà automaticamente una porta libera tra quelle disponibili.

Puoi avviare il server con un comando come questo:

```bash
java -cp out/production/java-chat Server.Server <porta>
```

### 5. Connettersi al Server

Una volta che il server è in esecuzione, i client possono connettersi utilizzando un'applicazione client (ad esempio tramite terminale o un'applicazione personalizzata). Ogni client dovrà inviare un nickname al server quando si connette.

## Esempio di utilizzo

- **Messaggio pubblico**: Un utente può inviare un messaggio a tutti con:

  ```
  Ciao a tutti!
  ```

  Questo messaggio verrà broadcastato a tutti gli utenti connessi.

- **Messaggio privato**: Per inviare un messaggio privato a un altro utente, usa la sintassi:

  ```
  @nickname Ciao, come stai?
  ```

  Dove `nickname` è il nome dell'utente destinatario.

- **Cambiamento del colore**: Un utente può cambiare il proprio colore utilizzando:

  ```
  #HEX_COLOR
  ```

  Ad esempio:

  ```
  #FF5733
  ```

  Questo cambierà il colore del nickname dell'utente (se il colore è valido e abbastanza scuro).

- **Emoji**: Usa i codici emoji per includere immagini nei messaggi. Ad esempio:

  ```
  Ciao :) Come va?
  ```

  Verrà sostituito con l'emoji di un sorriso.

## Struttura del progetto

Il progetto è organizzato nelle seguenti classi principali:

- **Server**: Gestisce le connessioni in ingresso, la gestione degli utenti e la broadcast dei messaggi.
- **User**: Rappresenta un singolo utente con il suo nickname, colore e i flussi di input/output.
- **UserHandler**: Gestisce la logica di comunicazione per ogni utente, inclusi i messaggi privati, il cambiamento di colore e l'invio di messaggi.
- **ColorInt**: Una classe di utilità per assegnare un colore unico a ogni utente.
- **Emoji**: Supporta la sostituzione di codici emoji con le immagini corrispondenti.

## Contribuire

Se desideri contribuire a questo progetto:

1. Fai un fork del repository.
2. Crea un nuovo branch per la tua funzionalità (`git checkout -b feature/nome-funzionalita`).
3. Fai delle modifiche e assicurati di aggiungere test o documentazione dove necessario.
4. Fai un commit delle tue modifiche (`git commit -m 'Aggiungi una funzionalità'`).
5. Fai il push del tuo branch (`git push origin feature/nome-funzionalita`).
6. Crea una Pull Request per le modifiche.

## Licenza

Questo progetto è sotto la licenza MIT. Consulta il file `LICENSE` per maggiori dettagli.