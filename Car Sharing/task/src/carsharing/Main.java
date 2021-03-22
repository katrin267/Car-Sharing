package carsharing;

import java.util.Scanner;

public class Main {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
//    static final String DB_URL = "jdbc:h2:~/test";

    //  Database credentials
//    static final String USER = "sa";
//    static final String PASS = "";


    public static void main(String[] args) {


        String dbName = "default";
        if (args.length > 1) {
            if (args[0].equals("-databaseFileName")) {
                dbName = args[1];
            }
        }
        Menu menu = new Menu(dbName);
        menu.mainMenu();
    }
}