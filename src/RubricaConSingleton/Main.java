package RubricaConSingleton;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static HashMap<String, GestioneDb> elencoDb = new HashMap<>(10);


    public static void main(String[] args) {
        Main myApp = new Main();
        // creo una istanza della GestioneDb e la inserisco nella mappa
        elencoDb.put("DbMario", myApp.creaInstanzaDb("root", "lucabodasca"));
        elencoDb.put("DbGuest", myApp.creaInstanzaDb("root", "lucabodasca"));
        myApp.menu();
    }

    private GestioneDb creaInstanzaDb(String user, String password) {
        GestioneDb istanza = GestioneDb.getInstance();
        istanza.getConnection(user, password);
        return istanza;
    }

    private void menu() {

        //  dbAzienda.inserimento("mario","benemar","mario@mario.it");
        //  dbAzienda.stampaVista();
        //  dbAzienda.cancellaRecord();
        //  dbAzienda.update();
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;

        do {
            System.out.println("Gestione database");
            System.out.println("1) Inserisci nuovo utente");
            System.out.println("2) Stampa utente/i");
            System.out.println("3) Modifica utente");
            System.out.println("4) Cancella utente");
            System.out.println("9) Exit");
            System.out.println("Inserisci la scelta:");
            int scelta = scanner.nextInt();
            switch (scelta) {

                case 1:
                    System.out.println("Inserisci i dati per il nuovo utente");
                    System.out.println("Username:");
                    String username = scanner.next();
                    System.out.println("Password: ");
                    String password = scanner.next();
                    System.out.println("Email:");
                    String email = scanner.next();
                    elencoDb.get("DbMario").inserimento(username, password, email);
                    break;
                case 2:
                    elencoDb.get("DbGuest").stampaVista();
                    break;

                case 3:
                    elencoDb.get("DbMario").update();
                    break;
                case 4:
                    elencoDb.get("DbGuest").cancellaRecord();
                    break;
                case 9:
                    System.out.println("Chiusura programma");
                    flag = false;
            }
        }
        while (flag);

    }
}

/*
    Applicando il principio del pattern Singleton costruiamo un database per gestione del nome utente e password ed email di persone.

    poi implementiamo i metodi:

     - per la creazione dell'utente
     - la stampa per singolo utente (con inserimento del nome_utente)
     - il cambio della password
     - cancellazione dell'utente

    tramite un menu di scelta l'utente puo' optare per
    -creare un utente
    -vista utente
    - update utente
    - Cancella utente
    - uscita
* */