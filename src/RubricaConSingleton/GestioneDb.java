package RubricaConSingleton;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Scanner;
public class GestioneDb {

    Connection conn = null;
    FileReader fileReader = null;
    Properties properties = null;

    // che il costruttore deve essere privato
    private GestioneDb(){

    }
    private static GestioneDb  istanze = null;

    /**
     *
     @return istanza dell'oggetto GestioneDb
     */
    public static GestioneDb getInstance(){
        if (istanze==null){
            istanze = new GestioneDb();
        }
        return istanze; // ritorna l'oggetto creato che non è piu' null
    }

    // gestione del database

    /**
     *
     * @param user String con username
     * @param password  String con password rilasciata dall'amministratore db
     * @return  object Connection
     */
    public  Connection getConnection(String user, String password) {

        try {
            fileReader = new FileReader("configDb.properties");
            properties =  new Properties();
            properties.load(fileReader);
            String path = properties.getProperty("url")+"://"+properties.getProperty("host")
                    +":"+properties.getProperty("porta")+"/"+properties.getProperty("database");
            // jdbc:mysql://localhost:3306/database
            // imposto i parametri per la connessione
            conn = DriverManager.getConnection(path, user, password);
            // url - utente - password

        } catch (FileNotFoundException e) {
            System.out.println("File non trovato" + e.getMessage());;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("Problemi con la connessione al db" +  e.getMessage());
        }
        if (conn!=null){
            System.out.println("Database connesso");
        }
        return conn;
    }

    // inserimento record nel db.

    /**
     *
     * @param nomeUtente Stringa con il nome utente (user) max 30 caratteri
     * @param password Stringa con la password da inserire max 15 caratteri
     * @param email Stringa con la mail max 30 caratteri
     */
    public void inserimento (String nomeUtente, String password, String email){

        // usiamo il prepared statement
        PreparedStatement preparedStatement = null;
        String nometab = properties.getProperty("tabUtenti");
        String sql = ("INSERT INTO "+nometab+" (user,password,email) values (?,?,?)");
        // System.out.println(nometab + " " + sql);
        try {
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,nomeUtente);
            preparedStatement.setString(2,password);
            preparedStatement.setString(3,email);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Non posso scrivere sul db" + e.getMessage());
        }
        finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("Errore durante la chiusura dello stream" +e.getMessage());
            }
        }
    }

    public void stampaVista (){
        // creaimo un menu scelta per selezionare la stampa
        // in base al tipo di immissione( user | pass | email) si genera una specifica query sql
        System.out.println("Stampa un utente per:");
        System.out.println("1) username");
        System.out.println("2) password");
        System.out.println("3) email");
        System.out.println("4) stampa tutti gli utenti in tabella");
        int scelta = new Scanner(System.in).nextInt();
        String sql="";
        switch (scelta){
            case 1:
                System.out.println("Inserisci l'username da cercare");
                String username = new Scanner(System.in).nextLine();
                sql = "select * from utenti where user = '"+username+"'";
                break;
            case 2:
                System.out.println("Inserisci la password da cercare");
                String password = new Scanner(System.in).nextLine();
                sql = "select * from utenti where password = '"+password+"'";
                break;
            case 3:
                System.out.println("Inserisci la mail da cercare");
                String mail = new Scanner(System.in).nextLine();
                sql = "select * from utenti where email = '"+mail+"'";
                break;
            case 4: sql = "select * from utenti";
                break;
        }
        Statement statement = null;
        ResultSet rs;
        try {
            statement= conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()){
                System.out.println("------------------------------------------------------");
                System.out.println("Record: " + rs.getRow());
                System.out.println("Nome utente: " + rs.getString(2));
                System.out.println("Password: "+ rs.getString(3));
                System.out.println("Email: " + rs.getString(4));
                System.out.println("Stampa fatta il: " + LocalDateTime.now());

            }
        } catch (SQLException e) {
            System.out.println("Errore nella creazione dell'oggetto statement." + e.getMessage());
        }
        finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Sollevata eccezione sofware" + e.getMessage());
            }
        }

    }

    /**
     Il metodo CancellaRecord elimina un record dal database.
     Richiede a scelta un inserimento di:
     nomeUtente oppure password oppure email
     */

    public void cancellaRecord (){
        System.out.println("Seleziona un utente da cancellare per:");
        System.out.println("1) username");
        System.out.println("2) password");
        System.out.println("3) email");
        System.out.println("4) cancella tutti gli utenti in tabella");
        int scelta = new Scanner(System.in).nextInt();
        String sql="";
        switch (scelta){
            case 1:
                System.out.println("Inserisci l'username da cancellare");
                String username = new Scanner(System.in).nextLine();
                sql = "select * from utenti where user = '"+username+"'";
                break;
            case 2:
                System.out.println("Inserisci la password da cancellare");
                String password = new Scanner(System.in).nextLine();
                sql = "select * from utenti where password = '"+password+"'";
                break;
            case 3:
                System.out.println("Inserisci la mail da ancellare");
                String mail = new Scanner(System.in).nextLine();
                sql = "select * from utenti where email = '"+mail+"'";
                break;
            case 4:
                System.out.println("Per cancellare tutti i record scrivi: CANCELLA");
                String conferma = new Scanner(System.in).nextLine();
                if (conferma.equals("CANCELLA")){
                    sql ="select * from utenti";
                }
                else{
                    System.out.println("Cancellazione dati non effettuata");
                }

                break;
        }

        PreparedStatement ps= null;
        try {
            ps = conn.prepareStatement(sql);
            int numrecord = ps.executeUpdate();// ritorna il nuimero del record cancellato
            System.out.println("Cancellato il record "+ numrecord+".");

        } catch (SQLException e) {
            System.out.println("Cancellazione del record non eseguita per il seguente problema: " +e.getMessage());
        }
    }

    public void update(){
        System.out.println("Seleziona un utente da aggiornare per:");
        System.out.println("1) username");
        System.out.println("2) password");
        System.out.println("3) email");

        int scelta = new Scanner(System.in).nextInt();
        String sql="";
        System.out.println("Record da aggiornare");
        System.out.println("Inserisci l'ID del record");
        int id = new Scanner(System.in).nextInt();
        // prepariamo l'istruzione sql per update
        // creiamo un array che conterra' le variabili username-password e imail da aggiornare
        String []  newDato = new String[3];
        switch (id){
            case 1:
                System.out.println("Modifica l'username sul record " + id );
                newDato[0]  = new Scanner(System.in).nextLine();
                sql = "update utenti set user = (?) where id =" + id;
                break;
            case 2:
                System.out.println("Modifica la password sul record " + id );
                newDato[1] = new Scanner(System.in).nextLine();
                sql = "update utenti set password = (?) where id =" + id;
                break;
            case 3:
                System.out.println("Modifica la mail sul record " + id );
                newDato[2] = new Scanner(System.in).nextLine();
                sql = "update utenti set mail = (?) where id =" + id;
                break;
        }
        // eseguiamo l'update con un preparedStatement
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            String selezionato = "";
            for (int i =0; i<newDato.length;i++)
                if (newDato[i]!=null)
                    preparedStatement.setString(1,newDato[i]  );
            int recordAggiornato = preparedStatement.executeUpdate();
            System.out.println("Aggiornato il record " + recordAggiornato);
        } catch (SQLException e) {
            System.out.println("Problema nella creazione dello statement. " + e.getMessage());
        }
    }

}