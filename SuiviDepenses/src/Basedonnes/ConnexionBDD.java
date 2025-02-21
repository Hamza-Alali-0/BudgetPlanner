package Basedonnes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBDD {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_depenses";
    private static final String UTILISATEUR = "root"; 
    private static final String MOT_DE_PASSE = ""; 

    public static Connection getConnexion() throws SQLException {
        return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
    }
}